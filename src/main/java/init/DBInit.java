package init;

import model.MoodNameEnum;
import model.UserRoleEnum;

import java.sql.*;

public class DBInit {
    private final Connection connection;

    public DBInit(Connection connection) {
        this.connection = connection;
    }

    public void initMoods() throws SQLException {
        String query = "INSERT IGNORE INTO moods (name, description) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (MoodNameEnum mood : MoodNameEnum.values()) {
                stmt.setString(1, mood.name());
                stmt.setString(2, getDescriptionForMood(mood));
                stmt.executeUpdate();
            }
            System.out.println("Mood table initialized with enum values.");
        } catch (SQLException e) {
            System.err.println("Error initializing user roles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initializeUserRoles() {
        String query = "INSERT IGNORE INTO user_roles (user_role) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (UserRoleEnum role : UserRoleEnum.values()) {
                System.out.println("Inserting role: " + role.name());
                stmt.setString(1, role.name());
                stmt.executeUpdate();
            }
            System.out.println("User roles initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing user roles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getDescriptionForMood(MoodNameEnum mood) {
        return switch (mood) {
            case HAPPY -> "Feeling joyful and positive.";
            case SAD -> "Feeling down or unhappy.";
            case INSPIRED -> "Feeling excited.";
        };
    }

    public void createAllTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            username VARCHAR(50) NOT NULL UNIQUE,
                            password VARCHAR(100) NOT NULL,
                            email VARCHAR(100) NOT NULL UNIQUE
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS moods (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name ENUM('HAPPY', 'SAD', 'INSPIRED') NOT NULL,
                            description VARCHAR(255)
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS user_roles (
                            id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
                            user_role  ENUM('ADMIN', 'USER') NOT NULL
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS posts (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            content TEXT NOT NULL,
                            user_id BIGINT NOT NULL,
                            mood_id BIGINT NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (mood_id) REFERENCES moods(id) ON DELETE CASCADE

                        );
                    """);


            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS users_user_roles (
                            user_id BIGINT NOT NULL,
                            user_roles_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, user_roles_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (user_roles_id) REFERENCES user_roles(id) ON DELETE CASCADE
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS posts_likes (
                            post_id BIGINT NOT NULL,
                            likes_id BIGINT NOT NULL,
                            PRIMARY KEY (post_id, likes_id),
                            FOREIGN KEY (likes_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
                        );
                    """);

            System.out.println("All tables created successfully (if they didn't already exist).");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clearTable(String tableName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE " + tableName);
            System.out.println("Cleared table: " + tableName);
        }
    }

    public boolean isTableEmpty(String tableName) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return true;
    }

    public boolean isDatabaseConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }


}
