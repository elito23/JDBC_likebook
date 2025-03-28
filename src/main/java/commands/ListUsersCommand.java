package commands;

import config.AppLogger;
import model.User;
import service.UserService;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ListUsersCommand implements Command {
    private final UserService userService;
    private static final Logger logger = AppLogger.getLogger(ListUsersCommand.class);


    public ListUsersCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String execute(String[] args) {
        try {
            List<User> users = userService.getAll();
            if (users.isEmpty()) {
                return "No users found";
            }
            StringBuilder result = new StringBuilder("Registered users:\n");
            for (User user : users) {
                result.append(formatUser(user));
            }
            return result.toString();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving users: ", e);
            return "Error retrieving users!";
        }
    }

    private String formatUser(User user) {
        StringBuilder roles=new StringBuilder();
        roles.append(user.getUserRoles().stream()
                .map(role->role.getUserRole().name())
                .collect(Collectors.joining(", ")));
        return String.format("ID: %d | Username: %s | Email: %s | Roles: %s%n",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles);
    }
}
