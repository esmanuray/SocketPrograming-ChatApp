package serverclientproje;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
public class ClientTerminal {
    private static final int PORT = 5000;
    private static final String SERVER_ADDRESS = "localhost";
    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, PORT);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter your username: ");
            String username = consoleInput.readLine();
            output.writeUTF(username);
            new Thread(() -> {
                try {
                    String serverMessage;
                    while (true) {
                        serverMessage = input.readUTF();
                        System.out.println(serverMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            String clientMessage;
            while (true) {
                clientMessage = consoleInput.readLine();
                output.writeUTF(clientMessage);
                if (clientMessage.equalsIgnoreCase("bye")) {
                    break;
                }
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
