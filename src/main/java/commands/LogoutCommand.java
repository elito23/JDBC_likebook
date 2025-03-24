package commands;

import main.ClientHandler;

public class LogoutCommand implements Command{
    private final ClientHandler clientHandler;

    public LogoutCommand(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public String execute(String[] args) {
        if (clientHandler.getCurrentUser() == null) {
            return "You are not logged in.";
        }
        clientHandler.setCurrentUser(null);
        return "You have been logged out.";
    }
}
