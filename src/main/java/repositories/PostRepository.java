package repositories;

import model.Post;
import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostRepository {
    private Connection connection;
    private final BaseRepository baseRepository;

    public PostRepository(Connection connection, BaseRepository baseRepository) {
        this.connection = connection;
        this.baseRepository = baseRepository;
    }

    public List<Post> findAll() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM posts";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next())
                posts.add(baseRepository.mapToPost(rs));
        }
        return posts;
    }

    public Optional<Post> findById(Long id) throws SQLException {
        String query = "SELECT * FROM posts WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return Optional.of(baseRepository.mapToPost(rs));
            }
        }
        return Optional.empty();
    }

    public List<Post> findAllByUser_Username(String username) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String query = """
                SELECT * FROM posts p
                JOIN users u ON u.id = p.user_id
                WHERE u.username = ?
                """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next())
                    posts.add(baseRepository.mapToPost(rs));
            }
        }
        return posts;
    }

    public List<Post> findAllByUser_UsernameNot(String username) throws SQLException {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * FROM posts p " +
                "JOIN users u ON u.id = p.user_id " +
                "WHERE NOT u.username = ? ";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(baseRepository.mapToPost(rs));
                }
            }
        }
        return posts;
    }

    public void savePost(Post post) throws SQLException {
        if (post.getContent() == null || post.getUser() == null || post.getMood() == null) {
            throw new IllegalArgumentException("Post, user, or mood cannot be null.");
        }
        String query = "INSERT INTO posts(content, user_id, mood_id) VALUES (?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, post.getContent());
            stmt.setLong(2, post.getUser().getId());
            stmt.setLong(3, post.getMood().getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Failed to save post, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Failed to retrieve generated post ID.");
                }
            }
        }
    }
    private boolean isPostLikedByUser(Long postId, Long userId) throws SQLException {
        String query = """
            SELECT 1 FROM posts_likes 
            WHERE post_id = ? AND likes_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, postId);
            stmt.setLong(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
    public void deleteById(Long id) throws SQLException {
        if (findById(id).isEmpty())
            System.err.println("No posts found with id: " + id);
        String query = "DELETE FROM posts WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0)
                System.err.println("No post deleted with id " + id);
            System.out.println("Post with id " + id + " deleted successfully.");
        }
    }
    public void likePost(Long postId, User user) throws SQLException {
        if (isPostLikedByUser(postId, user.getId())) {
            removeLike(postId, user.getId());
        } else {
            addLike(postId, user.getId());
        }
    }
    public int getLikeCount(Long postId) throws SQLException {
        String query = """
            SELECT COUNT(*) FROM posts_likes WHERE post_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setLong(1, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    private void addLike(Long postId, Long userId) throws SQLException {
       String query="INSERT INTO posts_likes(post_id, likes_id) VALUES (?,?)";
       try(PreparedStatement stmt=connection.prepareStatement(query)) {
           stmt.setLong(1,postId);
           stmt.setLong(2, userId);
           int affectedRows = stmt.executeUpdate();
           if(affectedRows==0)
               throw new SQLException("Failed to like post, no rows affected.");
           System.out.println("Post liked!");
       }
    }
    private void removeLike(Long postId, Long userId) throws SQLException {
        String query="DELETE FROM posts_likes WHERE post_id=? AND likes_id=?";
        try(PreparedStatement stmt=connection.prepareStatement(query)) {
            stmt.setLong(1,postId);
            stmt.setLong(2, userId);
            int affectedRows = stmt.executeUpdate();
            if(affectedRows==0)
                throw new SQLException("Failed to dislike post, no rows affected.");
            System.out.println("Post disliked!");
        }
    }
}
