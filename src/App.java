import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
        int port_serv = 5173;
        String hostname = "localhost";
        
        // Démarrer le serveur dans un thread séparé
        Thread serverThread = new Thread(() -> {
            try {
                Server server = new Server(hostname, port_serv);
                while (true) {
                    String receptionMessage = server.sendAndReceive();
                    System.out.println("Serveur reçoit: " + receptionMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        
        // Attendre que le serveur démarre
        System.out.println("Serveur démarre...");
        Thread.sleep(1000);
        
        // Client envoie un message
        Client client = new Client(hostname, port_serv);
        String message = client.sendAndReceive("Bonjour, serveur ! Je suis le client.");
        System.out.println("Client reçoit: " + message);
    }
}