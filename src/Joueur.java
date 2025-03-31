import java.net.DatagramSocket;
import java.net.InetAddress;

public class Joueur extends Client{

    
    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            Joueur joueur = new Joueur();

            InetAddress serverAddress = InetAddress.getByName("10.42.167.154");
            int serverPort = 8080;
            Boolean sonTour = false;
            // Envoyer un message vide au serveur
            String emptyMessage = "";
            String response = joueur.envoyerMessage(clientSocket, serverAddress, serverPort, emptyMessage);
            System.out.println("Réponse du serveur : " + response);
            if(response.equals(" Vous êtes le joueur n°1")){
                sonTour = true;
            }
            System.out.println(sonTour);

            System.out.println("le joueur numéro 1 commence");

            String inputMessage = "";
            while (!inputMessage.equals("exit")) {

                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
