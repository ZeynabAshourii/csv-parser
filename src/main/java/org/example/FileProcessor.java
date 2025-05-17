package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {
    private CSVParser csvParser;
    private DataValidator dataValidator;
    private DatabaseManager dbManager;

    public FileProcessor(CSVParser csvParser, DataValidator dataValidator, DatabaseManager dbManager) {
        this.csvParser = csvParser;
        this.dataValidator = dataValidator;
        this.dbManager = dbManager;
    }

    public void processFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1) {
                    continue;
                }
                try {
                    String[] values = csvParser.parseLine(line);
                    CSVData csvData = new CSVData(values, file.getName(), lineNumber);
                    dataValidator.validate(csvData);
                    dbManager.insertData(csvData);
                } catch (DataValidator.DataValidationException e) {
                    ErrorRecord error = new ErrorRecord(
                            file.getName(),
                            lineNumber,
                            e.getMessage(),
                            line
                    );
                    dbManager.logError(error);
                } catch (Exception e) {
                    ErrorRecord error = new ErrorRecord(
                            file.getName(),
                            lineNumber,
                            "Processing error: " + e.getMessage(),
                            line
                    );
                    dbManager.logError(error);
                }
            }
        } catch (IOException | SQLException e) {
            System.err.println("Error processing file " + file.getName() + ": " + e.getMessage());
        }
    }

    public List<File> findCSVFiles(List<String> paths) {
        List<File> csvFiles = new ArrayList<>();
        for (String path : paths) {
            File file = new File(path);
            if (file.isDirectory()) {
                File[] files = file.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
                if (files != null) {
                    for (File f : files) {
                        csvFiles.add(f);
                    }
                }
            } else if (file.isFile() && file.getName().toLowerCase().endsWith(".csv")) {
                csvFiles.add(file);
            }
        }
        return csvFiles;
    }
}