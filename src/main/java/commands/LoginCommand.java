package commands;

import config.AppLogger;
import model.User;
import main.ClientHandler;
import service.UserService;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LoginCommand implements Command {
    private final UserService userService;
    private final ClientHandler clientHandler;
    private static final Logger logger = AppLogger.getLogger(LoginCommand.class);


    public LoginCommand(UserService userService, ClientHandler clientHandler) {
        this.userService = userService;
        this.clientHandler = clientHandler;
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 2) {
            return "Usage: login <username> <password>";
        }
        String username = args[0];
        String password = args[1];
        try {
            User user = userService.getUserByUsername(username);
            if (!user.getPassword().equals(password)) {
                return "Incorrect password!";
            }

            StringBuilder roles = new StringBuilder();
            roles.append(user.getUserRoles().stream()
                    .map(role -> role.getUserRole().name())
                    .collect(Collectors.joining(", ")));

            clientHandler.setCurrentUser(user);
            return "Logged in as: " + username + " with roles: " + roles;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error logging in: ", e);
            return "Error logging in!";
        }
    }
}
