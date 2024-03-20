package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IPandLocationResolver {

    public static String[] getIPAndLocation() {
        String[] ipAndLocation = new String[4];
        try {
            // Fetching IP address
            URL ipify = new URL("https://api.ipify.org");
            BufferedReader ipReader = new BufferedReader(new InputStreamReader(ipify.openStream()));
            String ipAddress = ipReader.readLine();
            ipReader.close();

            // Fetching geolocation using IP address
            URL geoURL = new URL("http://ip-api.com/json/" + ipAddress);
            HttpURLConnection geoConnection = (HttpURLConnection) geoURL.openConnection();
            BufferedReader geoReader = new BufferedReader(new InputStreamReader(geoConnection.getInputStream()));
            StringBuilder geoResponse = new StringBuilder();
            String geoLine;
            while ((geoLine = geoReader.readLine()) != null) {
                geoResponse.append(geoLine);
            }
            geoReader.close();

            // Extracting city from geolocation response
            String city = ""; 
            String latitude = "";
            String longitude = "";
            String geoResponseString = geoResponse.toString();
            
            if (geoResponseString.contains("\"city\"")) {
                int startIndex = geoResponseString.indexOf("\"city\":") + "\"city\":".length();
                int endIndex = geoResponseString.indexOf(",", startIndex);
                city = geoResponseString.substring(startIndex, endIndex).replaceAll("\"", "").trim();
            }
            if (geoResponseString.contains("\"lat\"")) {
                int startIndex = geoResponseString.indexOf("\"lat\":") + "\"lat\":".length();
                int endIndex = geoResponseString.indexOf(",", startIndex);
                latitude = geoResponseString.substring(startIndex, endIndex).replaceAll("\"", "").trim();
            }
            if (geoResponseString.contains("\"lon\"")) {
                int startIndex = geoResponseString.indexOf("\"lon\":") + "\"lon\":".length();
                int endIndex = geoResponseString.indexOf(",", startIndex);
                longitude = geoResponseString.substring(startIndex, endIndex).replaceAll("\"", "").trim();
            }

            ipAndLocation[0] = ipAddress;
            ipAndLocation[1] = city;
            ipAndLocation[2] = latitude;
            ipAndLocation[3] = longitude;

            geoConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ipAndLocation;
    }

    public static void main(String[] args) {
        String[] ipAndLocation = getIPAndLocation();
        System.out.println("IP Address: " + ipAndLocation[0]);
        System.out.println("Location: " + ipAndLocation[1]);
        System.out.println("Latitude: " + ipAndLocation[2]);
        System.out.println("Longitude: " + ipAndLocation[3]);
    }
}