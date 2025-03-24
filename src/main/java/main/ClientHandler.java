package main;

import commands.*;
import model.User;
import model.UserRole;
import model.UserRoleEnum;
import repositories.*;
import service.*;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final CommandFactory commandFactory;
    private final UserService userService;
    private final PostService postService;
    private final MoodService moodService;
    private final UserRoleService userRolesService;
    private final Connection connection;

    private User currentUser;
    private volatile boolean running = true;

    public ClientHandler(Socket clientSocket, CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
        this.connection = createConnection();
        this.clientSocket = clientSocket;

        BaseRepository baseRepository = new BaseRepository(connection);
        UserRepository userRepository = new UserRepository(connection, baseRepository);
        PostRepository postRepository = new PostRepository(connection, baseRepository);
        MoodRepository moodRepository = new MoodRepository(connection,baseRepository);
        UserRoleRepository userRoleRepository = new UserRoleRepository(connection,baseRepository);

        this.userService = new UserService(userRepository);
        this.postService = new PostService(postRepository, userRoleRepository);
        this.moodService = new MoodService(moodRepository);
        this.userRolesService = new UserRoleService(userRoleRepository);

        CommandFactoryInit.initializeCommands(this.commandFactory, userService, postService, moodService, userRolesService, this);
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/JDBC_likeBook_db", "root", "Root.1234"
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create database connection", e);
        }
    }

    public void closeConnection() {
        running = false;
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
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

                String response = handleCommand(input);

                writer.println(response);
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
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
}
