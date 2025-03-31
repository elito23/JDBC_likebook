package main;

import commands.*;
import config.AppLogger;
import config.DbConnection;
import model.User;
import model.UserRole;
import model.UserRoleEnum;
import repositories.*;
import service.*;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Connection connection;
    private final CommandFactory commandFactory;
    private UserService userService;
    private PostService postService;
    private MoodService moodService;
    private UserRoleService userRolesService;
    private static final Logger logger = AppLogger.getLogger(ClientHandler.class);
    private User currentUser;
    private volatile boolean running = true;

    public ClientHandler(Socket clientSocket, CommandFactory commandFactory) throws SQLException {
        this.commandFactory = commandFactory;
        this.connection = DbConnection.getConnection();
        this.clientSocket = clientSocket;
        initServices(connection);

    }

    public void closeConnection() {
        running = false;
        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Problem closing connection", e);
        }
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            writer.println("Connected to the server. Type 'help' for available commands.");

            while (running) {
                String input = reader.readLine();
                if (input == null) break;
                System.out.println("Received input from main: " + input);

                String response;
                try {
                    ensureConnectionIsAlive();
                    response = handleCommand(input);
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Database error during command execution", e);
                    response = "A database error occurred. Please try again later.";
                }
                writer.println(response);
            }
        } catch (IOException e) {
            logger.log(Level.INFO, "Client disconnected", e);
        }finally {
            closeConnection();
            logger.info("Connection closed for client: " + clientSocket.getInetAddress());
        }
    }

    private String handleCommand(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0) {
            return "Empty command received.";
        }

        String commandName = parts[0];
        String[] args = (parts.length > 1) ? extractArgs(parts) : new String[0];

        Set<UserRoleEnum> currentRoles = getCurrentUserRoles();
        return commandFactory.executeCommand(commandName, args, currentRoles);
    }

    private String[] extractArgs(String[] parts) {
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);
        return args;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Set<UserRoleEnum> getCurrentUserRoles() {
        if (currentUser == null) {
            return Collections.emptySet();
        }

        return currentUser.getUserRoles().stream()
                .map(UserRole::getUserRole)
                .collect(Collectors.toSet());
    }
    private void initServices(Connection connection) {
        BaseRepository baseRepository = new BaseRepository(connection);
        UserRepository userRepository = new UserRepository(connection, baseRepository);
        PostRepository postRepository = new PostRepository(connection, baseRepository);
        MoodRepository moodRepository = new MoodRepository(connection, baseRepository);
        UserRoleRepository userRoleRepository = new UserRoleRepository(connection, baseRepository);

        this.userService = new UserService(userRepository);
        this.postService = new PostService(postRepository, userRoleRepository);
        this.moodService = new MoodService(moodRepository);
        this.userRolesService = new UserRoleService(userRoleRepository);

        CommandFactoryInit.initializeCommands(this.commandFactory, userService, postService, moodService, userRolesService, this);
    }
    private void ensureConnectionIsAlive() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            logger.warning("Lost DB connection. Reconnecting...");
            this.connection = DbConnection.getConnection();
            initServices(connection);
        }
    }

}
