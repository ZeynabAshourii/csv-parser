package org.example;

public class DataValidator {
    private SchemaConfig schemaConfig;

    public DataValidator(SchemaConfig schemaConfig) {
        this.schemaConfig = schemaConfig;
    }

    public void validate(CSVData csvData) throws DataValidationException {
        String[] values = csvData.getValues();
        if (values.length != schemaConfig.getColumnCount()) {
            throw new DataValidationException(
                    String.format("Expected %d columns, found %d", schemaConfig.getColumnCount(), values.length)
            );
        }
        for (int i = 0; i < values.length; i++) {
            if (!schemaConfig.getColumnsToImport().get(i)) {
                continue;
            }
            String type = schemaConfig.getColumnTypes().get(i);
            String value = values[i];
            if (value == null || value.isEmpty()) {
                continue;
            }
            try {
                validateDataType(type, value);
            } catch (DataValidationException e) {
                throw new DataValidationException(
                        String.format("Column '%s': %s", schemaConfig.getColumnNames().get(i), e.getMessage())
                );
            }
        }
    }

    private void validateDataType(String type, String value) throws DataValidationException {
        try {
            switch (type.toUpperCase()) {
                case "INT":
                    Integer.parseInt(value);
                    break;
                case "FLOAT":
                    Float.parseFloat(value);
                    break;
                case "DOUBLE":
                    Double.parseDouble(value);
                    break;
                case "DATE":
                    java.sql.Date.valueOf(value);
                    break;
                case "DATETIME":
                case "TIMESTAMP":
                    java.sql.Timestamp.valueOf(value);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new DataValidationException(
                    String.format("Invalid %s value: '%s'", type, value)
            );
        }
    }

    public static class DataValidationException extends Exception {
        public DataValidationException(String message) {
            super(message);
        }
    }
}