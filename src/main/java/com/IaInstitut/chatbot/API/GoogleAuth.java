package com.IaInstitut.chatbot.API;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GoogleAuth {

    private static final String CLIENT_ID = "446973941336-5se3jchk07hrna6b436rum3dscd4pg8r.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8080/Callback";
    private static final String SCOPE = "openid email profile https://mail.google.com/ https://www.googleapis.com/auth/calendar"; // Add required scopes

    public Map<String, String> getAccessToken(String authorizationCode) {
        try {
            String tokenUrl = "https://oauth2.googleapis.com/token";
            String params = "code=" + URLEncoder.encode(authorizationCode, StandardCharsets.UTF_8)
                    + "&client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8)
                    + "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8)
                    + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8)
                    + "&grant_type=authorization_code";
    
            URL url = new URL(tokenUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
    
            conn.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));
    
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
    
                // Parse JSON response to get access token
                String jsonResponse = response.toString();
                
                if (jsonResponse.contains("\"access_token\":")) {
                    String accessToken = jsonResponse.split("\"access_token\":")[1].split("\"")[1];
                    String expireIn = jsonResponse.split("\"expires_in\":")[1].split("\"")[1];
                    String refreshToken = jsonResponse.split("\"refresh_token\":")[1].split("\"")[1];
                    String scope = jsonResponse.split("\"scope\":")[1].split("\"")[1];
                    String tokenType = jsonResponse.split("\"token_type\":")[1].split("\"")[1];
                    String idToken = jsonResponse.split("\"id_token\":")[1].split("\"")[1];
                    
                    Map<String, String> allTokenInfo = new HashMap<>();
                    allTokenInfo.put("accessToken", accessToken);
                    allTokenInfo.put("expireIn", expireIn);
                    allTokenInfo.put("refreshToken", refreshToken);
                    allTokenInfo.put("scope", scope);
                    allTokenInfo.put("tokenType", tokenType);
                    allTokenInfo.put("idToken", idToken);
                    
                    return allTokenInfo;
                } else {
                    System.err.println("Access token not found in JSON response.");
                    System.out.println(jsonResponse);
                    return null;
                }
            } else {
                // Handle HTTP error response
                System.err.println("Failed to get access token. HTTP error code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            // Handle IO exceptions
            e.printStackTrace();
            return null;
        }
    }  

    public static String getAuthenticationCode() {
        try {
            // Start a local HTTP server to handle the callback
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Waiting for authorization code...");

            // Construct the authorization URL
            String authorizationUrl = "https://accounts.google.com/o/oauth2/auth?" +
                    "client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                    "&response_type=code" +
                    "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8) +
                    "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8);

            // Open the authorization URL in the default web browser
            Desktop.getDesktop().browse(new URI(authorizationUrl));

            // Wait for the authorization code to be received
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream();

            // Read the request from the client
            StringBuilder request = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                request.append(line).append("\r\n");
            }

            // Send the response back to the client
            String response = "HTTP/1.1 200 OK\r\n\r\nAuthorization code received. You can now close this page.";
            out.write(response.getBytes(StandardCharsets.UTF_8));

            // Extract the authorization code from the request
            String encodedUrl = request.toString();
            String decodedUrl = java.net.URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8.name());

            // Find the position of the 'code=' parameter
            int codeIndex = decodedUrl.indexOf("code=");

            if (codeIndex != -1) {
                // Extract the authorization code
                int startIndex = codeIndex + 5; // Length of "code="
                int endIndex = decodedUrl.indexOf("&", startIndex);
                String authorizationCode = endIndex != -1 ? decodedUrl.substring(startIndex, endIndex) : decodedUrl.substring(startIndex);

                // Close connections
                in.close();
                out.close();
                clientSocket.close();
                serverSocket.close();

                return authorizationCode.trim(); // Return the received authorization code
            } else {
                System.err.println("Authorization code not found in the URL.");
            }

            // Close connections
            in.close();
            out.close();
            clientSocket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if there's an error or if the code cannot be retrieved
    }
    
    public static void main(String[] args) {
        try {
            // Get the authorization code
            String authorizationCode = getAuthenticationCode();
            if (authorizationCode != null && !authorizationCode.isEmpty()) {
                System.out.println("Authorization code received: " + authorizationCode);
    
                // Get the access token using the authorization code
                GoogleAuth googleAuth = new GoogleAuth();
                Map<String, String> accessToken = googleAuth.getAccessToken(authorizationCode);
    
                if (accessToken != null && !accessToken.isEmpty()) {
                    // Access token obtained successfully
                    System.out.println("Access token obtained successfully: " + accessToken.get("accessToken"));
                    // You can perform further actions here
                } else {
                    // Access token retrieval failed
                    System.out.println("Access token retrieval failed.");
                }
            } else {
                // Authorization code retrieval failed
                System.out.println("Authorization code retrieval failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 
}
