package commands;

import model.UserRoleEnum;
import main.ClientHandler;

import java.util.Set;

public class HelpCommand implements Command{
    private final CommandFactory commandFactory;
    private final ClientHandler clientHandler;

    public HelpCommand(CommandFactory commandFactory, ClientHandler clientHandler) {
        this.commandFactory = commandFactory;
        this.clientHandler = clientHandler;
    }
    @Override
    public String execute(String[] args) {
        Set<UserRoleEnum> userRoles = clientHandler.getCurrentUserRoles();
        String availableCommands;
        if(userRoles.size()==0)
             availableCommands= commandFactory.getAvailableCommandsForAnonymous();
        else
             availableCommands = commandFactory.getAvailableCommandsForRoles(userRoles);

        return """
                Available Commands:
                ===========================
                """ + availableCommands;
    }
}
