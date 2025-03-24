package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/JDBC_likeBook_db?useSSL=false&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Root.1234";

    private DbConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try(Connection connection=getConnection()){
            System.out.println("Connection to MySQL established successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to connect to MySQL: " + e.getMessage());
        }
    }

}
