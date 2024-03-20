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
import java.util.Scanner;

public class GmailSend {

    private static final String GMAIL_DRAFTS_CREATE_ENDPOINT = "https://www.googleapis.com/gmail/v1/users/me/drafts";
    private static final String GMAIL_SEND_ENDPOINT = "https://www.googleapis.com/gmail/v1/users/me/messages/send";
    private static final GoogleAuth googleAuth = new API.GoogleAuth();
    private static final String authorizationCode = googleAuth.getAuthenticationCode();
    private static final Map<String, String> accessToken = googleAuth.getAccessToken(authorizationCode);
    private static final String ACCESS_TOKEN = accessToken.get("accessToken");

    public static void sendOrSaveDraft(String to, String subject, String body) {
        try {
            // Create the email message in the required format
            String emailJson = createEmailJson(to, subject, body);

            // Create a draft using Gmail API
            String draftId = createDraftViaGmailAPI(emailJson);

            // Prompt the user to send or save the draft
            promptToSendOrSave(draftId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createEmailJson(String to, String subject, String body) {
        // Construct the email message in JSON format
        String emailJson = "{\n" +
                "  \"message\": {\n" +
                "    \"raw\": \"" + encodeMessage(to, subject, body) + "\"\n" +
                "  }\n" +
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

    private static String createDraftViaGmailAPI(String emailJson) throws Exception {
        // Create a connection to the Gmail API endpoint
        URL url = new URL(GMAIL_DRAFTS_CREATE_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Send the draft request
        DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
        outputStream.writeBytes(emailJson);
        outputStream.flush();
        outputStream.close();

        // Parse the response to get the draft ID
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        conn.disconnect();

        // Extract the draft ID from the response
        String jsonResponse = response.toString();
        int draftIdIndex = jsonResponse.indexOf("\"id\":");
        if (draftIdIndex != -1) {
            int startIndex = draftIdIndex + 6;
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            return jsonResponse.substring(startIndex, endIndex);
        } else {
            throw new Exception("Failed to create draft. Draft ID not found in the response.");
        }
    }

    private static void promptToSendOrSave(String draftId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Draft created with ID: " + draftId);
        System.out.print("Do you want to send the email now? (yes/no): ");
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.equals("yes")) {
            sendDraft(draftId);
        } else if (input.equals("no")) {
            System.out.println("Draft saved. You can send it later.");
        } else {
            System.out.println("Invalid input. Please enter 'yes' to send the email or 'no' to save it as a draft.");
        }
        scanner.close();
    }

    private static void sendDraft(String draftId) {
        try {
            // Send the draft using Gmail API
            URL url = new URL(GMAIL_SEND_ENDPOINT + "?id=" + draftId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
            conn.setDoOutput(true);

            // Check the response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Draft sent successfully!");
            } else {
                // Print error message
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();
                System.err.println("Failed to send draft. Error response: " + errorResponse.toString());
            }

            // Disconnect the connection
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String to = "recipient@example.com";
        String subject = "Test Email Draft";
        String body = "This is a test email draft created using the Gmail API without external libraries.";
        sendOrSaveDraft(to, subject, body);
    }
}
