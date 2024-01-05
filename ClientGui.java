package serverclientproje;

import javax.swing.*;                           //program çalıştığında açılan penceredeki buton, metin alanı gibi yerleri bu kütüphane ile ekledik
import java.awt.*;                              //BorderLayout bu kütüphaneden aldık onun sayesinde swing kütüphanesindeki elemanları pencereye daha kolay yerleştirdik
import java.io.DataInputStream;                 //panelden aldığımuz mesajalrı bu kütüphane işledik .işlemekten kastımız aldığımız mesajı ekrandan almak
import java.io.DataOutputStream;                // ekrandan aldığımız veriyi servera iletmek 
import java.io.*;                               // bu kütüphaneyi olası giriş çıkış hataları için ekledik
import java.net.Socket;                         //iki bilgisayar arasında iletişim kurmak için kullanılan soket sınıfı bunun sayesinde oluşturduğumuz kullanıcılar birnrilerinin mesajalrını görüyorular 
import javax.imageio.ImageIO;                   // arayüze eklediğimiz ikon ve uygulama ikonlaru bu kütüphane sayesinde okunuyor.

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
        //arayüz nesnesini oluşturduk ve açılacak pencereyi çağırdık
    }

    private void ArayuzuOlusturVeGoster() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            // açılan pencerenin temasını değiştirdik
        } catch (Exception ex) {

        }

        frame = new JFrame("Esma's Server Client Program");// ekran açıldığında açılan pencerenin adını yazdık
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // pencere kapattığımızda programda client de durdurulur onu bu kod ile sağladık

        // Sohbet alanını içeren JTextArea
        SohbetAlani = new JTextArea();
        SohbetAlani.setEditable(false);// mesajların tutulduğu ekrana kullanıcının erişmesini önlüyorum
        // bu sayede kullanıcı mesaj metinlerini değştiremez
        // Metin boyutunu büyütüyorum
        // JTextArea için bir Font oluşturuyorum
        Font font = new Font("Courier New", Font.PLAIN, 14); // Font adı, stil ve boyut

        SohbetAlani.setFont(font);

        // JScrollPane kullanarak JTextArea'yı kaydırılabilir yapıyoruz
        JScrollPane scrollPane = new JScrollPane(SohbetAlani);
        frame.add(scrollPane, BorderLayout.CENTER);
        // kaydırılabilir kısmın konumunu belirledik BorderLayout.CENTER sayesinde  

        // Sohbet alanını kaydırmak için JPanel
        JPanel inputPanel = new JPanel(new BorderLayout());

        MesajGirisi = new JTextField(); //kullanıcının metin girişi yapacağı alanı oluşturduk
        MesajGirisi.addActionListener(e -> sendMessage());// addActionListenir ile sürekli bir dinleme yapıyoruz metin giriş kutusuna
        // eğer veri girilirse diye hazırda bekliyoruz
        sendButton = new JButton("Gönder");// ekradaki butonu oluşturduk
        sendButton.addActionListener(e -> sendMessage());//butonda her hangi bir aksiyon olursa diye onuda dinleme halinde birakıyoruz
        inputPanel.add(MesajGirisi, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        // panele hem mesaj girdi bölümünü hemde butonu ekliyoruz. mesaj girişini ortaya ekledik butonu ise daha sola doğru ekledik
        // BorderLayout metodu sayesinde konumunu Center East gibi kelimelerle direkt olarak söylüyoruz. 
        //başka bir yolu ise kordinat girmek olacaktı

        // Input panelini frame'e ekleme
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Pencere boyutu ve konumu
        Image iconImage = loadImage("icon3.png");
        if (iconImage != null) {
            frame.setIconImage(iconImage);
        }
        // burada penceremdeki ikonu ve görev çubuğunda görünecek ikonu ayarladık 

        frame.setSize(400, 500);// açılacak pencerenin kaça kaçlık boyutta olacağınu belirledik
        frame.setLocationRelativeTo(null);// açılacak pencereyi ekranın ortasında açılması için bu kodu ekledik

        // Pencereyi görünür yap
        frame.setVisible(true);//jframe görünür hale geldi 

        connectToServer();// bu motot ile Swing arayüzüne sahip client sunucuya bağlandı
    }

    private Image loadImage(String imageName) {
        try ( InputStream inputStream = getClass().getResourceAsStream(imageName)) {
            if (inputStream != null) {
                return ImageIO.read(inputStream);
            }
        } catch (IOException e) {

        }
        return null;
    }// oluşturuğumuz ikon dosyasını swing arayüzümüze yüklemek için kullandığımız fonksiyon

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);// soket nesnesi oluşturuldu belirtilen port ve host ile
            input = new DataInputStream(socket.getInputStream());// sunucu ile iletişim kurmak için giriş veri akışı oluşturuldu
            output = new DataOutputStream(socket.getOutputStream());// sunucu ile iletişim kurmak için çıkış veri akışı oluşturuldu
            // Özel bir simge eklemek için ImageIcon kullanın
            ImageIcon originalIcon = new ImageIcon("C:\\Users\\esman\\Documents\\NetBeansProjects\\ServerClientProje\\src\\serverclientproje\\icon3.png");
            Image originalImage = originalIcon.getImage();
            Image resizedImage = originalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH); // 50x50 boyutunda

            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            // Kullanıcı adı almak için showInputDialog kullanın
            String username = (String) JOptionPane.showInputDialog(
                    null,
                    "Kullanıcı adı giriniz:",
                    "Kullanıcı Adı Girişi",
                    JOptionPane.PLAIN_MESSAGE, // İletişim kutusu tipi
                    resizedIcon, // Özel simge
                    null, // Seçenekler dizisi (null, sadece giriş almak için)
                    "");   // Varsayılan metin

            // Kullanıcı adını kontrol et
            if (username != null && !username.isEmpty()) {
                System.out.println("Girilen Kullanıcı Adı: " + username);
            } else {
                System.out.println("Kullanıcı adı girilmedi veya iptal edildi.");
            }
            // pencere açıldığında ilk çıkan küçük pencere bu komutta oluşturuldu. buradan kullanıcı adını aldık ve sohbete bağlandık.
            appendMessage("Sohbete katıldın.");

            output.writeUTF(username);// kullanıcının seçtiği kullanıcı adı sunucuya iletildi

            // Güncellenmiş kısım
            SwingUtilities.invokeLater(() -> {
                SohbetAlani.repaint();
            });
            // bu yapı ile swing arayüz güncellemelerini yaparız. misal burada sohbete katıldığına dair kullanıcya bilgi verdik

            Thread messageListener = new Thread(new Runnable() {// sunucudan gelen mesajları dinleyen messageListener threadi oluşturuldu
                @Override
                public void run() {
                    try {
                        String serverMessage;
                        while (true) {
                            serverMessage = input.readUTF();// gelen mesajları okur eğer sunucudan gelen mesaj kullanıcı_ayrıldı olursa 
                            if (serverMessage.equals("KULLANICI_AYRILDI")) {// sohbet alanına ayrılan kullanıcı belirtilir
                                appendMessage(username + " isimli kullanıcı ayrıldı.");
                            } else {
                                appendMessage(serverMessage);
                            }
                        }
                    } catch (IOException e) {
                    }
                }
            });
            messageListener.setDaemon(true);
            // bunu daemon thread olarak işaretledik bu sayede bu thread bir işlem gibi çalışır
            // ana thread olarak geçer ve sonlanınca bütün threadler sonlanır 
            // java normelde tum threadler işini tammalanınca sonlanırken
            // dameon thread görevini yerine getirince sonlanır
            messageListener.start();
        } catch (Exception e) {

        }
    }

    private void sendMessage() {
        String mesaj = MesajGirisi.getText();
        // mesaj göndeirldikten sonra mesaj gönderme kutusunun içi temizlenir
        MesajGirisi.setText("");

        try {
            output.writeUTF(mesaj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendMessage(String message) {
        SohbetAlani.append(message + "\n");
        //sunucudan gelen mesaj mesaj ekranına eklenir
    }
}
