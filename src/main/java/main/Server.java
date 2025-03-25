package main;

import commands.CommandFactory;
import config.DbConnection;
import init.DBInit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class Server {

    private static final int PORT = 1234;
    static int countClients = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             Connection connection = DbConnection.getConnection()) {

            System.out.println("Initializing database...");
            DBInit dbInit = new DBInit(connection);
            dbInit.createAllTables();
            if (dbInit.isTableEmpty("user_roles"))
                dbInit.initializeUserRoles();
            if (dbInit.isTableEmpty("moods"))
                dbInit.initMoods();

            System.out.println("Likebook server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client " + countClients + " connected: " + clientSocket.getInetAddress());

                CommandFactory commandFactory = new CommandFactory();

                ClientHandler handler = new ClientHandler(clientSocket, commandFactory);

//                MyThread clientThread = new MyThread(handler);
                Thread clientThread=new Thread(handler);
                clientThread.start();

                countClients++;
            }

        } catch (IOException | java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}
