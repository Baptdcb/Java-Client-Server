import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    public static void main(String[] args){
        try (DatagramSocket clientSocket = new DatagramSocket()){
            InetAddress serverAddress = InetAddress.getByName("10.42.189.223");
            int serverPort = 8080;

            String message = "Bonjour, je suis le client";
            DatagramPacket packet = new DatagramPacket(
                message.getBytes(),
                message.length(),
                serverAddress,
                serverPort
            );

            clientSocket.send(packet);
            System.out.println("Message envoyé au serveur");

            byte[] buffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            clientSocket.receive(responsePacket);

            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            System.out.println("Réponse du serveur : " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
