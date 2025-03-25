import java.net.BindException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.net.InetSocketAddress;


public class Client {

    public static Map<Integer, Boolean> scanUDPPorts(String ipAddress, int startPort, int endPort) {
        Map<Integer, Boolean> portStatus = new HashMap<>();
        
        for (int port = startPort; port <= endPort; port++) {
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(null);
                socket.bind(new InetSocketAddress(ipAddress, port));
                portStatus.put(port, false);
                System.out.println("Port " + port + " is closed (libre) on IP " + ipAddress);
            } catch (BindException e) {
                portStatus.put(port, true);
                System.out.println("Port " + port + " is open (occupé) on IP " + ipAddress);
            } catch (SocketException e) {
                System.err.println("Error checking port " + port + " on IP " + ipAddress + ": " + e.getMessage());
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
        }
        
        return portStatus;
    }
}