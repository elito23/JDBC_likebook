package repositories;

import model.Mood;
import model.MoodNameEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoodRepository {
    private Connection connection;
    private final BaseRepository baseRepository;

    public MoodRepository(Connection connection, BaseRepository baseRepository) {
        this.connection = connection;
        this.baseRepository = baseRepository;
    }

    public Optional<Mood> findByName(MoodNameEnum moodNameEnum) throws SQLException {
        String query = "SELECT * FROM moods WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, moodNameEnum.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Optional.of(baseRepository.mapToMood(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching mood by name: " + moodNameEnum + " -> " + e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    public Optional<Mood> findById(Long id) throws SQLException {
        String query = "SELECT * FROM moods WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Optional.of(baseRepository.mapToMood(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching mood by ID: " + id + " -> " + e.getMessage());
            throw e;
        }
        return Optional.empty();
    }

    public List<Mood> findAll() throws SQLException {
        String query = "SELECT * FROM moods";
        List<Mood> moods = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next())
                moods.add(baseRepository.mapToMood(rs));
        } catch (SQLException e) {
            System.err.println("Error fetching all moods: " + e.getMessage());
            throw e;
        }
        return moods;
    }

    public void saveMood(Mood mood) throws SQLException {
        String query = "INSERT INTO moods(name,description) VALUES (?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mood.getName().name());
            stmt.setString(2, mood.getDescription());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting mood failed, no rows affected.");
            }
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    mood.setId(rs.getLong(1));
                    System.out.println("Successfully saved a mood!");
                } else
                    throw new SQLException("Failed to retrieve generated mood ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error saving mood: " + e.getMessage());
            throw e;
        }

    }

    public void deleteById(Long id) throws SQLException {
        if (findById(id).isEmpty()) {
            System.err.println("No mood found with id: " + id);
            return;
        }
        String query = "DELETE FROM moods WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("No mood deleted with id: " + id);
                return;
            }
            System.out.println("Mood with id " + id + " deleted successfully.");
            return;
        } catch (SQLException e) {
            System.err.println("Error deleting mood: " + e.getMessage());
            throw e;
        }
    }
}
