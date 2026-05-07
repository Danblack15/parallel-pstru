package org.example.lab_6;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private static final int SERVER_PORT = 9090;

    // Список всех подключённых клиентов
    private static final CopyOnWriteArrayList<ClientHandler> connectedClients =
            new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== SERVER STARTED ===");

        try (ServerSocket server = new ServerSocket(SERVER_PORT);
             Scanner console = new Scanner(System.in)) {

            // Поток для подключения клиентов
            Thread connectionThread = new Thread(() -> waitForClients(server));
            connectionThread.start();

            // Чтение сообщений из консоли сервера
            while (true) {
                String text = console.nextLine();

                if (text.equalsIgnoreCase("exit")) {
                    System.out.println("Stopping server...");
                    break;
                }

                sendToEveryone("[SERVER MESSAGE] " + text);
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }

        System.out.println("=== SERVER CLOSED ===");
    }

    // Ожидание новых клиентов
    private static void waitForClients(ServerSocket server) {

        while (true) {

            try {
                Socket socket = server.accept();

                ClientHandler client = new ClientHandler(socket);

                connectedClients.add(client);

                System.out.println("New client connected: "
                        + socket.getInetAddress());

                System.out.print("Write message: ");
                client.start();

            } catch (IOException e) {
                break;
            }
        }
    }

    // Отправка сообщения всем клиентам
    private static void sendToEveryone(String message) {

        System.out.println("Sending: " + message);

        for (ClientHandler client : connectedClients) {
            client.send(message);
        }

        System.out.print("Write message: ");
    }

    // Класс для хранения информации о клиенте
    private static class ClientHandler extends Thread {

        private final Socket socket;
        private PrintWriter writer;

        public ClientHandler(Socket socket) {
            this.socket = socket;

            try {
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.out.println("Output stream error");
            }
        }

        // Отправка сообщения клиенту
        public void send(String text) {
            writer.println(text);
        }

        @Override
        public void run() {

            try {

                // Просто ждём пока клиент не отключится
                while (!socket.isClosed()) {

                    if (socket.getInputStream().read() == -1) {
                        break;
                    }
                }

            } catch (IOException ignored) {

            } finally {

                connectedClients.remove(this);

                try {
                    socket.close();
                } catch (IOException ignored) {}

                System.out.println("Client disconnected");
            }
        }
    }
}