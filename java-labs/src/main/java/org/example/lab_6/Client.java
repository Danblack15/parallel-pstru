package org.example.lab_6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) {

        String clientId;

        if (args.length > 0) {
            clientId = args[0];
        } else {
            clientId = "Client-" + Thread.currentThread().threadId();
        }

        System.out.println(clientId + " is starting...");

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream()))) {

            System.out.println(clientId + " connected to server");

            while (true) {
                String serverMessage = reader.readLine();

                if (serverMessage == null) {
                    break;
                }

                System.out.println(clientId + " received: "
                        + serverMessage);
            }

        } catch (IOException e) {

            System.out.println(clientId + " lost connection");
        }

        System.out.println(clientId + " finished");
    }
}