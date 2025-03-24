package repositories;

import model.Post;
import model.User;
import model.UserRole;

import javax.crypto.spec.OAEPParameterSpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private Connection connection;
    private final BaseRepository baseRepository;

    public UserRepository(Connection connection, BaseRepository baseRepository) {
        this.connection = connection;
        this.baseRepository = baseRepository;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(baseRepository.mapToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users:  " + e.getMessage());
            throw e;
        }
        return users;

    }

    public Optional<User> findById(Long id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Optional.of(baseRepository.mapToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by id: " + id + " -> " + e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Optional.of(baseRepository.mapToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by username: " + username + " -> " + e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    public void saveUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to save user, no rows affected.");
            }
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                } else {
                    throw new SQLException("Failed to retrieve generated user id.");
                }
            }
            saveUserRoles(user);
            saveLikedPosts(user);
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            throw e;
        }
    }

    public void deleteById(Long id) throws SQLException {
        if (findById(id).isEmpty()) {
            System.err.println("No user found with ID: " + id);
            return;
        }
        deleteUserRoles(id);
        String query = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("No user deleted with id: " + id);
                return;
            }
            System.out.println("User with id " + id + " deleted successfully.");
            return;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            throw e;
        }
    }
    private void deleteUserRoles(Long userId) throws SQLException {
        String query = "DELETE FROM users_user_roles WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, userId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("No roles found for user ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting user roles: " + e.getMessage());
            throw e;
        }
    }


    private void saveUserRoles(User user) throws SQLException {
        String query = "INSERT INTO users_user_roles (user_id, user_roles_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (UserRole role : user.getUserRoles()) {
                stmt.setLong(1, user.getId());
                stmt.setLong(2, role.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error saving user roles: " + e.getMessage());
            throw e;
        }
    }

    private void saveLikedPosts(User user) throws SQLException {
        String query = "INSERT INTO posts_likes (post_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (Post post : user.getPosts()) {
                stmt.setLong(1, post.getId());
                stmt.setLong(2, user.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error saving liked posts: " + e.getMessage());
            throw e;
        }
    }
}
