import java.net.DatagramSocket;
import java.net.InetAddress;

public class Spectateur extends Client{
    
    public static void main(String[] args){
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            Spectateur spectateur = new Spectateur();

            InetAddress serverAddress = InetAddress.getByName("10.42.167.154");
            int serverPort = 8080;

            String emptyMessage = "";
            spectateur.envoyerMessage(clientSocket, serverAddress, serverPort, emptyMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
