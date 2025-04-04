import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private DatagramSocket serverSocket;
    private HashMap<String, String> joueurs;
    private String[] listeJoueurs;
    private HashMap<String, String> spectateurs;
    private Grille grille;
    private int joueurActuel;

    public Server() throws Exception {
        this.serverSocket = new DatagramSocket(8080);
        this.joueurs = new HashMap<>();
        this.listeJoueurs = new String[2];
        this.spectateurs = new HashMap<>();
        this.grille = new Grille();
        this.joueurActuel = 0;
        System.out.println("Serveur en attente...");
    }

    public void start() {

        byte[] buffer = new byte[1024];

        try {
            while (joueurs.size() < 2) {
                connexionClient(serverSocket, buffer);
            }

            diffuserMessage("Debut du jeu, grille initiale :\n");
            diffuserGrille();

            diffuserMessage("\nC'est au tour du joueur " + listeJoueurs[joueurActuel]);
            envoyerMessageAuClient(serverSocket, joueurs.get(listeJoueurs[joueurActuel]), "C'est votre tour");

            boolean annoncerTour = false;

            while (true) {

                if (annoncerTour) {
                    diffuserMessage("\nC'est au tour du joueur " + listeJoueurs[joueurActuel]);
                    envoyerMessageAuClient(serverSocket, joueurs.get(listeJoueurs[joueurActuel]), "C'est votre tour");
                    annoncerTour = false;
                }

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                serverSocket.receive(packet);

                String adresseEnvoyeur = packet.getAddress().toString().replace("/", "");
                int portEnvoyeur = packet.getPort();
                String infosEnvoyeur = adresseEnvoyeur + ":" + portEnvoyeur;
                String message = new String(packet.getData(), 0, packet.getLength());

                if (message.startsWith("COUP:")) {
                    if (joueurs.get(listeJoueurs[joueurActuel]).equals(infosEnvoyeur)) {
                        envoyerReponse("Serveur : Coup joué", packet);
                        System.out.println("Coup joué : " + message);
                        joueurActuel = (joueurActuel == 0) ? 1 : 0;
                        annoncerTour = true;
                    } else {
                        envoyerReponse("Serveur : Ce n'est pas ton tour", packet);
                    }
                } else if (message.startsWith("CHAT:")) {
                    String expediteur = trouverExpediteur(infosEnvoyeur);
                    diffuserMessage("Message de " + expediteur + " : " + message.substring(5), expediteur);
                } else if (message.startsWith("MP:")) {
                    String contenu = message.substring(3).trim();
                    String[] parties = contenu.split(" ", 2);

                    if (parties.length < 2) {
                        envoyerReponse("Format invalide, utilisez la forme /destinataire message", packet);
                    } else {
                        String destinataire = parties[0].substring(1);
                        String contenuMessage = parties[1];
                        String adresse = trouverPersonne(destinataire);

                        if (adresse.isEmpty()) {
                            envoyerReponse("Destinataire introuvable", packet);
                        } else {
                            String expediteur = trouverExpediteur(infosEnvoyeur);
                            envoyerReponse("Serveur : Message envoyé", packet);
                            envoyerMessageAuClient(serverSocket, adresse,
                                    "Message de " + expediteur + " : " + contenuMessage);
                        }
                    }

                } else if (message.startsWith("PERSONNES")) {
                    envoyerReponse(listePersonnes(), packet);
                }
            }
        } catch (

        Exception e) {
            e.printStackTrace();
        }
    }

    private void connexionClient(DatagramSocket serverSocket, byte[] buffer) throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        serverSocket.receive(packet);

        String valuerIpPort = packet.getAddress().toString().replace("/", "") + ":" + packet.getPort();
        String ipAddress = packet.getAddress().toString();

        String message = new String(packet.getData(), 0, packet.getLength());

        if (message.startsWith("joueur") || message.startsWith("spectateur")) {
            String[] infosClient = message.split(" ");

            if (verifierPseudo(infosClient[1])) {
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

    // Envoyer un message de réponse au client à partir d'un packet reçu
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

    private void diffuserMessage(String message) throws Exception {

        for (Map.Entry<String, String> entry : joueurs.entrySet()) {
            envoyerMessageAuClient(serverSocket, entry.getValue(), message);
        }

        for (Map.Entry<String, String> entry : spectateurs.entrySet()) {
            envoyerMessageAuClient(serverSocket, entry.getValue(), message);
        }
    }

    private void diffuserMessage(String message, String expediteur) throws Exception {

        for (Map.Entry<String, String> entry : joueurs.entrySet()) {
            if (entry.getKey() != expediteur) {
                envoyerMessageAuClient(serverSocket, entry.getValue(), message);
            }
        }

        for (Map.Entry<String, String> entry : spectateurs.entrySet()) {
            if (entry.getKey() != expediteur) {
                envoyerMessageAuClient(serverSocket, entry.getValue(), message);
            }
        }
    }

    private void diffuserGrille() {
        try {
            diffuserMessage(grille.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    private String listePersonnes() {
        String value = "Joueurs : " + listeJoueurs[0] + ", " + listeJoueurs[1] + "\n";
        value += "Spectateurs : ";
        for (Map.Entry<String, String> entry : spectateurs.entrySet()) {
            value += entry.getKey() + ", ";
        }
        return value.substring(0, value.length() - 2);
    }

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

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
