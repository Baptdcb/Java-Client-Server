import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Server {
    public Server(InetSocketAddress address, String message){ 
    
        try{
            // 1 - Création du canal
            DatagramSocket socketServeur = new DatagramSocket(null);
            // 2 - Réservation du port

            socketServeur.bind(address);
            byte[] recues = new byte[1024]; // tampon d'émission
            byte[] envoyees; // tampon de réception
            // 3 - Recevoir
            DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
            socketServeur.receive(paquetRecu);
            System.out.println("Reçu: " + message);
            // 4 - Émettre
            InetAddress adrClient = paquetRecu.getAddress(); int prtClient = paquetRecu.getPort();
            String reponse = "Accusé de réception"; envoyees = reponse.getBytes();
            DatagramPacket paquetEnvoye = new DatagramPacket(envoyees, envoyees.length, adrClient, prtClient);
            socketServeur.send(paquetEnvoye);
            // 5 - Libérer le canal
            socketServeur.close();
        }catch(Exception e){
            System.err.println(e);
        }
    
    }
}