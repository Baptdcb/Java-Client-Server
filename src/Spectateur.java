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
            connexionServeur("spectateur", spectateurSocket);
            String responseConnection = recevoirMessage(spectateurSocket);
            System.out.println("Serveur : " + responseConnection);

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

            System.out.println("Vous pouvez envoyer des messages de chat :");

            while (true) {
                String input = scanner.nextLine();
                envoyerChat(spectateurSocket, input);
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
