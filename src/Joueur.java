import java.net.DatagramSocket;
import java.net.InetAddress;

public class Joueur extends Client{

    
    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            Joueur joueur = new Joueur();

            InetAddress serverAddress = InetAddress.getByName("10.42.189.223");
            int serverPort = 8080;
            Boolean sonTour = false;
            // Envoyer un message vide au serveur
            String joueurMessage = "joueur";
            String response = joueur.envoyerMessage(clientSocket, serverAddress, serverPort, joueurMessage);
            System.out.println("Réponse du serveur : " + response);
            System.out.println("le joueur numéro 1 commence");

            String inputMessage = "";
            while (!inputMessage.equals("exit")) {
                // Demander un message dans la console
                System.out.println("Entrez un message :");
                inputMessage = System.console().readLine();
                // Envoyer le message au serveur et afficher la réponse
                response = joueur.envoyerMessage(clientSocket, serverAddress, serverPort, inputMessage);
                System.out.println("Réponse du serveur : " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
