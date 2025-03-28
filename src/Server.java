import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class Server {   
    
    public static void main(String[] args){
        try(DatagramSocket serverSocket = new DatagramSocket(8080)){
            System.out.println("Serveur en attente...");
            byte[] buffer = new byte[1024];
            
            HashMap<String, Integer> joueurs = new HashMap<String, Integer>();

            while (joueurs.size() < 2) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);

                String ipAddress = packet.getAddress().toString();

                joueurs.put(ipAddress, joueurs.size()+1);

                System.out.println("Serveur : IP Client:" + packet.getAddress() + " Joueur n°" + joueurs.get(ipAddress));

                String response = " Vous estes le joueur n°" + joueurs.get(ipAddress);  
                byte[] responseBytes = response.getBytes();
                DatagramPacket responsPacket = new DatagramPacket(
                    responseBytes,
                    responseBytes.length,
                    packet.getAddress(),
                    packet.getPort()
                );
                serverSocket.send(responsPacket);
            }

            System.out.println("Debut du jeu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
