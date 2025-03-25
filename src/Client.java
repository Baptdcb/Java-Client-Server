import java.net.BindException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;


public class Client {

public static Map<Integer, Boolean> scanUDPPorts(int startPort, int endPort) {
    Map<Integer, Boolean> portStatus = new HashMap<>();
    
    for (int port = startPort; port <= endPort; port++) {
        DatagramSocket socket = null;
        try {
            
            socket = new DatagramSocket(port);
            portStatus.put(port, false);
            System.out.println("Port " + port + " is closed (available)");
        } catch (BindException e) {
            portStatus.put(port, true);
            System.out.println("Port " + port + " is open (in use)");
        } catch (SocketException e) {
            System.err.println("Error checking port " + port + ": " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
    
    return portStatus;
}
}