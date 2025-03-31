package repositories;

import model.UserRole;
import model.UserRoleEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRoleRepository {
    private Connection connection;
    private final BaseRepository baseRepository;

    public UserRoleRepository(Connection connection, BaseRepository baseRepository) {
        this.connection = connection;
        this.baseRepository = baseRepository;
    }

    public List<UserRole> findAll() throws SQLException {
        List<UserRole> userRoles=new ArrayList<>();
        String query = "SELECT * FROM user_roles";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next())
                userRoles.add(baseRepository.mapToUserRole(rs));
        }
        return userRoles;
    }

    public Optional<UserRole> findById(Long id) throws SQLException {
        String query = "SELECT * FROM user_roles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Optional.of(baseRepository.mapToUserRole(rs));
            }
        }
        return Optional.empty();
    }

    public Optional<UserRole> findByUserRole(UserRoleEnum userRoleEum) throws SQLException {
        String query = "SELECT * FROM user_roles WHERE user_role = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userRoleEum.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Optional.of(baseRepository.mapToUserRole(rs));
            }
        }
        return Optional.empty();
    }

    public void saveUserRole(UserRole userRole) throws SQLException {
        String query = "INSERT INTO user_roles(user_role) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userRole.getUserRole().name());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Failed to save user role, no rows affected.");
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    userRole.setId(rs.getLong(1));
                    System.out.println("Successfully saved a userRole");
                } else {
                    throw new SQLException("Failed to retrieve generated user role ID.");
                }
            }
        }
    }

    public void deleteById(Long id) throws SQLException {
        if (findById(id).isEmpty()) {
            System.err.println("No user role found with id: " + id);
            return;
        }
        String query = "DELETE FROM user_roles WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("No user role deleted with id: " + id);
                return;
            }
            System.out.println("User role with id " + id + " deleted successfully.");
            return;
        }
    }
}
