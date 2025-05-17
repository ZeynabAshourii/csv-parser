package org.example;

import java.sql.Timestamp;

public class ErrorRecord {
    private String fileName;
    private int lineNumber;
    private String errorMessage;
    private String rawData;
    private Timestamp timestamp;

    public ErrorRecord(String fileName, int lineNumber, String errorMessage, String rawData) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.errorMessage = errorMessage;
        this.rawData = rawData;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public String getFileName() { return fileName; }
    public int getLineNumber() { return lineNumber; }
    public String getErrorMessage() { return errorMessage; }
    public String getRawData() { return rawData; }
    public Timestamp getTimestamp() { return timestamp; }
}