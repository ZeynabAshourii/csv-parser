package org.example;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("CSV Parser to MySQL");
        System.out.print("Enter database URL : ");
        //jdbc:mysql://localhost:3306/mytestdb
        String dbUrl = scanner.nextLine();
        System.out.print("Enter database username: ");
        //root
        String dbUser = scanner.nextLine();
        System.out.print("Enter database password: ");
        //1383z1392h
        String dbPassword = scanner.nextLine();
        System.out.print("Enter table name: ");
        String tableName = scanner.nextLine();
        DatabaseConfig dbConfig = new DatabaseConfig(dbUrl, dbUser, dbPassword, tableName);
        List<String> columnNames = CLIUtils.promptForList(
                scanner,
                "Enter column names separated by commas (e.g., id,name,age):",
                ","
        );
        List<String> columnTypes = CLIUtils.promptForList(
                scanner,
                "Enter column types for each column separated by commas (e.g., INT,VARCHAR(255),FLOAT):",
                ","
        );
        List<String> importFlags = CLIUtils.promptForList(
                scanner,
                "Enter which columns to import (true/false) separated by commas (e.g., true,true,false):",
                ","
        );
        SchemaConfig schemaConfig = new SchemaConfig(
                columnNames,
                columnTypes,
                CLIUtils.convertToBooleanList(importFlags)
        );
        List<String> paths = CLIUtils.promptForList(
                scanner,
                "Enter file or directory paths separated by commas:",
                ","
        );

        try {
            CSVParser csvParser = new CSVParser();
            DatabaseManager dbManager = new DatabaseManager(dbConfig, schemaConfig);
            DataValidator dataValidator = new DataValidator(schemaConfig);
            FileProcessor fileProcessor = new FileProcessor(csvParser, dataValidator, dbManager);

            List<File> filesToProcess = fileProcessor.findCSVFiles(paths);
            for (File file : filesToProcess) {
                System.out.println("Processing file: " + file.getName());
                fileProcessor.processFile(file);
            }
            System.out.println("Processing completed successfully!");
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}