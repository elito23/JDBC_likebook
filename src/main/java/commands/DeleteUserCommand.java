package commands;

import config.AppLogger;
import main.ClientHandler;
import model.User;
import service.UserService;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeleteUserCommand implements Command{
    private final UserService userService;
    private final ClientHandler clientHandler;
    private static final Logger logger = AppLogger.getLogger(DeleteUserCommand.class);


    public DeleteUserCommand(UserService userService, ClientHandler clientHandler) {
        this.userService = userService;
        this.clientHandler = clientHandler;
    }

    @Override
    public String execute(String[] args) {
        if (clientHandler.getCurrentUser() == null) {
            return "You must be logged in to delete users.";
        }
        if (args.length != 1) {
            return "Usage: delete_user <user_id>";
        }
        try{
            Long userId=Long.parseLong(args[0]);
            Optional<User>user=userService.getById(userId);
            if(user.isEmpty()){
                return "No user with this ID!";
            }
            userService.deleteUser(userId);
            if(user.get().getId()==clientHandler.getCurrentUser().getId()){
                clientHandler.setCurrentUser(null);
                return "Your account was deleted and you have been logged out.";
            }
            return "User deleted successfully";
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting user: ", e);
            return "Error deleting user!";
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid user ID format: ", e);
            return "Invalid user ID format.";
        }
    }
}
