package org.example;

public class CSVData {
    private String[] values;
    private String fileName;
    private int lineNumber;

    public CSVData(String[] values, String fileName, int lineNumber) {
        this.values = values;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }
    public String[] getValues() { return values; }
    public String getFileName() { return fileName; }
    public int getLineNumber() { return lineNumber; }
}