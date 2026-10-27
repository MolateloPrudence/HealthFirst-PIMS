package pims_project.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    
    private static final String URL =
            "jdbc:mysql://localhost:3306/pims_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Prudy1587@"; 

    // ==========================================

    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Quick test
     */
    public static void main(String[] args) {
        System.out.println("Testing DBConnection...");
        try (Connection conn = getConnection()) {
            System.out.println("Connected to: " + conn.getCatalog());
        } catch (SQLException e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }
}