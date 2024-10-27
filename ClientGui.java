package serverclientproje;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;

public class ClientGui {

    private static final int PORT = 5000;
    private static final String SERVER_ADDRESS = "localhost";

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    private JFrame frame;
    private JTextArea SohbetAlani;
    private JTextField MesajGirisi;
    private JButton sendButton;

    public static void main(String[] args) {
        new ClientGui().ArayuzuOlusturVeGoster();
    }

    private void ArayuzuOlusturVeGoster() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

        } catch (Exception ex) {

        }

        frame = new JFrame("Esma's Server Client Program");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SohbetAlani = new JTextArea();
        SohbetAlani.setEditable(false);
        Font font = new Font("Courier New", Font.PLAIN, 14);

        SohbetAlani.setFont(font);

        JScrollPane scrollPane = new JScrollPane(SohbetAlani);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());

        MesajGirisi = new JTextField();
        MesajGirisi.addActionListener(e -> sendMessage());
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(MesajGirisi, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(inputPanel, BorderLayout.SOUTH);

        Image iconImage = loadImage("icon3.png");
        if (iconImage != null) {
            frame.setIconImage(iconImage);
        }

        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        connectToServer();
    }

    private Image loadImage(String imageName) {
        try ( InputStream inputStream = getClass().getResourceAsStream(imageName)) {
            if (inputStream != null) {
                return ImageIO.read(inputStream);
            }
        } catch (IOException e) {

        }
        return null;
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            ImageIcon originalIcon = new ImageIcon("icon3.png");
            Image originalImage = originalIcon.getImage();
            Image resizedImage = originalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);

            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            String username = (String) JOptionPane.showInputDialog(
                    null,
                    "Enter your username:",
                    "username",
                    JOptionPane.PLAIN_MESSAGE,
                    resizedIcon,
                    null,
                    "");

            if (username != null && !username.isEmpty()) {
                System.out.println("username: " + username);
            } else {
                System.out.println("don't enter a username.");
            }
             appendMessage("you joined the conversation.");

            output.writeUTF(username);
            SwingUtilities.invokeLater(() -> {
                SohbetAlani.repaint();
            });

            Thread messageListener = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String serverMessage;
                        while (true) {
                            serverMessage = input.readUTF();
                            if (serverMessage.equals("leave_user")) {
                                appendMessage(username + " leaved the conversation.");
                            } else {
                                appendMessage(serverMessage);
                            }
                        }
                    } catch (IOException e) {
                    }
                }
            });
            messageListener.setDaemon(true);

            messageListener.start();
        } catch (Exception e) {

        }
    }

    private void sendMessage() {
        String mesaj = MesajGirisi.getText();

        MesajGirisi.setText("");

        try {
            output.writeUTF(mesaj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String message) {
        SohbetAlani.append(message + "\n");

    }
}
