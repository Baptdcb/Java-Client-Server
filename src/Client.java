import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    protected String SERVER_ADDRESS = "10.138.122.54";
    protected int SERVER_PORT = 8080;

    public Client() {
    }

    public boolean envoyerMessage(DatagramSocket socket, String message) {
        try {
            DatagramPacket packet = new DatagramPacket(
                    message.getBytes(),
                    message.getBytes().length,
                    InetAddress.getByName(SERVER_ADDRESS),
                    SERVER_PORT);
            socket.send(packet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String recevoirMessage(DatagramSocket socket) {
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            // Cette méthode est bloquante - elle attend jusqu'à ce qu'un paquet soit reçu
            socket.receive(receivePacket);

            // Convertir les données reçues en chaîne de caractères
            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean connexionServeur(String type, DatagramSocket socket) {
        try {
            String joueurMessage = type;
            envoyerMessage(socket, joueurMessage);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void envoyerChat(DatagramSocket socket, String message) {
        envoyerMessage(socket, "CHAT:" + message);
    }
}
