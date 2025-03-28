package commands;

import config.AppLogger;
import model.Post;
import main.ClientHandler;
import service.PostService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FindPostCommand implements Command {
    private final PostService postService;
    private final ClientHandler clientHandler;
    private static final Logger logger = AppLogger.getLogger(FindPostCommand.class);


    public FindPostCommand(PostService postService, ClientHandler clientHandler) {
        this.postService = postService;
        this.clientHandler = clientHandler;
    }
    @Override
    public String execute(String[] args) {
        if (clientHandler.getCurrentUser() == null) {
            return "You must be logged in to find posts.";
        }

        if (args.length != 2) {
            return "Usage: find_post <by_id|by_user> <value>";
        }
        String searchType = args[0];
        String value = args[1];

        try {
            if (searchType.equalsIgnoreCase("by_id")) {
                return findPostById(value);
            } else if (searchType.equalsIgnoreCase("by_user")) {
                return findPostsByUser(value);
            } else {
                return "Invalid search type. Use 'by_id' or 'by_user'.";
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error finding post: ", e);
            return "Error finding post!";
        }
    }
    private String findPostById(String idString) throws SQLException {
        try {
            Long id = Long.parseLong(idString);
            Optional<Post> post = postService.getPostById(id);

            if (post.isPresent()) {
                return formatPost(post.get());
            } else {
                return "No post found with ID " + id;
            }
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid ID format: ", e);
            return "Invalid ID format.";
        }
    }

    private String findPostsByUser(String username) throws SQLException {
        List<Post> posts = postService.getAllByUser_Username(username);

        if (posts.isEmpty()) {
            return "No posts found for user " + username;
        }

        StringBuilder result = new StringBuilder("Posts by " + username + ":\n");
        for (Post post : posts) {
            result.append(formatPost(post));
        }
        return result.toString();
    }

    private String formatPost(Post post) {
        return String.format("ID: %d | Mood: %s | Content: %s | Likes: %s%n",
                post.getId(),
                post.getMood().getName(),
                post.getContent(),
                post.getLikes().size());
    }
}
