import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Classe Client, permet d'avoir des méthodes communes pour les classes Joueur
 * et Spectateur. Gère la communication réseau via UDP.
 */
public class Client {

    /** L'adresse IP de la machine sur laquelle est exécuté le serveur */
    protected String SERVER_ADDRESS = "192.168.0.15";

    /** Port de la machine sur lequel le serveur est exéctuté */
    protected int SERVER_PORT = 8080;

    /**
     * Constructeur par défaut
     * Initialise un client avec les valeurs par défaut pour l'adresse et le port du
     * serveur
     */
    public Client() {
    }

    /**
     * Envoie un message au serveur via le socket UDP spécifié
     * 
     * @param socket  Le socket UDP à utiliser pour l'envoi
     * @param message Le contenu du message à envoyer
     * @return true si l'envoi a réussi, false sinon
     */
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

    /**
     * Reçoit un message du serveur via le socket UDP spécifié
     * Cette méthode est bloquante jusqu'à réception d'un message
     * 
     * @param socket Le socket UDP à utiliser pour la réception
     * @return Le contenu du message reçu ou null en cas d'erreur
     */
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

    /**
     * Établit une connexion avec le serveur en envoyant le type de client
     * (joueur/spectateur)
     * et un pseudo. Gère les tentatives répétées si le pseudo est déjà pris.
     * 
     * @param type    Le type de client ("joueur" ou "spectateur")
     * @param socket  Le socket UDP à utiliser pour la communication
     * @param scanner Le scanner pour lire les entrées utilisateur
     * @return true si la connexion a réussi, false sinon
     */
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
                    String reponse = recevoirMessage(socket);

                    if (reponse != null) {
                        if (reponse.contains("Une personne existe déjà sous ce pseudo")) {
                            System.out.println("Erreur: " + reponse);
                            System.out.println("Veuillez choisir un autre pseudo.");
                        } else {
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

    /**
     * Envoie un message de chat au serveur
     * Le message est préfixé par "CHAT:" pour indiquer son type
     * 
     * @param socket  Le socket UDP à utiliser pour l'envoi
     * @param message Le contenu du message de chat
     */
    public void envoyerChat(DatagramSocket socket, String message) {
        envoyerMessage(socket, "CHAT:" + message);
    }

    /**
     * Demande la liste des personnes connectées au serveur
     * Envoie une requête "PERSONNES" au serveur
     * 
     * @param socket Le socket UDP à utiliser pour l'envoi
     */
    public void demanderPersonnes(DatagramSocket socket) {
        envoyerMessage(socket, "PERSONNES");
    }

    /**
     * Envoie un message privé à un autre utilisateur via le serveur
     * Le message est préfixé par "MP:" pour indiquer son type
     * 
     * @param socket  Le socket UDP à utiliser pour l'envoi
     * @param message Le message privé au format "/mp /destinataire contenu du
     *                message"
     */
    public void envoyerMessagePrive(DatagramSocket socket, String message) {
        envoyerMessage(socket, "MP:" + message);
    }
}
