package commands;

import model.UserRoleEnum;

import java.util.*;
import java.util.stream.Collectors;

public class CommandFactory {
    private final Map<String, Command> commandMap = new TreeMap<>();
    private final Map<String, Command> anonymousCommands = new TreeMap<>();
    private final Map<String, Set<UserRoleEnum>> roleRestrictedCommands = new TreeMap<>();

    public void registerAnonymousCommand(String commandName, Command command) {
        commandMap.put(commandName.toLowerCase(), command);
        anonymousCommands.put(commandName.toLowerCase(), command);
    }

    public void registerRoleRestrictedCommand(String commandName, Command command, Set<UserRoleEnum> allowedRoles) {
        commandMap.put(commandName.toLowerCase(), command);
        roleRestrictedCommands.put(commandName.toLowerCase(), allowedRoles);
    }

    public String executeCommand(String commandName, String[] args, Set<UserRoleEnum> userRoles) {
        String lowerCaseCommand = commandName.toLowerCase();
        Command command = commandMap.get(lowerCaseCommand);

        if (command == null) {
            if (userRoles.size() == 0)
                return "Unknown command: " + commandName + "\nAvailable Commands: " + getAvailableCommandsForAnonymous();
            else
                return "Unknown command: " + commandName + "\nAvailable Commands: " + getAvailableCommandsForRoles(userRoles);
        }
        if (anonymousCommands.containsKey(lowerCaseCommand)) {
            if(userRoles.size() != 0 &&(lowerCaseCommand.equals("login")||lowerCaseCommand.equals("register")))
                return "You have to be logged out for this command!";
            return command.execute(args);
        }

        if (roleRestrictedCommands.containsKey(lowerCaseCommand)) {
            Set<UserRoleEnum> allowedRoles = roleRestrictedCommands.get(lowerCaseCommand);
            if (userRoles.stream().noneMatch(allowedRoles::contains)) {
                return "You do not have permission to use the '" + commandName + "' command.";
            }
        }

        return command.execute(args);
    }

    public String getAvailableCommandsForRoles(Set<UserRoleEnum> userRoles) {
        Set<String> availableCommands = new TreeSet<>();

        for (Map.Entry<String, Set<UserRoleEnum>> entry : roleRestrictedCommands.entrySet()) {
            String command = entry.getKey();
            Set<UserRoleEnum> allowedRoles = entry.getValue();

            if (userRoles.stream().anyMatch(allowedRoles::contains)) {
                availableCommands.add(command);
            }
        }
        return String.join(", ", availableCommands);
    }

    public String getAvailableCommandsForAnonymous() {
        Set<String> availableCommands = new TreeSet<>();

        for (String command : anonymousCommands.keySet()) {
            availableCommands.add(command);
        }
        return String.join(", ", availableCommands);
    }

}
