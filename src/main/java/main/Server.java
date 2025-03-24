package main;

import commands.CommandFactory;
import init.DBInit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class Server {

    private static final int PORT = 1234;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/JDBC_likeBook_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Root.1234";
    static int countClients = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

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
