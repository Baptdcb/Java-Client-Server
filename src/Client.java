import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.net.InetSocketAddress;


public class Client {
    private String hostname;
    private int serverPort;
    
    public Client(String hostname, int serverPort) {
        this.hostname = hostname;
        this.serverPort = serverPort;
    }



    public String sendAndReceive(String message) {
        try (DatagramSocket socketClient = new DatagramSocket()) {

            
            // Préparation et envoi du message
            InetAddress adresseClient = InetAddress.getByName(this.hostname);
            byte[] envoyees = message.getBytes();
            DatagramPacket messageEnvoye = new DatagramPacket(envoyees, envoyees.length, 
                                                            adresseClient, this.serverPort);
            socketClient.send(messageEnvoye);
            
            // Réception de la réponse
            byte[] recues = new byte[1024];
            DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
            socketClient.receive(paquetRecu);
            
            // Traitement de la réponse
            String reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
            System.out.println("Depuis le serveur: " + reponse);

            // libérer le canal
            socketClient.close();

            return reponse;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<Integer, Boolean> scanUDPPorts(String ipAddress, int startPort, int endPort) {
        Map<Integer, Boolean> portStatus = new HashMap<>();
        
        for (int port = startPort; port <= endPort; port++) {
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(null);
                socket.bind(new InetSocketAddress(ipAddress, port));
                portStatus.put(port, false);
                System.out.println("Port " + port + " is closed (libre) on IP " + ipAddress);
            } catch (BindException e) {
                portStatus.put(port, true);
                System.out.println("Port " + port + " is open (occupé) on IP " + ipAddress);
            } catch (SocketException e) {
                System.err.println("Error checking port " + port + " on IP " + ipAddress + ": " + e.getMessage());
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
        }
        
        return portStatus;
    }

    
}