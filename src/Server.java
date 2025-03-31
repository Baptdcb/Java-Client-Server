import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Server {   
    
    public static void main(String[] args){
        try(DatagramSocket serverSocket = new DatagramSocket(8080)){
            
            System.out.println("Serveur en attente...");
            byte[] buffer = new byte[1024];
            
            HashMap<Integer, String> joueurs = new HashMap<>();

            while (joueurs.size() < 2) {
                int identifiant = joueurs.size() + 1;
                String valuer_Ip_port = recevoirClient(serverSocket, buffer, identifiant);
                joueurs.put(identifiant, valuer_Ip_port);
                System.out.println(joueurs.size() + " joueur(s) connecté(s)");
            }
            System.out.println("Debut du jeu");

            while (true) {
                for (int i = 1; i <= joueurs.size(); i++) {
                    String message = "Fin du tour";
                    byte[] messageBytes = message.getBytes();
                    InetAddress address = InetAddress.getByName(joueurs.get(i).split(":")[0]);
                    int port = Integer.parseInt(joueurs.get(i).split(":")[1]);
                    
                    DatagramPacket messagePacket = new DatagramPacket(
                        messageBytes,
                        messageBytes.length,
                        address,
                        port);
                    serverSocket.send(messagePacket);
                    System.out.println("Message envoyé au joueur n°" + i);

                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    serverSocket.receive(responsePacket);
                    String coupJoue = new String(responsePacket.getData(), 0, responsePacket.getLength());

                    // Vérification du numéro du joueur
                    String senderAddress = responsePacket.getAddress().toString().replace("/", "");
                    int senderPort = responsePacket.getPort();
                    String expectedAddressPort = joueurs.get(i);
              
                    if (!expectedAddressPort.equals(senderAddress + ":" + senderPort)) {
                        System.out.println("Erreur : Message reçu d'un joueur inattendu !");
                        i=i-1;
                        continue;
                    }

                    System.out.println("Joueur n°" + i + " a joué : " + coupJoue);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String recevoirClient(DatagramSocket serverSocket, byte[] buffer, int identifiant) throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(packet);

        String valuer_Ip_port = packet.getAddress().toString().replace("/", "") + ":" + packet.getPort();
        String ipAddress = packet.getAddress().toString();

        System.out.println("Serveur : IP Client:" + ipAddress + " Port Client:" + packet.getPort()
                + " Joueur n°" + identifiant);

        String response = " Vous êtes le joueur n°" + identifiant;  
        byte[] responseBytes = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(
            responseBytes,
            responseBytes.length,
            packet.getAddress(),
            packet.getPort()
        );
        serverSocket.send(responsePacket);

        return valuer_Ip_port;
    }
}
