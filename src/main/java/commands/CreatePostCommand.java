package commands;

import config.AppLogger;
import model.Mood;
import model.MoodNameEnum;
import model.Post;
import main.ClientHandler;
import service.MoodService;
import service.PostService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CreatePostCommand implements Command{
    private final PostService postService;
    private final MoodService moodService;
    private final ClientHandler clientHandler;
    private static final Logger logger = AppLogger.getLogger(CreatePostCommand.class);


    public CreatePostCommand(PostService postService, MoodService moodService, ClientHandler clientHandler) {
        this.postService = postService;
        this.moodService = moodService;
        this.clientHandler = clientHandler;
    }

    @Override
    public String execute(String[] args) {
        if(clientHandler.getCurrentUser()==null){
            return "You must be logged in to create a post.";
        }
        if (args.length < 2) {
            return "Usage: create_post <mood_name> <content>";
        }
        String moodNameStr=args[0].toUpperCase();
        String content= Arrays.stream(args,1,args.length)
                .collect(Collectors.joining(" "));
        try{
            MoodNameEnum moodName=MoodNameEnum.valueOf(moodNameStr);
            Mood mood=moodService.getMoodByName(moodName);
            Post post=new Post();

            post.setUser(clientHandler.getCurrentUser());
            post.setMood(mood);
            post.setLikes(new ArrayList<>());
            post.setContent(content);

            postService.createPost(post);
            return "Post created successfully!";
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING,"Invalid mood name input: " + moodNameStr+": ",e);
            return "Invalid mood name. Allowed moods: HAPPY, SAD, INSPIRED.";
        } catch (SQLException e) {
            logger.log(Level.WARNING,"Error creating post: ",e);
            return "Error creating post: " + e.getMessage();
        }
    }
}
