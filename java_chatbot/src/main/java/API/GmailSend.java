package API;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class GmailSend {

    private static final String GMAIL_SEND_ENDPOINT = "https://www.googleapis.com/gmail/v1/users/me/messages/send";
    private static final GoogleAuth googleAuth = new API.GoogleAuth();
    private static final String authorizationCode = googleAuth.getAuthenticationCode();
    private static final Map<String, String> accessToken = googleAuth.getAccessToken(authorizationCode);
    private static final String ACCESS_TOKEN = accessToken.get("accessToken");

    public static void sendEmail(String to, String subject, String body) {
        try {
            // Create the email message in the required format
            String emailJson = createEmailJson(to, subject, body);

            // Send the email using Gmail API
            sendEmailViaGmailAPI(emailJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createEmailJson(String to, String subject, String body) {
        // Construct the email message in JSON format
        String emailJson = "{\n" +
                "  \"raw\": \"" + encodeMessage(to, subject, body) + "\"\n" +
                "}";
        return emailJson;
    }

    private static String encodeMessage(String to, String subject, String body) {
        // Construct the raw email message and encode it in base64 format
        String from = "YOUR_EMAIL@gmail.com"; // Replace with your email address
        String emailContent = "From: " + from + "\r\n" +
                "To: " + to + "\r\n" +
                "Subject: " + subject + "\r\n" +
                "\r\n" + body;
        return Base64.getUrlEncoder().encodeToString(emailContent.getBytes(StandardCharsets.UTF_8));
    }

    private static void sendEmailViaGmailAPI(String emailJson) throws Exception {
        // Create a connection to the Gmail API endpoint
        URL url = new URL(GMAIL_SEND_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Send the email message
        DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
        outputStream.writeBytes(emailJson);
        outputStream.flush();
        outputStream.close();

        // Check the response code
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Email sent successfully!");
        } else {
            // Print error message
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine);
            }
            errorReader.close();
            System.err.println("Failed to send email. Error response: " + errorResponse.toString());
        }

        // Disconnect the connection
        conn.disconnect();
    }

    public static void main(String[] args) {
        String to = args[0];
        String subject = "Test Email";
        String body = "This is a test email sent using the Gmail API without external libraries.";
        sendEmail(to, subject, body);
    }
}
