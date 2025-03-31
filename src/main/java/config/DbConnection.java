package config;

import main.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbConnection {
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static final Logger logger = AppLogger.getLogger(DbConnection.class);
    static {
        loadProperties("src/main/resources/config.properties");
    }

    private DbConnection() {
    }

    private static void loadProperties(String filePath) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            props.load(fis);
            URL = props.getProperty("URL");
            USER = props.getProperty("USER");
            PASSWORD = props.getProperty("PASSWORD");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read database config", e);
            System.err.println("Failed to read database config");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
        }
        if (URL == null || URL.isBlank()) {
            throw new SQLException("Missing DB URL. Please check your config.properties file.");
        }
        if (USER == null || USER.isBlank()) {
            throw new SQLException("Missing DB username. Please check your config.properties file.");
        }
        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException("Missing DB password. Please check your config.properties file.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection connection = getConnection()) {
            System.out.println("Connection to MySQL established successfully!");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to connect to MySQL: ", e);
            System.err.println("Failed to connect to MySQL!");
        }
    }

}
