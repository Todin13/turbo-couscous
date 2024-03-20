package API;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;


public class agendaGoogle {

    private static final String GOOGLE_CALENDAR_EVENTS_ENDPOINT = "https://www.googleapis.com/calendar/v3/calendars/primary/events";
    private static final GoogleAuth googleAuth = new API.GoogleAuth();
    private static final String authorizationCode = googleAuth.getAuthenticationCode();
    private static final Map<String, String> accessToken = googleAuth.getAccessToken(authorizationCode);
    private static final String ACCESS_TOKEN = accessToken.get("accessToken");
    private static final String DEFAULT_TIMEZONE = TimeZone.getDefault().getID();

    public static void addEventToCalendar(String summary, String description, String startDateTime, String endDateTime) {
        try {
            // Create the event JSON
            String eventJson = createEventJson(summary, description, startDateTime, endDateTime);

            // Send the request to add the event to Google Calendar
            sendRequestToCalendarAPI(GOOGLE_CALENDAR_EVENTS_ENDPOINT, "POST", eventJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeEventFromCalendar(String eventId) {
        try {
            // Construct the URL to delete the event
            String deleteUrl = GOOGLE_CALENDAR_EVENTS_ENDPOINT + "/" + eventId;

            // Send the request to remove the event from Google Calendar
            sendRequestToCalendarAPI(deleteUrl, "DELETE", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createEventJson(String summary, String description, String startDateTime, String endDateTime) {
        return "{\n" +
                "  \"summary\": \"" + summary + "\",\n" +
                "  \"description\": \"" + description + "\",\n" +
                "  \"start\": {\n" +
                "    \"dateTime\": \"" + startDateTime + "\",\n" +
                "    \"timeZone\": \"" + DEFAULT_TIMEZONE + "\"\n" +
                "  },\n" +
                "  \"end\": {\n" +
                "    \"dateTime\": \"" + endDateTime + "\",\n" +
                "    \"timeZone\": \"" + DEFAULT_TIMEZONE + "\"\n" +
                "  }\n" +
                "}";
    }
    

    private static void sendRequestToCalendarAPI(String urlString, String requestMethod, String requestBody) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(requestMethod);
        conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        if (requestBody != null) {
            try (DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream())) {
                outputStream.writeBytes(requestBody);
            }
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            System.out.println("Operation completed successfully!");
        } else {
            // Print error message
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                System.err.println("Operation failed. Error response: " + errorResponse.toString());
            }
        }

        conn.disconnect();
    }

    public static void showUpcomingEvents() {
        try {
            // Construct the URL to get upcoming events
            String url = GOOGLE_CALENDAR_EVENTS_ENDPOINT + "?timeMin=now&orderBy=startTime&singleEvents=true";

            // Send the request to retrieve upcoming events
            String jsonResponse = sendGetRequest(url);

            // Parse the JSON response and display upcoming events
            parseAndDisplayEvents(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new RuntimeException("Failed to retrieve events. HTTP error code: " + responseCode);
        }
    }

    private static void parseAndDisplayEvents(String jsonResponse) {
        // Parse the JSON response and display upcoming events
        // You can use a JSON parsing library like Gson or Jackson for parsing, but for simplicity, let's use basic string manipulation here.
        // Note: This code assumes the JSON structure returned by the Google Calendar API. You may need to adjust it if the structure changes.
        System.out.println("Upcoming Events:");
        System.out.println("----------------");
        // Parse the JSON response here and display events
    }

    public static void main(String[] args) {
        // Example usage: Adding an event
        String summary = "Meeting with Client";
        String description = "Discuss project progress and future plans.";
        String startDateTime = "2024-03-20T10:00:00";
        String endDateTime = "2024-03-20T11:00:00";
        addEventToCalendar(summary, description, startDateTime, endDateTime);

        showUpcomingEvents();
    }
}