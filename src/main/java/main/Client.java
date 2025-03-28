package main;

import config.AppLogger;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final Logger logger = AppLogger.getLogger(Client.class);

    public static void main(String[] args) {
        String host = "localhost";
        int port = 1234;

        Socket socket = null;
        int retries = 3;

        for (int i = 0; i < retries; i++) {
            try {
                socket = new Socket(host, port);
                break;
            } catch (IOException e) {
                logger.log(Level.WARNING, "Attempt " + (i + 1) + ": Unable to connect to server.", e);
                System.err.println("Attempt " + (i + 1) + ": Unable to connect to LikeBook server.");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        if (socket == null) {
            System.err.println("Failed to connect to LikeBook server after multiple attempts.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to Likebook server!");
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    logger.log(Level.WARNING,"Connection closed: ",e);
                    System.err.println("Connection closed.");
                }
            }).start();

            String input;
            while ((input = consoleReader.readLine()) != null) {
                writer.write(input);
                writer.newLine();
                writer.flush();
                if ("exit".equalsIgnoreCase(input.trim())) {
                    System.out.println("Exiting main.");
                    break;
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to connect to the LikeBook server or lost connection", e);
            System.err.println("Unable to connect to the LikeBook server or lost connection!");
        }
    }
}
