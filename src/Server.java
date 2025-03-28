import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    public static void main(String[] args){
        try(DatagramSocket serverSocket = new DatagramSocket(8080)){
            System.out.println("Serveur en attente...");
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Message reçu : " + message);

                String response = "Message reçu : " + message;
                DatagramPacket responsPacket = new DatagramPacket(
                    response.getBytes(),
                    response.length(),
                    packet.getAddress(),
                    packet.getPort()
                );
                serverSocket.send(responsPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
