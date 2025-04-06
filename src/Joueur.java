import java.net.DatagramSocket;
import java.util.Scanner;

/**
 * Classe Joueur qui permet à un utilisateur de se connecter en tant que
 * participant actif
 * dans une partie de morpion.
 * Un joueur peut jouer des coups sur la grille, envoyer des messages, voir la
 * liste des personnes connectées
 * et envoyer des messages privés.
 */
public class Joueur extends Client {

    /** Socket UDP utilisé pour communiquer avec le serveur */
    private DatagramSocket clientSocket;

    /**
     * Constructeur de la classe Joueur
     * Initialise un nouveau socket UDP pour la communication
     * 
     * @throws Exception Si la création du socket échoue
     */
    public Joueur() throws Exception {
        this.clientSocket = new DatagramSocket();
    }

    /**
     * Démarre le client Joueur
     * Cette méthode gère la connexion au serveur, la réception des messages,
     * l'envoi des coups et les interactions utilisateur via la console
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);

        try {
            // Connexion au serveur
            boolean connexionReussie = connexionServeur("joueur", clientSocket, scanner);
            if (!connexionReussie) {
                return;
            } else {
                System.out.println("Vous êtes connecté en tant que joueur");
            }

            // Création d'un thread pour la réception des messages du serveur
            Thread receptionThread = new Thread(() -> {
                while (true) {
                    try {
                        String message = recevoirMessage(clientSocket);
                        System.out.println(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            receptionThread.start();

            // Affichage des commandes disponibles
            System.out.println("Commandes disponibles :");
            System.out.println("- Pour jouer : /jouer ligne,colonne (ex: /jouer 1,1)");
            System.out.println("- Pour envoyer un message : tapez simplement votre message");
            System.out.println("- Pour afficher la liste des personnes : /personnes");
            System.out.println("- Pour envoyer un message privé : tapez votre message précédé de /mp /pseudoPersonne");

            // Boucle principale pour traiter les entrées utilisateur
            while (true) {

                String input = scanner.nextLine();

                if (input.startsWith("/jouer")) {
                    if (input.length() >= 8) {
                        String coup = input.substring(7);
                        envoyerCoup(clientSocket, coup);
                    } else {
                        System.out.println("Veuillez utiliser le bon format pour jouer");
                    }
                } else if (input.trim().equals("/personnes")) {
                    demanderPersonnes(clientSocket);
                } else if (input.startsWith("/mp")) {
                    if (input.length() >= 4) {
                        String message = input.substring(4);
                        envoyerMessagePrive(clientSocket, message);
                    } else {
                        System.out.println("Veuillez utiliser le bon format pour envoyer des messages");
                    }
                }

                else {
                    envoyerChat(clientSocket, input);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * Envoie un coup au serveur
     * Le message est préfixé par "COUP:" pour indiquer son type
     * 
     * @param socket Le socket UDP à utiliser pour l'envoi
     * @param coup   Le coup à jouer au format "ligne,colonne" (ex: "1,2")
     */
    public void envoyerCoup(DatagramSocket socket, String coup) {
        envoyerMessage(socket, "COUP:" + coup);
    }

    /**
     * Point d'entrée principal pour exécuter un client Joueur
     * 
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        try {
            Joueur joueur = new Joueur();
            joueur.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
