import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    
    public String envoyerMessage(DatagramSocket socket, InetAddress serverAddress, int serverPort, String message) {
        try {
            // Configurer et envoyer le paquet
            DatagramPacket packet = new DatagramPacket(
                message.getBytes(),
                message.getBytes().length,
                serverAddress,
                serverPort
            );
            socket.send(packet);

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
}
