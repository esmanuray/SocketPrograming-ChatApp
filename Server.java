package serverclientproje;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int PORT = 5000;
    private List<ClientHandler> clients = new ArrayList<>();

    public Server() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server running. Port:  " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("connect to new user. user: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    private class ClientHandler extends Thread {

        private Socket socket;
        private DataInputStream input;
        private DataOutputStream output;

        public ClientHandler(Socket socket) {
            try {
                this.socket = socket;
                this.input = new DataInputStream(socket.getInputStream());
                this.output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {

            }
        }

        @Override
        public void run() {
            String username = null;
            try {
                username = input.readUTF();
                broadcast(username + " joined the chat.");

                String message;
                while (true) {
                    message = input.readUTF();
                    if (message.equalsIgnoreCase("bye")) {
                        break;
                    }
                    broadcast(username + ": " + message);
                }
            } catch (IOException e) {

            } finally {
                try {
                    broadcast(username + " leave the chat.");
                    socket.close();
                    clients.remove(this);

                } catch (IOException e) {

                }
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                try {
                    client.output.writeUTF(message);
                } catch (IOException e) {

                }
            }
        }
    }
}
