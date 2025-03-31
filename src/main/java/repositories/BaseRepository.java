package repositories;

import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BaseRepository {
    protected final Connection connection;

    public BaseRepository(Connection connection) {
        this.connection = connection;
    }


    public Mood mapToMood(ResultSet rs) throws SQLException {
        return new Mood(
                rs.getLong("id"),
                MoodNameEnum.valueOf(rs.getString("name")),
                rs.getString("description")
        );

    }

    public Post mapToPost(ResultSet rs) throws SQLException {
        return new Post(
                rs.getLong("id"),
                rs.getString("content"),
                findUserByIdWithoutPosts(rs.getLong("user_id")),
                findLikesByPostId(rs.getLong("id")),
                findMoodById(rs.getLong("mood_id"))
        );

    }

    private User findUserByIdWithoutPosts(long user_id) throws SQLException {
        String query = "SELECT id, username, password, email FROM users WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, user_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            new ArrayList<>(),
                            findUserRolesByUserId(user_id)
                    );
                }
            }
        }
        return null;
    }


    public User mapToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                findPostsByUserId(rs.getLong("id")),
                findUserRolesByUserId(rs.getLong("id"))
        );
    }
    public UserRole mapToUserRole(ResultSet rs) throws SQLException {
        return new UserRole(
                rs.getLong("id"),
                UserRoleEnum.valueOf( rs.getString("user_role"))
        );
    }

    private Mood findMoodById(long mood_id) throws SQLException {
        String query = "SELECT * FROM moods WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, mood_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapToMood(rs);
            }
        }
        return null;
    }

    private List<User> findLikesByPostId(long id) throws SQLException {

        String query = "SELECT u.* FROM users u JOIN posts_likes pl ON u.id = pl.likes_id WHERE pl.post_id = ?";
        List<User> users = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    users.add(new User(
                            rs.getLong("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            new ArrayList<>(),
                            findUserRolesByUserId(rs.getLong("id"))
                    ));
            }
        }
        return users;
    }

    private User findUserById(long user_id) throws SQLException {
        String query = "SELECT * FROM users WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, user_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapToUser(rs);
            }
        }
        return null;
    }

    private Set<UserRole> findUserRolesByUserId(long id) throws SQLException {
        Set<UserRole> roles = new HashSet<>();
        String query = "SELECT ur.* FROM user_roles ur " +
                "JOIN users_user_roles uur ON ur.id = uur.user_roles_id " +
                "WHERE uur.user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1,id);
            try(ResultSet rs= stmt.executeQuery()){
                while(rs.next())
                    roles.add(mapToUserRole(rs));
            }
        }
        return roles;
    }

    private List<Post> findPostsByUserId(long id) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM posts p WHERE p.user_id= ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    posts.add(mapToPost(rs));
            }
        }
        return posts;
    }


}
