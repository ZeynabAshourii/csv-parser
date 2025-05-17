package org.example;

import java.util.List;

public class SchemaConfig {
    private List<String> columnNames;
    private List<String> columnTypes;
    private List<Boolean> columnsToImport;

    public SchemaConfig(List<String> columnNames, List<String> columnTypes, List<Boolean> columnsToImport) {
        if (columnNames.size() != columnTypes.size() || columnNames.size() != columnsToImport.size()) {
            throw new IllegalArgumentException("All lists must have the same size");
        }
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.columnsToImport = columnsToImport;
    }
    public List<String> getColumnNames() { return columnNames; }
    public List<String> getColumnTypes() { return columnTypes; }
    public List<Boolean> getColumnsToImport() { return columnsToImport; }
    public int getColumnCount() { return columnNames.size(); }
}