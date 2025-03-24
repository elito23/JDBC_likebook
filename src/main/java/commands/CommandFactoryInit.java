package commands;

import model.UserRoleEnum;
import main.ClientHandler;
import service.MoodService;
import service.PostService;
import service.UserRoleService;
import service.UserService;

import java.util.Set;

public class CommandFactoryInit {
    public static void initializeCommands(CommandFactory commandFactory,
                                          UserService userService,
                                          PostService postService,
                                          MoodService moodService,
                                          UserRoleService userRoleService,
                                          ClientHandler clientHandler) {


        commandFactory.registerAnonymousCommand("register", new RegisterUserCommand(userService, userRoleService));
        commandFactory.registerAnonymousCommand("login", new LoginCommand(userService, clientHandler));
        commandFactory.registerAnonymousCommand("help", new HelpCommand(commandFactory, clientHandler));
        commandFactory.registerAnonymousCommand("exit", new ExitCommand(clientHandler));

        Set<UserRoleEnum> adminsOnly = Set.of(UserRoleEnum.ADMIN);
        commandFactory.registerRoleRestrictedCommand("delete_user", new DeleteUserCommand(userService, clientHandler), adminsOnly);
        commandFactory.registerRoleRestrictedCommand("list_users", new ListUsersCommand(userService), adminsOnly);

        Set<UserRoleEnum> usersAndAdmin = Set.of(UserRoleEnum.USER, UserRoleEnum.ADMIN);
        commandFactory.registerRoleRestrictedCommand("create_post", new CreatePostCommand(postService, moodService, clientHandler), usersAndAdmin);
        commandFactory.registerRoleRestrictedCommand("delete_post", new DeletePostCommand(postService, clientHandler), usersAndAdmin);
        commandFactory.registerRoleRestrictedCommand("list_moods", new ListMoodsCommand(moodService), usersAndAdmin);
        commandFactory.registerRoleRestrictedCommand("find_post", new FindPostCommand(postService,clientHandler), usersAndAdmin);
        commandFactory.registerRoleRestrictedCommand("list_posts", new ListPostsCommand(postService, clientHandler), usersAndAdmin);
        commandFactory.registerRoleRestrictedCommand("like_post", new LikeDislikeCommand(postService, clientHandler), usersAndAdmin);
        commandFactory.registerRoleRestrictedCommand("help", new HelpCommand(commandFactory, clientHandler),usersAndAdmin);
        commandFactory.registerRoleRestrictedCommand("exit", new ExitCommand(clientHandler),usersAndAdmin);
        commandFactory.registerRoleRestrictedCommand("logout", new LogoutCommand(clientHandler),usersAndAdmin);
    }
}
