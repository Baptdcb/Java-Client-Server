import java.net.DatagramSocket;
import java.util.Scanner;

public class Joueur extends Client {

    private DatagramSocket clientSocket;

    public Joueur() throws Exception {
        this.clientSocket = new DatagramSocket();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        try {
            boolean connexionReussie = connexionServeur("joueur", clientSocket, scanner);
            if (!connexionReussie) {
                System.out.println("Impossible de se connecter");
                return;
            } else {
                System.out.println("Vous êtes connecté en tant que joueur");
            }

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

            System.out.println("Commandes disponibles :");
            System.out.println("- Pour jouer : /jouer ligne,colonne (ex: /jouer 0,1)");
            System.out.println("- Pour envoyer un message : tapez simplement votre message");

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

                // if (response.equals(" Vous êtes le joueur n°1")) {
                // sonTour = true;
                // }
                // System.out.println(sonTour);
                // System.out.println("le joueur numéro 1 commence");
                // String inputMessage = "";
            }

            // while (!inputMessage.equals("exit")) {

            // }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    public void envoyerCoup(DatagramSocket socket, String coup) {
        envoyerMessage(socket, "COUP:" + coup);
    }

    public static void main(String[] args) {
        try {
            Joueur joueur = new Joueur();
            joueur.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
