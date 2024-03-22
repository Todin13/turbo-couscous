package com.IaInstitut.chatbot.API;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.List;
import java.util.ArrayList;

public class agendaGoogle {

    private static final String GOOGLE_CALENDAR_EVENTS_ENDPOINT = "https://www.googleapis.com/calendar/v3/calendars/primary/events";
    private static final String ACCESS_TOKEN = apiController.readToken();
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

    public static boolean removeEventFromCalendarBySummary(Map<String, Object> eventDico, String summary) {
        // Get the event ID using the summary
        String eventId = getIdFromSummary(eventDico, summary);
        
        if (eventId != null) {
            try {
                // If event ID is found, remove the event
                removeEventFromCalendar(eventId);
                System.out.println("Event with summary '" + summary + "' has been removed from the calendar.");
                return true;
            } catch (NullPointerException e) {
                // Handle the case where the event ID is not found
                System.out.println("Event with summary '" + summary + "' not found in the calendar.");
                return false;
            } catch (Exception e) {
                // Handle any other exceptions
                e.printStackTrace();
                return false;
            }
        }
        return false;
       
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

    private static String getIdFromSummary(Map<String, Object> eventDico, String summary) {
        // Assuming 'item' is a List<Map<String, Object>>
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> events = (List<Map<String, Object>>) eventDico.get("item");
    
        for (Map<String, Object> event : events) {
            // Check if the summary matches
            if (event.get("summary").equals(summary)) {
                // If a match is found, return the id
                return (String) event.get("id");
            }
        }
    
        // If no match is found, return null or throw an exception based on your requirement
        return null;
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

    public static String showUpcomingEvents() {

        Map<String, Object> upComingEvent = getUpcomingEvent();

        return DisplayEvents(upComingEvent);
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

        Map<String, Object> jsonData = new HashMap<>();
        Map<String, String> jsonDefaultReminders = new HashMap<>();
        List<Map<String, Object>> jsonAllItem = new ArrayList<>();
        Map<String, String> jsonCreator= new HashMap<>();
        Map<String, String> jsonOrganizer= new HashMap<>();
        Map<String, String> jsonStart= new HashMap<>();
        Map<String, String> jsonEnd= new HashMap<>();
        Map<String, String> jsonReminders= new HashMap<>();

        String kind = jsonResponse.split("\"kind\":")[1].split("\"")[1];
        jsonData.put("kind",kind);
        
        String etag = jsonResponse.split("\"etag\":")[1].split("\"")[1];
        jsonData.put("etag",etag);
        
        String summary = jsonResponse.split("\"summary\":")[1].split("\"")[1];
        jsonData.put("summary",summary);
        
        String description = jsonResponse.split("\"description\":")[1].split("\"")[1];
        jsonData.put("description",description);
        
        String updated = jsonResponse.split("\"updated\":")[1].split("\"")[1];
        jsonData.put("updated",updated);
        
        String timeZone = jsonResponse.split("\"timeZone\":")[1].split("\"")[1];
        jsonData.put("timeZone",timeZone);
        
        String accessRole = jsonResponse.split("\"accessRole\":")[1].split("\"")[1];
        jsonData.put("accessRole",accessRole);
        
        
        String defaultReminders = jsonResponse.split("\"defaultReminders\":")[1];
        if(defaultReminders.contains("\"method\":")){
            String method = defaultReminders.split("\"method\":")[1].split("\"")[1];
            jsonDefaultReminders.put("method",method);
        }
        if(defaultReminders.contains("\"minutes\":")){
            String minutes = defaultReminders.split("\"minutes\":")[1].split("\"")[1];
            jsonDefaultReminders.put("minutes",minutes);
        }
        jsonData.put("defaultReminders", jsonDefaultReminders);
        
        String nextSyncToken = jsonResponse.split("\"nextSyncToken\":")[1].split("\"")[1];
        jsonData.put("nextSyncToken",nextSyncToken);
        

        String allItems = jsonResponse.split("\"items\":")[1];
        String[] item = allItems.split("\\},  \\{");
            for(String items : item) {

                Map<String, Object> jsonItems = new HashMap<>();
                
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
                    String creator = items.split("\"creator\":")[1];
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
                    String organizer = items.split("\"organizer\":")[1];
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
                    String start = items.split("\"start\":")[1];
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
                    String end = items.split("\"end\":")[1];
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
                    String reminders = items.split("\"reminders\":")[1];
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
                jsonAllItem.add(jsonItems);
            }
        
        jsonData.put("item", jsonAllItem);
    
        return jsonData;
    }

    private static String DisplayEvents(Map<String, Object> eventDico) {
        StringBuilder eventsSummary = new StringBuilder();
        eventsSummary.append("Upcoming Events:\n");
        eventsSummary.append("----------------\n");
        
        // Assuming 'item' is a List<Map<String, Object>>
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> events = (List<Map<String, Object>>) eventDico.get("item");
        
        for(Map<String, Object> event : events) {
            eventsSummary.append(event.get("summary")).append("\n");
            
            // Assuming 'start' and 'end' are Maps with a 'dateTime' key
            @SuppressWarnings("unchecked")
            Map<String, Object> start = (Map<String, Object>) event.get("start");
            eventsSummary.append("Start: ").append(start.get("dateTime")).append("\n");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> end = (Map<String, Object>) event.get("end");
            eventsSummary.append("End: ").append(end.get("dateTime")).append("\n");
            
            eventsSummary.append("----------------\n");
        }
        
        return eventsSummary.toString();
    }
    

    public static void main(String[] args) {
        // Example usage: Adding an event
        String summary = "Meeting with dad";
        String description = "Discuss project progress and future plans.";
        String startDateTime = "2024-03-22T19:00:00";
        String endDateTime = "2024-03-22T21:00:00";
        addEventToCalendar(summary, description, startDateTime, endDateTime);
        showUpcomingEvents(); 
        removeEventFromCalendarBySummary(getUpcomingEvent(), "Meeting with dad");
        System.out.println();
        showUpcomingEvents(); 
    }
}