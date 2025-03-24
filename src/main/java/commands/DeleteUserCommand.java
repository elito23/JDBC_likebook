package commands;

import main.ClientHandler;
import model.User;
import service.UserService;

import java.sql.SQLException;
import java.util.Optional;

public class DeleteUserCommand implements Command{
    private final UserService userService;
    private final ClientHandler clientHandler;

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
            return "Error deleting user: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Invalid user ID format.";
        }
    }
}
