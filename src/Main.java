import java.io.IOException;
import java.util.HashMap;

public class Main {
    public static String extractMac(String str) {
        if(str == null)
            return null;

        String value = str.toUpperCase().replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");//remove space
        int s = value.indexOf("MAC=");
        int e = value.indexOf(";");
        String mac = value.substring(s+4, e);
        return mac;
    }

    private static void executeWOL(String macStr, String ipStr) {
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


    public static void main(String[] args) {
        String searchTarget = "urn:dial-multiscreen-org:service:dial:1";

        /* Find upnp device */
        UpnpClient upnpc = new UpnpClient(searchTarget);
        HashMap<String, String> response = upnpc.discoverDevice();

        String wakeupField = response.get("WAKEUP");
        String locationField = response.get("LOCATION");

        System.out.println("WOL: " + wakeupField);
        System.out.println("DEVLOC: " + locationField);      

        /* Get XML description about device description */
        String desc = upnpc.getDesc(locationField);      
        String scpdURL = null;

        upnpc.setDeviceDescription(desc);       
        String scpdLocation = upnpc.getSCPDURL();

        /* Get XML description about state variables */
        if((desc != null) && (locationField != null) && (scpdLocation != null)) {
            scpdURL = locationField + scpdLocation;
            desc = upnpc.getDesc(scpdURL);
            System.out.println("State Variables: " + desc);
        }

        /* Turn power on */
        String macStr = extractMac(wakeupField);
        String ipStr = "192.168.0.255";
        executeWOL(macStr, ipStr);
    }
}
