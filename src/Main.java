import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static String extractMac(String str) {
        String value = str.toUpperCase().replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");//remove space
        int s = value.indexOf("MAC=");
        int e = value.indexOf(";");
        String mac = value.substring(s+4, e);
        return mac;
    }

    public static void main(String[] args) {
        String ipStr = "192.168.0.255";
        String searchTarget = "urn:dial-multiscreen-org:service:dial:1";       
        SSDP ssdp = new SSDP();
        HashMap<String, String> response = ssdp.discover(searchTarget);
        String macStr = extractMac(response.get("WAKEUP"));

        if(macStr != null) {
            System.out.println("Ok...TV Mac is successfully receved");
            System.out.println("Please Power off your TV");
            System.out.print("Please input 'y' if you want to turn on your TV (y): ");

            try {
                char ch = (char) System.in.read();
                if((ch == 'y') || (ch == 'Y')) {
                    WakeOnLan wol = new WakeOnLan(ipStr, macStr);
                    wol.wakeUp();
                } else {
                    System.out.println("Bye");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed");
        }
    }
}

