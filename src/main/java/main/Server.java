package main;

import commands.CommandFactory;
import config.AppLogger;
import config.DbConnection;
import init.DBInit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = AppLogger.getLogger(Server.class);
    private static final int PORT = 1234;
    static int countClients = 1;

    public static void main(String[] args) {
        Connection connection = null;
        int retries = 3;
        for (int i = 0; i < retries; i++) {
            try {
                connection = DbConnection.getConnection();
                break;
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Failed to connect to DB (attempt " + (i + 1) + ")", e);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.log(Level.SEVERE,"Retry sleep interrupted: ",e);
                    return;
                }
            }
        }

        if (connection == null) {
            logger.severe("Could not establish DB connection after multiple attempts. Server shutting down.");
            System.out.println("Could not establish DB connection after multiple attempts. Server shutting down.");
            return;
        }
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            logger.info("Initializing database...");
            System.out.println("Initializing database...");

            DBInit dbInit = new DBInit(connection);
            dbInit.createAllTables();
            if (dbInit.isTableEmpty("user_roles"))
                dbInit.initializeUserRoles();
            if (dbInit.isTableEmpty("moods"))
                dbInit.initMoods();

            logger.info("Likebook server started on port " + PORT);
            System.out.println("Likebook server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Client " + countClients + " connected: " + clientSocket.getInetAddress());
                System.out.println("Client " + countClients + " connected: " + clientSocket.getInetAddress());

                CommandFactory commandFactory = new CommandFactory();
                ClientHandler handler = new ClientHandler(clientSocket, commandFactory);

                Thread clientThread = new Thread(handler);
                clientThread.start();

                countClients++;
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Problem with server socket or client connection", e);
            System.err.println("Problem with server socket or client connection");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unable to connect or initialize the database", e);
            System.err.println("Unable to connect or initialize the database");

        }
    }
}
