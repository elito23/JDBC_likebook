package commands;

import main.ClientHandler;
import model.Post;
import service.PostService;

import java.sql.SQLException;
import java.util.Optional;

public class DeletePostCommand implements Command {
    private final PostService postService;
    private final ClientHandler clientHandler;

    public DeletePostCommand(PostService postService, ClientHandler clientHandler) {
        this.postService = postService;
        this.clientHandler = clientHandler;
    }

    @Override
    public String execute(String[] args) {
        if (clientHandler.getCurrentUser() == null) {
            return "You must be logged in to delete posts.";
        }
        if (args.length != 1) {
            return "Usage: delete_post <post_id>";
        }
        try{
            Long postId=Long.parseLong(args[0]);
            Optional<Post> post = postService.getPostById(postId);
            if(post.isEmpty()){
                return "No post with this ID!";
            }
            postService.deleteById(postId,clientHandler.getCurrentUser());
            return "Post with ID " + postId + " deleted successfully!";
        } catch (NumberFormatException e) {
            return "Invalid post ID format.";
        } catch (SQLException e) {
            return "Error deleting post: " + e.getMessage();
        }
    }
}
