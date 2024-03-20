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
import java.util.TimeZone;


public class agendaGoogle {

    private static final String GOOGLE_CALENDAR_EVENTS_ENDPOINT = "https://www.googleapis.com/calendar/v3/calendars/primary/events";
    private static final GoogleAuth googleAuth = new API.GoogleAuth();
    private static final String authorizationCode = googleAuth.getAuthenticationCode();
    private static final Map<String, String> accessToken = googleAuth.getAccessToken(authorizationCode);
    private static final String ACCESS_TOKEN = accessToken.get("accessToken");
    private static final String DEFAULT_TIMEZONE = TimeZone.getDefault().getID();

    public static void addEventToCalendar(String calendarId, String summary, String description, String startDateTime, String endDateTime) {
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

        Map<String, Object> upComingEvent = getUpcomingEvent();

        DisplayEvents(upComingEvent);
    }

    public static Map<String, Object> getUpcomingEvent() {
        try {
            // Construct the URL to get upcoming events
            String url = GOOGLE_CALENDAR_EVENTS_ENDPOINT ;

            // Send the request to retrieve upcoming events
            String jsonResponse = sendGetRequest(url);

            // Parse the JSON response and display upcoming events
            Map<String, Object> upComingEvent = parseEvents(jsonResponse);

            return upComingEvent;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            // Print error message along with the error response
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder errorMessage = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorMessage.append(errorLine);
                }
                throw new RuntimeException("Failed to retrieve events. HTTP error code: " + responseCode + ". Error response: " + errorMessage.toString());
            }
        }
    }

    private static Map<String, Object> parseEvents(String jsonResponse) {

        System.out.println(jsonResponse);

        Map<String, Object> jsonData = new HashMap<>();
        Map<String, String> jsonDefaultReminders = new HashMap<>();
        Map<String, Object> jsonItems = new HashMap<>();
        Map<String, String> jsonCreator= new HashMap<>();
        Map<String, String> jsonOrganizer= new HashMap<>();
        Map<String, String> jsonStart= new HashMap<>();
        Map<String, String> jsonEnd= new HashMap<>();
        Map<String, String> jsonReminders= new HashMap<>();

        if(jsonResponse.contains("\"kind\":")){
            String kind = jsonResponse.split("\"kind\":")[1].split("\"")[1];
            jsonItems.put("kind",kind);
        }
        if(jsonResponse.contains("\"etag\":")){
            String etag = jsonResponse.split("\"etag\":")[1].split("\"")[1];
            jsonItems.put("etag",etag);
        }
        if(jsonResponse.contains("\"summary\":")){
            String summary = jsonResponse.split("\"summary\":")[1].split("\"")[1];
            jsonItems.put("summary",summary);
        }
        if(jsonResponse.contains("\"description\":")){
            String description = jsonResponse.split("\"description\":")[1].split("\"")[1];
            jsonItems.put("description",description);
        }
        if(jsonResponse.contains("\"updated\":")){
            String updated = jsonResponse.split("\"updated\":")[1].split("\"")[1];
            jsonItems.put("updated",updated);
        }
        if(jsonResponse.contains("\"timeZone\":")){
            String timeZone = jsonResponse.split("\"timeZone\":")[1].split("\"")[1];
            jsonItems.put("timeZone",timeZone);
        }
        if(jsonResponse.contains("\"accessRole\":")){
            String accessRole = jsonResponse.split("\"accessRole\":")[1].split("\"")[1];
            jsonItems.put("accessRole",accessRole);
        }
        if(jsonResponse.contains("\"defaultReminders\":")){
            String defaultReminders = jsonResponse.split("\"defaultReminders\":")[1].split("\"")[1];
            if(defaultReminders.contains("\"method\":")){
                String method = defaultReminders.split("\"method\":")[1].split("\"")[1];
                jsonDefaultReminders.put("method",method);
            }
            if(defaultReminders.contains("\"minutes\":")){
                String minutes = defaultReminders.split("\"minutes\":")[1].split("\"")[1];
                jsonDefaultReminders.put("minutes",minutes);
            }
            jsonData.put("defaultReminders", jsonDefaultReminders);
        }
        if(jsonResponse.contains("\"nextSyncToken\":")){
            String nextSyncToken = jsonResponse.split("\"nextSyncToken\":")[1].split("\"")[1];
            jsonItems.put("nextSyncToken",nextSyncToken);
        }
        if(jsonResponse.contains("\"items\":")){
            String items = jsonResponse.split("\"items\":")[1].split("\"")[1];
            if(items.contains("\"kind\":")){
                String kindItems = items.split("\"kind\":")[1].split("\"")[1];
                jsonItems.put("kind",kindItems);
            }
            if(items.contains("\"etag\":")){
                String etagItems = items.split("\"etag\":")[1].split("\"")[1];
                jsonItems.put("etag",etagItems);
            }
            if(items.contains("\"id\":")){
                String id = items.split("\"id\":")[1].split("\"")[1];
                jsonItems.put("id",id);
            }
            if(items.contains("\"status\":")){
                String status = items.split("\"status\":")[1].split("\"")[1];
                jsonItems.put("status",status);
            }
            if(items.contains("\"htmlLink\":")){
                String htmlLink = items.split("\"htmlLink\":")[1].split("\"")[1];
                jsonItems.put("htmlLink",htmlLink);
            }
            if(items.contains("\"created\":")){
                String created = items.split("\"created\":")[1].split("\"")[1];
                jsonItems.put("created",created);
            }
            if(items.contains("\"updated\":")){
                String updatedItems = items.split("\"updated\":")[1].split("\"")[1];
                jsonItems.put("updated",updatedItems);
            }
            if(items.contains("\"summary\":")){
                String summaryItems = items.split("\"summary\":")[1].split("\"")[1];
                jsonItems.put("summary",summaryItems);
            }
            if(items.contains("\"description\":")){
                String descriptionItems = items.split("\"description\":")[1].split("\"")[1];
                jsonItems.put("description",descriptionItems);
            }
            if(items.contains("\"creator\":")){
                String creator = items.split("\"creator\":")[1].split("\"")[1];
                if(creator.contains("\"email\":")){
                    String creatorEmail = creator.split("\"email\":")[1].split("\"")[1];
                    jsonCreator.put("email",creatorEmail);
                }
                if(creator.contains("\"self\":")){
                    String creatorSelf = creator.split("\"self\":")[1].split("\"")[1];
                    jsonCreator.put("self",creatorSelf);
                }
                jsonItems.put("creator",jsonCreator);
            }
            if(items.contains("\"organizer\":")){
                String organizer = items.split("\"organizer\":")[1].split("\"")[1];
                if(organizer.contains("\"email\":")){
                    String organizerEmail = organizer.split("\"email\":")[1].split("\"")[1];
                    jsonOrganizer.put("email",organizerEmail);
                }
                if(organizer.contains("\"self\":")){
                    String organizerSelf = organizer.split("\"self\":")[1].split("\"")[1];
                    jsonOrganizer.put("self",organizerSelf);
                }
                jsonItems.put("organizer",jsonOrganizer);
            }
            if(items.contains("\"start\":")){
                String start = items.split("\"start\":")[1].split("\"")[1];
                if(start.contains("\"dateTime\":")){
                    String startDateTime = start.split("\"dateTime\":")[1].split("\"")[1];
                    jsonStart.put("dateTime", startDateTime);
                }
                if(start.contains("\"timeZone\":")){
                    String startTimeZone = start.split("\"timeZone\":")[1].split("\"")[1];
                    jsonStart.put("timeZone", startTimeZone);
                }
                jsonItems.put("start", jsonStart);
            }
            if(items.contains("\"end\":")){
                String end = items.split("\"end\":")[1].split("\"")[1];
                if(end.contains("\"dateTime\":")){
                    String endDateTime = end.split("\"dateTime\":")[1].split("\"")[1];
                    jsonEnd.put("dateTime", endDateTime);
                }
                if(end.contains("\"timeZone\":")){
                    String endTimeZone = end.split("\"timeZone\":")[1].split("\"")[1];
                    jsonEnd.put("timeZone", endTimeZone);
                }
                jsonItems.put("end", jsonEnd);
            }
            if(items.contains("\"iCalUID\":")){
                String iCalUID = items.split("\"iCalUID\":")[1].split("\"")[1];
                jsonItems.put("iCalUID",iCalUID);
            }
            if(items.contains("\"sequence\":")){
                String sequence = items.split("\"sequence\":")[1].split("\"")[1];
                jsonItems.put("sequence",sequence);
            }
            if(items.contains("\"reminders\":")){
                String reminders = items.split("\"reminders\":")[1].split("\"")[1];
                if(reminders.contains("\"useDefault\":")){
                    String useDefault = reminders.split("\"useDefault\":")[1].split("\"")[1];
                    jsonReminders.put("useDefault", useDefault);
                }
                jsonItems.put("reminders",jsonReminders);
            }
            if(items.contains("\"eventType\":")){
                String eventType = items.split("\"eventType\":")[1].split("\"")[1];       
                jsonItems.put("eventType",eventType);

            }
            jsonData.put("item", jsonItems);
            
        }
        return jsonData;
    }

    private static void DisplayEvents(Map<String, Object> eventDico) {
        
        System.out.println("Upcoming Events:");
        System.out.println("----------------");
        System.out.println(eventDico);

    }

    public static void main(String[] args) {
        // Example usage: Adding an event
        String summary = "Meeting with Client";
        String description = "Discuss project progress and future plans.";
        String startDateTime = "2024-03-20T17:00:00";
        String endDateTime = "2024-03-20T19:00:00";
        //addEventToCalendar(calendarId, summary, description, startDateTime, endDateTime);

        showUpcomingEvents();
    }
}