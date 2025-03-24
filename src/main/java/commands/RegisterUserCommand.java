package commands;

import model.User;
import model.UserRole;
import model.UserRoleEnum;
import service.UserRoleService;
import service.UserService;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class RegisterUserCommand  implements Command{
    private final UserService userService;
    private final UserRoleService userRoleService;

    public RegisterUserCommand(UserService userService, UserRoleService userRoleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @Override
    public String execute(String[] args) {
        if(args.length<4){
            return "Usage: register <username> <password> <email> <role1 role2>";

        }
        String username = args[0];
        String password = args[1];
        String email = args[2];
        String allRoles = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        String[] roleNames = allRoles.split("\\s+");
        try{
            Set<UserRole> roles = Arrays.stream(roleNames)
                    .map(role -> {
                        try {
                            return userRoleService.getByUserRole(UserRoleEnum.valueOf(role.toUpperCase()));
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet());

            User user=new User();

            user.setUserRoles(roles);
            user.setEmail(email);
            user.setPassword(password);
            user.setUsername(username);
            user.setPosts(new ArrayList<>());

            userService.createUser(user);
            return "User registered successfully with roles: " +
                    roles.stream()
                            .map(role->role.getUserRole().name())
                            .collect(Collectors.joining(", "));

        } catch (IllegalArgumentException e) {
            return "Invalid role. Available roles: ADMIN, USER";
        }
        catch (SQLException e) {
            return "Error registering user: " + e.getMessage();
        }
    }
}
