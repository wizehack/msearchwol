import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class UpnpClient {
    private final String USER_AGENT = "WEBOS/4.0 UPnP/1.0 MyProduct/1.0";
    private String deviceDesc = null;
    private String deviceId = null;
    private SSDP ssdp = null;

    public UpnpClient(String deviceId) {
        this.ssdp = new SSDP();
        this.deviceId = deviceId;
    }

    public HashMap<String, String> discoverDevice() {
        HashMap<String, String> response = this.ssdp.discover(this.deviceId);
        return response;
    }

    public String getDesc(String location)
    {
        String url = location;
        URL httpURL = null;

        if(url == null)
            return null;

        try {
            if(url != null)
                httpURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection con = (HttpURLConnection) httpURL.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            System.out.println("Sending 'GET' request to URL : " + url);
            int responseCode = con.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setDeviceDescription(String descXML) {
        this.deviceDesc = descXML;
    }

    public String getSCPDURL()
    {
        if(this.deviceDesc == null)
            return null;

        String xml = this.deviceDesc;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            try {
                Document doc = db.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
                String value = find(doc.getDocumentElement(), "SCPDURL");

                if(value.indexOf("/") == 0)
                    value = value.substring(1);

                if(value != null)
                    return value;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String find(Node e, String tag) {
        String nodeName = e.getNodeName();
        String value = e.getTextContent();

        if (nodeName == tag) {
            return value;
        }

        NodeList childs = e.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node c = childs.item(i);
            value = find(c, tag);
            if(value != null)
                return value;
        }

        return null;
    }
}

