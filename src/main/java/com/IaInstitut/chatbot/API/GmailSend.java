package com.IaInstitut.chatbot.API;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Scanner;

public class GmailSend {

    private static final String GMAIL_SEND_ENDPOINT = "https://www.googleapis.com/gmail/v1/users/me/messages/send";
    private static final String ACCESS_TOKEN = apiController.readToken();
    private static final String GMAIL_DRAFT_ENDPOINT = "https://www.googleapis.com/gmail/v1/users/me/drafts";

    public static void sendEmail(String to, String subject, String body) {
        try {
            if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
                System.out.println("One or more fields (to, subject, body) are empty. Email cannot be sent or saved as draft.");
                return;
            }

            // Create the email message in the required format
            String emailJson = createEmailJson(to, subject, body);

            // Prompt the user before sending the email
            System.out.println("Do you want to send the email? (yes/no)");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("yes")) {
                sendEmailViaGmailAPI(emailJson);
            } else if (response.equals("no")) {
                saveEmailAsDraft(emailJson);
            } else {
                System.out.println("Invalid response. Please enter 'yes' or 'no'.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createEmailJson(String to, String subject, String body) {
        // Construct the email message in JSON format
        String emailContent = "From: YOUR_EMAIL@gmail.com\r\n" +
                "To: " + to + "\r\n" +
                "Subject: " + subject + "\r\n" +
                "\r\n" + body;
        return Base64.getUrlEncoder().encodeToString(emailContent.getBytes(StandardCharsets.UTF_8));
    }

    public static void sendEmailViaGmailAPI(String emailJson) throws Exception {
        // Create a connection to the Gmail API endpoint
        URL url = new URL(GMAIL_SEND_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Send the email message
        try (DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream())) {
            outputStream.writeBytes("{ \"raw\": \"" + emailJson + "\" }");
        }

        // Check the response code
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Email sent successfully!");
        } else {
            // Print error message
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                System.err.println("Failed to send email. Error response: " + errorResponse.toString());
            }
        }

        // Disconnect the connection
        conn.disconnect();
    }

    private static void saveEmailAsDraft(String emailJson) throws Exception {
        // Create a connection to the Gmail drafts endpoint
        URL url = new URL(GMAIL_DRAFT_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Send the email message as a draft
        try (DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream())) {
            outputStream.writeBytes("{ \"message\": { \"raw\": \"" + emailJson + "\" } }");
        }

        // Check the response code
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Email saved as draft successfully!");
        } else {
            // Print error message
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                System.err.println("Failed to save email as draft. Error response: " + errorResponse.toString());
            }
        }

        // Disconnect the connection
        conn.disconnect();
    }

    public static void main(String[] args) {
        String to = "test@gmail.com";
        String subject = "Test Email";
        String body = "This is a test email sent using the Gmail API without external libraries.";
        sendEmail(to, subject, body);
    }
}
