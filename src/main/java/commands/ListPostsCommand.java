package commands;

import model.Post;
import main.ClientHandler;
import service.PostService;

import java.sql.SQLException;
import java.util.List;

public class ListPostsCommand implements Command{
    private final PostService postService;
    private final ClientHandler clientHandler;

    public ListPostsCommand(PostService postService, ClientHandler clientHandler) {
        this.postService = postService;
        this.clientHandler = clientHandler;
    }

    @Override
    public String execute(String[] args) {
        if (clientHandler.getCurrentUser() == null) {
            return "You must be logged in to view posts.";
        }
        try {
            List<Post> posts = postService.getAll();
            if (posts.isEmpty()) {
                return "No posts found.";
            }

            StringBuilder result = new StringBuilder("All Posts:\n");
            for (Post post : posts) {
                result.append(formatPost(post));
            }

            return result.toString();
        } catch (SQLException e) {
            return "Error retrieving posts: " + e.getMessage();
        }
    }

    private String formatPost(Post post) {
        return String.format("ID: %d | User: %s | Mood: %s | Content: %s | Likes: %s%n",
                post.getId(),
                post.getUser().getUsername(),
                post.getMood().getName(),
                post.getContent(),
                post.getLikes().size());
    }
}
