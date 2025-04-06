import java.net.DatagramSocket;
import java.util.Scanner;

/**
 * Classe Spectateur qui permet à un utilisateur de se connecter en tant que
 * spectateur
 * et d'observer une partie de morpion sans y participer directement.
 * Un spectateur peut envoyer des messages, voir la liste des personnes
 * connectées
 * et envoyer des messages privés.
 */
public class Spectateur extends Client {

    /** Socket UDP utilisé pour communiquer avec le serveur */

    private DatagramSocket spectateurSocket;

    /**
     * Constructeur de la classe Spectateur
     * Initialise un nouveau socket UDP pour la communication
     * 
     * @throws Exception Si la création du socket échoue
     */
    public Spectateur() throws Exception {
        this.spectateurSocket = new DatagramSocket();
    }

    /**
     * Démarre le client Spectateur
     * Cette méthode gère la connexion au serveur, la réception des messages
     * et les interactions utilisateur via la console
     */
    public void start() {

        Scanner scanner = new Scanner(System.in);

        try {
            // Connexion au serveur
            boolean connexionReussie = connexionServeur("spectateur", spectateurSocket, scanner);
            if (!connexionReussie) {
                return;
            } else {
                System.out.println("Vous êtes connecté en tant que spectateur");
            }

            // Création d'un thread pour la réception des messages du serveur
            Thread receptionThread = new Thread(() -> {
                while (true) {
                    try {
                        String message = recevoirMessage(spectateurSocket);
                        System.out.println(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            receptionThread.start();

            // Affichage des commandes disponibles
            System.out.println("Commandes disponibles :");
            System.out.println("- Pour envoyer un message : tapez simplement votre message");
            System.out.println("- Pour afficher la liste des personnes : /personnes");
            System.out.println("- Pour envoyer un message privé : tapez votre message précédé de /mp /pseudoPersonne");

            // Boucle principale pour traiter les entrées utilisateur
            while (true) {
                String input = scanner.nextLine();

                if (input.trim().equals("/personnes")) {
                    demanderPersonnes(spectateurSocket);
                } else if (input.startsWith("/mp")) {
                    if (input.length() >= 4) {
                        String message = input.substring(4);
                        envoyerMessagePrive(spectateurSocket, message);
                    } else {
                        System.out.println("Veuillez utiliser le bon format pour envoyer des messages");
                    }
                } else {
                    envoyerChat(spectateurSocket, input);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * Point d'entrée principal pour exécuter un client Spectateur
     * 
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        try {
            Spectateur spec = new Spectateur();
            spec.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
