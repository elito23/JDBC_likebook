package commands;

import main.ClientHandler;
import service.PostService;

import java.sql.SQLException;

public class LikeDislikeCommand implements Command{
    private final PostService postService;
    private final ClientHandler clientHandler;

    public LikeDislikeCommand(PostService postService, ClientHandler clientHandler) {
        this.postService = postService;
        this.clientHandler = clientHandler;
    }

    @Override
    public String execute(String[] args) {
        if (clientHandler.getCurrentUser() == null) {
            return "You must be logged in to like/dislike posts.";
        }
        if (args.length != 1) {
            return "Usage: like_post <post_id>";
        }
        try {
            Long postId = Long.parseLong(args[0]);
            if(postService.getPostById(postId).isEmpty()){
                return  "No post with this ID found!";
            }
            int likes=postService.getLikeCount(postId);
            postService.likePost(postId, clientHandler.getCurrentUser());
            int updatedLikes= postService.getLikeCount(postId);
            if(updatedLikes-likes>0){
                return "Post was liked!";
            }else{
                return "Post was disliked!";
            }
        } catch (NumberFormatException e) {
            return "Invalid post ID format.";
        } catch (SQLException e) {
            return "Error liking/disliking post: " + e.getMessage();
        }
    }
}
