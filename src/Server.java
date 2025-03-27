import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Server {

    private DatagramSocket socket; 

    public Server(String host, int port) throws Exception {

        this.socket = new DatagramSocket(port);
        System.out.println("Serveur démarré sur le port " + port);
    }

    public String sendAndReceive() throws Exception {

        
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);

        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Message reçu : " + receivedMessage);
        
        String responseMessage =  "Accusé de réception";
        byte[] sendData = responseMessage.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, 
                                                      receivePacket.getAddress(), 
                                                      receivePacket.getPort());
        socket.send(sendPacket);
        
        return receivedMessage;
    }
    

}