import java.net.DatagramSocket;
import java.util.Scanner;

public class Spectateur extends Client {

    private DatagramSocket spectateurSocket;

    public Spectateur() throws Exception {
        this.spectateurSocket = new DatagramSocket();
    }

    public void start() {

        Scanner scanner = new Scanner(System.in);

        try {
            boolean connexionReussie = connexionServeur("spectateur", spectateurSocket, scanner);
            if (!connexionReussie) {
                System.out.println("Impossible de se connecter au serveur");
                return;
            } else {
                System.out.println("Vous êtes connecté en tant que spectateur");
            }

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

            System.out.println("Commandes disponibles :");
            System.out.println("- Pour envoyer un message : tapez simplement votre message");
            System.out.println("- Pour afficher la liste des personnes : /personnes");
            System.out.println("- Pour envoyer un message privé : tapez votre message précédé de /mp /pseudoPersonne");

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

    public static void main(String[] args) {
        try {
            Spectateur spec = new Spectateur();
            spec.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
