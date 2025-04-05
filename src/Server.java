import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe Server qui implémente un serveur de jeu de morpion en réseau.
 * Gère les connexions des joueurs et spectateurs, le déroulement du jeu,
 * la communication et les échanges de messages entre les clients.
 */
public class Server {

    /** Socket UDP pour la communication réseau */
    private DatagramSocket serverSocket;

    /** Map stockant les joueurs connectés (pseudo → adresse:port) */
    private HashMap<String, String> joueurs;

    /**
     * Tableau des pseudos des joueurs actifs (indice 0: joueur X, indice 1: joueur
     * O)
     */
    private String[] listeJoueurs;

    /** Map stockant les spectateurs connectés (pseudo → adresse:port) */
    private HashMap<String, String> spectateurs;

    /** Représentation de la grille de jeu */
    private Grille grille;

    /** Indice du joueur actuel (0 ou 1) */
    private int joueurActuel;

    /** Indique si on doit annoncer le tour du joueur actuel */
    private boolean annoncerTour;

    /**
     * Constructeur du serveur.
     * Initialise les structures de données et démarre le socket sur le port 8080.
     * 
     * @throws Exception Si l'initialisation du socket échoue
     */
    public Server() throws Exception {
        this.serverSocket = new DatagramSocket(8080);
        this.joueurs = new HashMap<>();
        this.listeJoueurs = new String[2];
        this.spectateurs = new HashMap<>();
        this.grille = new Grille();
        this.joueurActuel = 0;
        this.annoncerTour = false;
        System.out.println("Serveur en attente...");
    }

    /**
     * Méthode principale qui démarre le serveur et gère le déroulement du jeu.
     * Attend la connexion de deux joueurs, puis gère la partie et les messages.
     */
    public void start() {

        byte[] buffer = new byte[1024];

        try {
            // Attendre la connexion des deux joueurs
            while (joueurs.size() < 2) {
                connexionClient(serverSocket, buffer);
            }

            // Initialisation du jeu et premier tour
            diffuserMessage("Debut du jeu, grille initiale :\n");
            diffuserGrille();

            diffuserMessage("\nC'est au tour du joueur " + listeJoueurs[joueurActuel]);
            envoyerMessageAuClient(serverSocket, joueurs.get(listeJoueurs[joueurActuel]), "C'est votre tour");

            // Boucle principale du jeu
            while (true) {

                // Annoncer le tour si nécessaire
                if (annoncerTour) {
                    diffuserMessage("\nC'est au tour du joueur " + listeJoueurs[joueurActuel]);
                    envoyerMessageAuClient(serverSocket, joueurs.get(listeJoueurs[joueurActuel]), "C'est votre tour");
                    annoncerTour = false;
                }

                // Attendre et traiter les messages des clients
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);

                // Identifier l'expéditeur
                String adresseEnvoyeur = packet.getAddress().toString().replace("/", "");
                int portEnvoyeur = packet.getPort();
                String infosEnvoyeur = adresseEnvoyeur + ":" + portEnvoyeur;
                String message = new String(packet.getData(), 0, packet.getLength());
                String expediteur = trouverExpediteur(infosEnvoyeur);

                // Traiter les différents types de messages
                if (message.startsWith("COUP:")) {
                    jouer(infosEnvoyeur, message, packet, expediteur);
                } else if (message.startsWith("CHAT:")) {
                    diffuserMessage("Message de " + expediteur + " : " + message.substring(5), expediteur);
                } else if (message.startsWith("MP:")) {
                    envoyerMessagePrive(message, expediteur, packet);
                } else if (message.startsWith("PERSONNES")) {
                    envoyerReponse(listePersonnes(), packet);
                }
            }
        } catch (

        Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gère la connexion d'un nouveau client (joueur ou spectateur).
     * Vérifie la disponibilité du pseudo et ajoute le client à la structure
     * appropriée.
     * 
     * @param serverSocket Le socket du serveur
     * @param buffer       Le buffer pour recevoir les données
     * @throws Exception Si une erreur de communication se produit
     */
    private void connexionClient(DatagramSocket serverSocket, byte[] buffer) throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(packet);

        String valuerIpPort = packet.getAddress().toString().replace("/", "") + ":" + packet.getPort();
        String ipAddress = packet.getAddress().toString();

        String message = new String(packet.getData(), 0, packet.getLength());

        if (message.startsWith("joueur") || message.startsWith("spectateur")) {
            String[] infosClient = message.split(" ");

            if (verifierPseudo(infosClient[1])) {
                // Connexion d'un joueur
                if (infosClient[0].equals("joueur")) {
                    if (joueurs.size() < 2) {
                        System.out.println(
                                "Joueur " + infosClient[1] + " connecté (IP : " + ipAddress + ", Port : "
                                        + packet.getPort());
                        String response = "\nVous êtes le joueur " + infosClient[1] + "\n";
                        envoyerReponse(response, packet);
                        joueurs.put(infosClient[1], valuerIpPort);
                        if (joueurs.size() == 1) {
                            listeJoueurs[0] = infosClient[1];
                        } else if (joueurs.size() == 2) {
                            listeJoueurs[1] = infosClient[1];
                        }
                        System.out.println(joueurs.size() + " joueur(s) connecté(s)\n");
                    } else {
                        System.out.println("Tentative de connxion en tant que Joueur refusée - maximun atteint");
                        String response = "Nombre maximum de Joueurs atteint, connectez vous en tant que Spectateur";
                        envoyerReponse(response, packet);
                    }
                } else if (infosClient[0].equals("spectateur")) {
                    // Connexion d'un spectateur
                    System.out.println(
                            "Spectateur " + infosClient[1] + " connecté (IP : " + ipAddress + ", Port : "
                                    + packet.getPort());
                    String response = "Vous êtes le spectateur " + infosClient[1];
                    envoyerReponse(response, packet);
                    spectateurs.put(infosClient[1], valuerIpPort);
                    System.out.println(spectateurs.size() + " spectateur(s) connecté(s)\n");
                }
            } else {
                envoyerReponse("Une personne existe déjà sous ce pseudo", packet);
            }
        } else {
            envoyerReponse("Veuillez attendre le début de la partie pour envoyer un message", packet);
        }

    }

    /**
     * Traite un coup joué par un joueur.
     * Vérifie si c'est le tour du joueur, la validité du coup et l'applique sur la
     * grille.
     * Gère aussi les conditions de victoire et de match nul.
     * 
     * @param infosEnvoyeur L'adresse IP et le port de l'expéditeur
     * @param message       Le message contenant les coordonnées du coup
     * @param packet        Le paquet UDP reçu
     * @param expediteur    Le pseudo du joueur qui joue
     */
    private void jouer(String infosEnvoyeur, String message, DatagramPacket packet, String expediteur) {
        // Vérifier si c'est le tour du joueur
        if (joueurs.get(listeJoueurs[joueurActuel]).equals(infosEnvoyeur)) {
            String[] coup = message.substring(5).trim().split(",");
            if (coup.length != 2) {
                envoyerReponse("Veuillez utiliser le bon format", packet);
            } else {
                try {
                    // Conversion en indices
                    int ligne = Integer.parseInt(coup[0]) - 1;
                    int colonne = Integer.parseInt(coup[1]) - 1;

                    // Validation des coordonnées
                    if (ligne >= 0 && ligne < 3 && colonne >= 0 && colonne < 3) {

                        // Jouer le coup
                        boolean coupJoue = grille.jouer(joueurActuel, ligne, colonne);
                        if (coupJoue) {
                            // Partager la nouvelle grille
                            diffuserMessage("\n" + expediteur + " a joué, voici la grille : ", expediteur);
                            diffuserGrille();

                            char symbole = joueurActuel == 0 ? 'X' : 'O';

                            // Vérifier si le joueur a gagné
                            if (grille.verifierGagnant(symbole)) {
                                diffuserMessage(expediteur + " a gagné la partie \nNouvelle partie : ");
                                grille.initialiserGrille();
                                diffuserGrille();
                            } else {
                                // Vérifier si la grille est pleine
                                if (grille.grilleRemplie()) {
                                    diffuserMessage(
                                            "La grille est remplie, aucun joueur n'a gagné.\nNouvelle partie : ");
                                    grille.initialiserGrille();
                                    diffuserGrille();
                                } else {
                                    System.out.println("Coup joué : " + message.substring(5));
                                }
                            }
                            // Changer de joueur
                            joueurActuel = (joueurActuel == 0) ? 1 : 0;
                            annoncerTour = true;

                            // Envoi d'une réponse d'explication en cas de problème
                        } else {
                            envoyerReponse("La case est déjà prise, rejouez\n", packet);
                        }
                    } else {
                        envoyerReponse("Veuillez entrer des valeurs comprises entre 1 et 3", packet);
                    }
                } catch (Exception e) {
                    envoyerReponse("Veuillez entrer le bon format", packet);
                }
            }

        } else {
            envoyerReponse("Serveur : Ce n'est pas ton tour", packet);
        }
    }

    /**
     * Traite un message privé envoyé par un client.
     * Format: MP: /destinataire message
     * 
     * @param message    Le message complet reçu
     * @param expediteur Le pseudo de l'expéditeur
     * @param packet     Le paquet UDP reçu
     */
    private void envoyerMessagePrive(String message, String expediteur, DatagramPacket packet) {
        String contenu = message.substring(3).trim();
        String[] parties = contenu.split(" ", 2);

        try {
            if (parties.length < 2) {
                envoyerReponse("Format invalide, utilisez la forme /destinataire message", packet);
            } else {
                String destinataire = parties[0].substring(1);
                String contenuMessage = parties[1];
                String adresse = trouverPersonne(destinataire);

                if (adresse.isEmpty()) {
                    envoyerReponse("Destinataire introuvable", packet);
                } else {
                    envoyerReponse("Serveur : Message envoyé", packet);
                    envoyerMessageAuClient(serverSocket, adresse,
                            "Message de " + expediteur + " : " + contenuMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie une réponse à un client spécifique.
     * 
     * @param responseMessage Le message à envoyer
     * @param packet          Le paquet contenant les informations du destinataire
     */
    private void envoyerReponse(String responseMessage, DatagramPacket packet) {
        try {
            byte[] responseBytes = responseMessage.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(
                    responseBytes,
                    responseBytes.length,
                    packet.getAddress(),
                    packet.getPort());
            serverSocket.send(responsePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie un message à un client spécifique identifié par son adresse:port.
     * 
     * @param serverSocket Le socket du serveur
     * @param clientInfo   L'information du client au format "adresse:port"
     * @param message      Le message à envoyer
     * @throws Exception Si l'envoi échoue
     */
    private void envoyerMessageAuClient(DatagramSocket serverSocket, String clientInfo, String message)
            throws Exception {
        String[] clientParts = clientInfo.split(":");
        InetAddress address = InetAddress.getByName(clientParts[0]);
        int port = Integer.parseInt(clientParts[1]);

        byte[] messageBytes = message.getBytes();
        DatagramPacket messagePacket = new DatagramPacket(
                messageBytes,
                messageBytes.length,
                address,
                port);
        serverSocket.send(messagePacket);
    }

    /**
     * Diffuse un message à tous les clients connectés (joueurs et spectateurs).
     * 
     * @param message Le message à diffuser
     * @throws Exception Si l'envoi échoue
     */
    private void diffuserMessage(String message) throws Exception {
        // Envoi aux joueurs
        for (Map.Entry<String, String> entry : joueurs.entrySet()) {
            envoyerMessageAuClient(serverSocket, entry.getValue(), message);
        }

        // Envoi au spectateurs
        for (Map.Entry<String, String> entry : spectateurs.entrySet()) {
            envoyerMessageAuClient(serverSocket, entry.getValue(), message);
        }
    }

    /**
     * Diffuse un message à tous les clients sauf à l'expéditeur.
     * 
     * @param message    Le message à diffuser
     * @param expediteur Le pseudo de l'expéditeur à exclure
     * @throws Exception Si l'envoi échoue
     */
    private void diffuserMessage(String message, String expediteur) throws Exception {
        // Envoie aux joueurs (sauf expediteur)
        for (Map.Entry<String, String> entry : joueurs.entrySet()) {
            if (entry.getKey() != expediteur) {
                envoyerMessageAuClient(serverSocket, entry.getValue(), message);
            }
        }

        // Envoie aux spectateurs (sauf expediteur)
        for (Map.Entry<String, String> entry : spectateurs.entrySet()) {
            if (entry.getKey() != expediteur) {
                envoyerMessageAuClient(serverSocket, entry.getValue(), message);
            }
        }
    }

    /**
     * Diffuse l'état actuel de la grille à tous les clients.
     */
    private void diffuserGrille() {
        try {
            diffuserMessage(grille.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si un pseudo est disponible.
     * 
     * @param pseudo Le pseudo à vérifier
     * @return true si le pseudo est disponible, false sinon
     */
    private boolean verifierPseudo(String pseudo) {
        for (Map.Entry<String, String> entry : joueurs.entrySet()) {
            if (entry.getKey().toLowerCase().equals(pseudo.toLowerCase())) {
                return false;
            }
        }

        for (Map.Entry<String, String> entry : spectateurs.entrySet()) {
            if (entry.getKey().toLowerCase().equals(pseudo.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Génère une liste des personnes connectées.
     * 
     * @return Une chaîne contenant la liste formatée des joueurs et spectateurs
     */
    private String listePersonnes() {
        String value = "Joueurs : " + listeJoueurs[0] + ", " + listeJoueurs[1] + "\n";
        value += "Spectateurs : ";
        for (Map.Entry<String, String> entry : spectateurs.entrySet()) {
            value += entry.getKey() + ", ";
        }
        return value.substring(0, value.length() - 2);
    }

    /**
     * Trouve l'adresse réseau d'une personne à partir de son pseudo.
     * 
     * @param pseudo Le pseudo de la personne recherchée
     * @return L'adresse au format "adresse:port" ou une chaîne vide si non trouvée
     */
    private String trouverPersonne(String pseudo) {
        for (Map.Entry<String, String> entry : joueurs.entrySet()) {
            if (entry.getKey().toLowerCase().equals(pseudo.toLowerCase())) {
                return entry.getValue();
            }
        }

        for (Map.Entry<String, String> entry : spectateurs.entrySet()) {
            if (entry.getKey().toLowerCase().equals(pseudo.toLowerCase())) {
                return entry.getValue();
            }
        }
        return "";
    }

    /**
     * Trouve le pseudo d'un client à partir de son adresse réseau.
     * 
     * @param infosEnvoyeur L'adresse au format "adresse:port"
     * @return Le pseudo du client ou "Inconnu" si non trouvé
     */
    private String trouverExpediteur(String infosEnvoyeur) {
        String expediteur = "Inconnu";
        for (Map.Entry<String, String> entry : joueurs.entrySet()) {
            if (entry.getValue().equals(infosEnvoyeur)) {
                expediteur = entry.getKey();
                break;
            }
        }
        for (Map.Entry<String, String> entry : spectateurs.entrySet()) {
            if (entry.getValue().equals(infosEnvoyeur)) {
                expediteur = entry.getKey();
                break;
            }
        }
        return expediteur;
    }

    /**
     * Point d'entrée principal pour démarrer le serveur.
     * 
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
