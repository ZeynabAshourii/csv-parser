package org.example;

public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    private String tableName;

    public DatabaseConfig(String url, String username, String password, String tableName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.tableName = tableName;
    }
    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getTableName() { return tableName; }
    public String getErrorTableName() { return tableName + "_errors"; }
}