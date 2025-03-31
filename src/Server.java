import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Server {   
    
    private DatagramSocket serverSocket;
    private HashMap<Integer, String> joueurs;
    private HashMap<Integer, String> spectateurs;

    public Server() throws Exception{
        this.serverSocket = new DatagramSocket(8080);
        this.joueurs = new HashMap<>();
        this.spectateurs = new HashMap<>();
    }

    public void start(){

    }

    public static void main(String[] args){
        try(DatagramSocket serverSocket = new DatagramSocket(8080)){
            
            System.out.println("Serveur en attente...");
            byte[] buffer = new byte[1024];
            
            HashMap<Integer, String> joueurs = new HashMap<>();
            HashMap<Integer, String> spectateurs = new HashMap<>();

            while (joueurs.size() < 2) {
                int identifiant = joueurs.size() + 1;
                String valuerIpPort = connexionClient(serverSocket, buffer, identifiant);
                joueurs.put(identifiant, valuerIpPort);
                System.out.println(joueurs.size() + " joueur(s) connecté(s)");
            }
            System.out.println("Debut du jeu");

            while (true) {
                for (int i = 1; i <= joueurs.size(); i++) {
                    String message = "C'est votre tour";
                    envoyerMessageAuClient(serverSocket, joueurs.get(i), message);
                    System.out.println("Message envoyé au joueur n°" + i+ " : " + message);


                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    serverSocket.receive(responsePacket);
                    String coupJoue = new String(responsePacket.getData(), 0, responsePacket.getLength());

                    // Vérification du numéro du joueur
                    String senderAddress = responsePacket.getAddress().toString().replace("/", "");
                    int senderPort = responsePacket.getPort();
                    String expectedAddressPort = joueurs.get(i);
                    
                    if (!expectedAddressPort.equals(senderAddress + ":" + senderPort)) {
                        System.out.println("Erreur : Message reçu d'un joueur inattendu !");
                        String messageErreur = "Erreur : Ce n'est pas votre tour";
                        envoyerMessageAuClient(serverSocket, senderAddress + ":" + senderPort, messageErreur);

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

    private String connexionClient(DatagramSocket serverSocket, byte[] buffer, int identifiant) throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(packet);

        String valuerIpPort = packet.getAddress().toString().replace("/", "") + ":" + packet.getPort();
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

        return valuerIpPort;
    }

    private void envoyerMessageAuClient(DatagramSocket serverSocket, String clientInfo, String message) throws Exception {
        String[] clientParts = clientInfo.split(":");
        InetAddress address = InetAddress.getByName(clientParts[0]);
        int port = Integer.parseInt(clientParts[1]);

        byte[] messageBytes = message.getBytes();
        DatagramPacket messagePacket = new DatagramPacket(
            messageBytes,
            messageBytes.length,
            address,
            port
        );
        serverSocket.send(messagePacket);
    }

    private void envoyerMessageBroadcast(){

    }
}
