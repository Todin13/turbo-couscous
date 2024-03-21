package API;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class apiController {
    

    private static void connexion() {
        GoogleAuth googleAuth = new API.GoogleAuth();
        String authorizationCode = googleAuth.getAuthenticationCode();
        Map<String, String> accessInfo = googleAuth.getAccessToken(authorizationCode);

        String filePath = "authToken.txt";
        
        File file = new File(filePath);

        try {
            // Create the file if it doesn't exist
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("File created successfully.");
            }
            
            // Create a FileWriter with append mode as false (overwrite mode)
            FileWriter fileWriter = new FileWriter(file, false);
            
            // Create a BufferedWriter object to write to the file
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            
            // Write the content to the file
            for (Map.Entry<String, String> entry : accessInfo.entrySet()) {
                bufferedWriter.write("\"" + entry.getKey() + "\": \"" + entry.getValue() + "\"");
                bufferedWriter.newLine();
            }
            
            // Close the BufferedWriter
            bufferedWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String readToken() {
        String filePath = "authToken.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            Map<String, String> tokenMap = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String key = parts[0].trim().replaceAll("^\"|\"$", "");;
                    String value = parts[1].trim().replaceAll("^\"|\"$", ""); // Remove leading and trailing quotes
                    tokenMap.put(key, value);
                }
            }

            // Extract and print the access token
            String accessToken = tokenMap.get("accessToken");
            return accessToken;
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] api) {
        connexion();
        // String to = "test@gmail.com";
        // String subject = "Test Email";
        // String body = "This is a test email sent using the Gmail API without external libraries.";
        // GmailSend.sendEmail(to, subject, body);

        // String summary = "Meeting with Client";
        // String description = "Discuss project progress and future plans.";
        // String startDateTime = "2024-03-23T17:00:00";
        // String endDateTime = "2024-03-23T19:00:00";
        // agendaGoogle.addEventToCalendar(summary, description, startDateTime, endDateTime);

    }
}