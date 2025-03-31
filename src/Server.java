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
        System.out.println("Serveur en attente...");
    }

    public void start(){

        byte[] buffer = new byte[1024];

        try{
            while(joueurs.size() < 2){
                connexionClient(serverSocket, buffer);
            }
            System.out.println("Debut du jeu\n");

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

    private void connexionClient(DatagramSocket serverSocket, byte[] buffer) throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(packet);

        String valuerIpPort = packet.getAddress().toString().replace("/", "") + ":" + packet.getPort();
        String ipAddress = packet.getAddress().toString();

        String message = new String(packet.getData(), 0, packet.getLength());

        if(message.equals("joueur")){
            int id = joueurs.size() + 1;
            System.out.println("Joueur " + id + " connecté (IP : " + ipAddress + ", Port : " + packet.getPort());
            String response = "Vous êtes le joueur n°" + id + "/2";  
            byte[] responseBytes = response.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(
                responseBytes,
                responseBytes.length,
                packet.getAddress(),
                packet.getPort()
                );
            serverSocket.send(responsePacket);
            joueurs.put(id, valuerIpPort);  
            System.out.println(joueurs.size() + " joueur(s) connecté(s)\n");
        } else if (message.equals("spectateur")){
            int id = spectateurs.size() + 1;
            System.out.println("SPectateur " + id + " connecté (IP : " + ipAddress + ", Port : " + packet.getPort());
            String response = "Vous êtes le spectateur n°" + id;  
            byte[] responseBytes = response.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(
                responseBytes,
                responseBytes.length,
                packet.getAddress(),
                packet.getPort()
                );
            serverSocket.send(responsePacket);
            spectateurs.put(id, valuerIpPort);
            System.out.println(joueurs.size() + " spectateur(s) connecté(s)\n");
        }
    }

    public void envoyerMessage () {
        
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

    public static void main(String[] args){
        try {
            Server server = new Server();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
