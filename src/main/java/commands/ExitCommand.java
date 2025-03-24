package commands;

import main.ClientHandler;

public class ExitCommand implements Command{
    private final ClientHandler clientHandler;

    public ExitCommand(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    public String execute(String[] args) {
        clientHandler.closeConnection();
        return "Goodbye!";
    }
}
