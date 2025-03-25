import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.net.InetSocketAddress;


public class Client {

    public Client (InetSocketAddress adress, String message){
        try {
            // 1 - Création du canal
            DatagramSocket socketClient = new DatagramSocket();
            InetAddress adresseClient = InetAddress.getByName("localhost");
            byte[] envoyees; // tampon d'émission
            byte[] recues = new byte[1024]; // tampon de réception
            // 2 - Émettre 
            envoyees = message.getBytes();
            DatagramPacket messageEnvoye = new DatagramPacket(envoyees, envoyees.length, adresseClient, 6666);
            socketClient.send(messageEnvoye);
            // 3 - Recevoir
            DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
            socketClient.receive(paquetRecu);
            String reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
            System.out.println("Depuis le serveur: " + reponse);
            // 4 - Libérer le canal
            socketClient.close();
        } catch (Exception e) {
            System.err.println(e);
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