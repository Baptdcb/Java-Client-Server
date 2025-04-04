import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {

    protected String SERVER_ADDRESS = "10.138.122.105";
    protected int SERVER_PORT = 8080;

    public Client() {
    }

    public boolean envoyerMessage(DatagramSocket socket, String message) {
        try {
            DatagramPacket packet = new DatagramPacket(
                    message.getBytes(),
                    message.getBytes().length,
                    InetAddress.getByName(SERVER_ADDRESS),
                    SERVER_PORT);
            socket.send(packet);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String recevoirMessage(DatagramSocket socket) {
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            socket.receive(receivePacket);

            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean connexionServeur(String type, DatagramSocket socket, Scanner scanner) {
        boolean connexionReussie = false;

        while (!connexionReussie) {
            try {
                System.out.println("Quel est votre pseudo ?");
                String username = scanner.nextLine().replace(" ", "");
                while (username.trim().equals("")) {
                    System.out.println("Pseudo incorrect, veuillez recommencer");
                    username = scanner.nextLine().replace(" ", "");
                }

                String message = type + " " + username;
                boolean messageEnvoye = envoyerMessage(socket, message);

                if (messageEnvoye) {
                    // Attendre la réponse du serveur
                    String reponse = recevoirMessage(socket);

                    if (reponse != null) {
                        if (reponse.contains("Une personne existe déjà sous ce pseudo")) {
                            System.out.println("Erreur: " + reponse);
                            System.out.println("Veuillez choisir un autre pseudo.");
                            // La boucle continue pour demander un nouveau pseudo
                        } else {
                            // Connexion réussie
                            connexionReussie = true;
                            return true;
                        }
                    } else {
                        System.out.println("Aucune réponse du serveur, veuillez réessayer.");
                    }
                } else {
                    System.out.println("Échec de l'envoi du message, veuillez réessayer.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public void envoyerChat(DatagramSocket socket, String message) {
        envoyerMessage(socket, "CHAT:" + message);
    }

    public void demanderPersonnes(DatagramSocket socket) {
        envoyerMessage(socket, "PERSONNES");
    }

    public void envoyerMessagePrive(DatagramSocket socket, String message) {
        envoyerMessage(socket, "MP:" + message);
    }
}
