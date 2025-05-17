package org.example;

import java.sql.*;

public class DatabaseManager {
    private Connection connection;
    private DatabaseConfig dbConfig;
    private SchemaConfig schemaConfig;

    public DatabaseManager(DatabaseConfig dbConfig, SchemaConfig schemaConfig) throws SQLException {
        this.dbConfig = dbConfig;
        this.schemaConfig = schemaConfig;
        this.connection = DriverManager.getConnection(
                dbConfig.getUrl(),
                dbConfig.getUsername(),
                dbConfig.getPassword()
        );
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        dropTableIfExists(dbConfig.getTableName());
        dropTableIfExists(dbConfig.getErrorTableName());
        createMainTable();
        createErrorTable();
    }

    private void dropTableIfExists(String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS " + tableName);
        }
    }

    private void createMainTable() throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE ")
                .append(dbConfig.getTableName())
                .append(" (");

        for (int i = 0; i < schemaConfig.getColumnCount(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(schemaConfig.getColumnNames().get(i))
                    .append(" ")
                    .append(schemaConfig.getColumnTypes().get(i));
        }
        sql.append(")");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql.toString());
        }
    }

    private void createErrorTable() throws SQLException {
        String sql = "CREATE TABLE " + dbConfig.getErrorTableName() + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "file_name VARCHAR(255), " +
                "line_number INT, " +
                "error_message TEXT, " +
                "raw_data TEXT, " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insertData(CSVData csvData) throws SQLException {
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        boolean first = true;
        for (int i = 0; i < schemaConfig.getColumnCount(); i++) {
            if (!schemaConfig.getColumnsToImport().get(i)) {
                continue;
            }
            if (!first) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(schemaConfig.getColumnNames().get(i));
            placeholders.append("?");
            first = false;
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                dbConfig.getTableName(), columns, placeholders);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            for (int i = 0; i < schemaConfig.getColumnCount(); i++) {
                if (!schemaConfig.getColumnsToImport().get(i)) {
                    continue;
                }
                String type = schemaConfig.getColumnTypes().get(i).toUpperCase();
                String value = csvData.getValues()[i];
                if (value == null || value.isEmpty()) {
                    stmt.setNull(paramIndex++, getSqlType(type));
                    continue;
                }
                switch (type) {
                    case "INT":
                        stmt.setInt(paramIndex++, Integer.parseInt(value));
                        break;
                    case "FLOAT":
                        stmt.setFloat(paramIndex++, Float.parseFloat(value));
                        break;
                    case "DOUBLE":
                        stmt.setDouble(paramIndex++, Double.parseDouble(value));
                        break;
                    case "DATE":
                        stmt.setDate(paramIndex++, java.sql.Date.valueOf(value));
                        break;
                    case "DATETIME":
                    case "TIMESTAMP":
                        stmt.setTimestamp(paramIndex++, java.sql.Timestamp.valueOf(value));
                        break;
                    default:
                        stmt.setString(paramIndex++, value);
                }
            }
            stmt.executeUpdate();
        }
    }

    public void logError(ErrorRecord error) throws SQLException {
        String sql = "INSERT INTO " + dbConfig.getErrorTableName() +
                " (file_name, line_number, error_message, raw_data) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, error.getFileName());
            stmt.setInt(2, error.getLineNumber());
            stmt.setString(3, error.getErrorMessage());
            stmt.setString(4, error.getRawData());
            stmt.executeUpdate();
        }
    }

    private int getSqlType(String type) {
        switch (type.toUpperCase()) {
            case "INT": return Types.INTEGER;
            case "FLOAT": return Types.FLOAT;
            case "DOUBLE": return Types.DOUBLE;
            case "DATE": return Types.DATE;
            case "DATETIME":
            case "TIMESTAMP": return Types.TIMESTAMP;
            default: return Types.VARCHAR;
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}