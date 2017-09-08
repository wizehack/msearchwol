import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SSDP {
    public HashMap<String, String>  discover(String target) {
        /* create byte arrays to hold our send and response data */
        byte[] sendData = new byte[1024];

        /* our M-SEARCH data as a byte array */
        String sentance = "M-SEARCH * HTTP/1.1\r\n"
            + "HOST: 239.255.255.250:1900\r\n"
            + "MAN: \"ssdp:discover\"\r\n"
            + "MX: 2\r\n"
            + "ST: " + target + "\r\n"
            + "\r\n";
        sendData = sentance.getBytes();

        /* create a packet from our data destined for 239.255.255.250:1900 */
        DatagramPacket sendPacket = createPacket(sendData);
        DatagramPacket resPacket = sendPacket(sendPacket);
        HashMap<String, String> headers = new HashMap<String, String>();

        /* parse response */
        Pattern pattern = Pattern.compile("(.*): (.*)");
        String[] lines = new String(resPacket.getData()).split("\r\n");

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if(matcher.matches()) {
                headers.put(matcher.group(1).toUpperCase(), matcher.group(2));
            }
        }

        return headers;
    }

    private DatagramPacket sendPacket(DatagramPacket sendPacket) {
        /* send packet to the socket we're creating */
        byte[] receiveData = new byte[1024];
        DatagramSocket clientSocket = null;

        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        System.out.println("request to " + sendPacket.getAddress().getHostAddress());
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* recieve response and store in our receivePacket */
        System.out.println("wait....");

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            clientSocket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* close the socket */
        clientSocket.close();

        return receivePacket;
    }

    private DatagramPacket createPacket(byte[] sendData) {
        DatagramPacket sendPacket = null;
        try {
            sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("239.255.255.250"), 1900);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return sendPacket;
    }
}

