import java.net.DatagramSocket;
import java.net.InetAddress;

public class Spectateur extends Client{
    
    private DatagramSocket spectateurSocket;

    public Spectateur() throws Exception{
        this.spectateurSocket = new DatagramSocket();
    }

    public void start(){
        try {
            InetAddress serverAddress = InetAddress.getByName("10.42.189.223");
            int serverPort = 8080;
            String spectateurMessage = "spectateur";
            envoyerMessage(spectateurSocket, serverAddress, serverPort, spectateurMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        try {
            Spectateur spec = new Spectateur();
            spec.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
