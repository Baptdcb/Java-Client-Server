import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    
    public String envoyerMessage(DatagramSocket socket, InetAddress serverAddress, int serverPort, String message) {
        try {
            // Configurer et envoyer le paquet
            DatagramPacket packet = new DatagramPacket(
                message.getBytes(),
                message.length(),
                serverAddress,
                serverPort
            );
            socket.send(packet);
            System.out.println("Message envoyé");

            // Recevoir la réponse
            byte[] buffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);

            // Retourner la réponse du serveur
            return new String(responsePacket.getData(), 0, responsePacket.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
