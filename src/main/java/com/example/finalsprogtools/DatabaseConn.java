package com.example.finalsprogtools;
import java.sql.*;

public class DatabaseConn {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/202210627_lab7";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
