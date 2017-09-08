import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class WakeOnLan {
    private String ipStr = "";
    private String macStr = "";
    private final int PORT = 100;    

    public WakeOnLan(String ipStr, String macStr) {
        this.ipStr = ipStr;
        this.macStr = macStr;
    }

    public void wakeUp() {

        try {
            byte[] data = makePacketData();

            InetAddress address = InetAddress.getByName(this.ipStr);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, this.PORT);
            DatagramSocket socket = new DatagramSocket();

            System.out.println("Sent WOL packet to " + address.getHostAddress() + " MAC: " + this.macStr);

            socket.send(packet);
            socket.close();

            System.out.println("Done");
        }
        catch (Exception e) {
            System.out.println("Failed to send Wake-on-LAN packet: + e");
            System.exit(1);
        }

    }

    private byte[] makePacketData() {
        byte[] macBytes = getMacBytes(this.macStr);
        byte[] packetData = new byte[6 + 16 * macBytes.length];

        /* set magic packet */
        for (int i = 0; i < 6; i++) {
            packetData[i] = (byte) 0xff;
        }

        /* set mac * 16 */
        for (int i = 6; i < packetData.length; i += macBytes.length) {
            System.arraycopy(macBytes, 0, packetData, i, macBytes.length);
        }
        return packetData;
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");

        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

}

