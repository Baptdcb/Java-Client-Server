
public class App {
    public static void main(String[] args) throws Exception {
        int port_serv = 5173;
        String hostname = "localhost";
        
        // Démarrer le serveur dans un thread séparé
        Thread serverThread = new Thread(() -> {
            try {
                Server server = new Server( port_serv);
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
        Client client_1 = new Client(hostname, port_serv);
        String message = client_1.sendAndReceive("Bonjour, serveur ! Je suis le client 1.");

        System.out.println("Client reçoit: " + message);

        Client client_2 = new Client(hostname, port_serv);
        String message2 = client_2.sendAndReceive("Bonjour, serveur ! Je suis le client 2.");

        System.out.println("Client reçoit: " + message2);
    }
}