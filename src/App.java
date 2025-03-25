import java.util.Map;

public class App {
    public static void main(String[] args) throws Exception {
        
        // Scan ports 1 to 65535
        Map<Integer, Boolean> portStatus = Client.scanUDPPorts("localhost", 5173, 5174);
        System.out.println(portStatus);
    }
}
