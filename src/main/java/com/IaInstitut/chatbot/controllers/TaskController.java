package com.IaInstitut.chatbot.controllers;

import java.util.Map;

import com.IaInstitut.chatbot.API.IPandLocationResolver;
import com.IaInstitut.chatbot.model.ModelController;
import com.IaInstitut.chatbot.model.NlpResult;
import com.IaInstitut.chatbot.API.weatherApi;
import com.IaInstitut.chatbot.API.GmailSend;
import com.IaInstitut.chatbot.API.agendaGoogle;

public class TaskController {
    private final ModelController modelController;
    private GmailSend emailSender = new GmailSend(); // Create an instance of the EmailSender class
    private String collectingInfoForEmail = null; // This will be null, "to", "subject", or "body"
    private String to = "", subject = "", body = "";
    private String collectingInfoForAppointment = null; // This will be null, "summary", "description", "startDateTime", or "endDateTime"
    private String summary = "", description = "", startDateTime = "", endDateTime = "";


    public TaskController() {
        this.modelController = new ModelController();
    }

    public String handleInput(String userInput) {
        NlpResult nlpResult = modelController.processUserInput(userInput);
        String response;

        // Check if we are in the process of collecting information for sending an email
        if (collectingInfoForEmail != null) {
            switch (collectingInfoForEmail) {
                case "to":
                    to = userInput; // Collect the recipient's email address
                    collectingInfoForEmail = "subject"; // Transition to collecting the email subject
                    return "Please enter the email subject:";
                case "subject":
                    subject = userInput; // Collect the email subject
                    collectingInfoForEmail = "body"; // Transition to collecting the email body
                    return "Please enter the email body:";
                case "body":
                    body = userInput; // Collect the email body
                    collectingInfoForEmail = null; // We've collected all information, reset the state

                    try {
                        String emailJson = emailSender.createEmailJson(to, subject, body);
                        emailSender.sendEmailViaGmailAPI(emailJson);
                        return "Email sent successfully.";
                    } catch (Exception e) {
                        return "Failed to send the email: " + e.getMessage();
                    }
                default:
                    collectingInfoForEmail = null; // Reset state in case of an unexpected state
                    return "Unexpected error. Please try to send the email again.";
            }
        } else if (collectingInfoForAppointment != null) {
            switch (collectingInfoForAppointment) {
                case "summary":
                    summary = userInput;
                    collectingInfoForAppointment = "description";
                    return "Please enter the appointment description:";
                case "description":
                    description = userInput;
                    collectingInfoForAppointment = "startDateTime";
                    return "Please enter the appointment start date and time (e.g., 2024-03-22T10:00:00):";
                case "startDateTime":
                    startDateTime = userInput;
                    collectingInfoForAppointment = "endDateTime";
                    return "Please enter the appointment end date and time (e.g., 2024-03-22T11:00:00):";
                case "endDateTime":
                    endDateTime = userInput;
                    collectingInfoForAppointment = null; // We've collected all information, reset the state
                    
                    try {
                        com.IaInstitut.chatbot.API.agendaGoogle.addEventToCalendar(summary, description, startDateTime, endDateTime);
                        response = "Appointment scheduled successfully.";
                    } catch (Exception e) {
                        response = "Failed to schedule the appointment: " + e.getMessage();
                    }
                    // Reset appointment details for next use
                    summary = "";
                    description = "";
                    startDateTime = "";
                    endDateTime = "";
                    return response;
                default:
                    collectingInfoForAppointment = null; // Reset state in case of an unexpected error
                    return "Unexpected error. Please try scheduling the appointment again.";
            }
        } else if ("scheduleAppointment".equals(nlpResult.getIntent())) {
            collectingInfoForAppointment = "summary"; // Begin collecting information for scheduling an appointment
            return "Please enter the appointment summary:";
        }
        
        
        if ("deleteEvent".equals(nlpResult.getIntent())) {
            String eventSummary = extractSummaryFromUserInput(userInput);
            if (eventSummary != null && !eventSummary.isEmpty()) {
                // Call the method to delete the event from the calendar by summary
                boolean deleted = agendaGoogle.removeEventFromCalendarBySummary(agendaGoogle.getUpcomingEvent(), eventSummary);
                if (deleted) {
                    response = "Event deleted successfully.";
                } else {
                    response = "Event with the specified summary not found.";
                }
            } else {
                response = "Please specify the event summary to delete.";
            }
        } else if ("showAppointmentSummary".equals(nlpResult.getIntent())) {
            // Assuming agendaGoogle.showUpcomingEvents() now returns a String
            response = agendaGoogle.showUpcomingEvents();
        }
        

        // Handling other intents
        if ("sendEmail".equals(nlpResult.getIntent())) {
            collectingInfoForEmail = "to"; // Start collecting information for sending an email
            return "Please enter the recipient's email address:";
        } else if ("responseFromDataset".equals(nlpResult.getIntent())) {
            response = nlpResult.getEntities().getOrDefault("response", new String[]{"I'm not sure how to help with that."})[0];
        } else {
            switch (nlpResult.getIntent()) {
                

                case "scheduleAppointment":
                        response = "Not Implemented Yet"; // Placeholder response for "scheduleAppointment
                    break;
                
                    case "showAppointmentSummary":
                    // Execute the method to get and show the upcoming events
                    // Make sure this method prints the event summary
                    response =  agendaGoogle.showUpcomingEvents();;
                    break;

                    case "weatherForecast":
                    // Assuming IPandLocationResolver and weatherApi are properly set up
                    String[] ipAndLocation = IPandLocationResolver.getIPAndLocation();
                    String latitude = ipAndLocation[2];
                    String longitude = ipAndLocation[3];

                    // Assume that the weather forecast data is a Map with keys as timestamps
                    Map<String, Map<String, String>> weatherForecast = weatherApi.getWeatherForecast(latitude, longitude);

                    // Print the forecast for debugging purposes
                    System.out.println("Weather Forecast Map: " + weatherForecast);

                    // Attempt to find the current weather details
                    String currentWeatherKey = findCurrentWeatherKey(weatherForecast);
                    if (currentWeatherKey != null) {
                        Map<String, String> currentWeather = weatherForecast.get(currentWeatherKey);

                        String temperature = currentWeather.get("temperature_2m");
                        String humidity = currentWeather.get("relative_humidity_2m");
                        String cloudCover = currentWeather.get("cloud_cover_low");
                        String pressure = currentWeather.get("surface_pressure");

                        if (temperature != null && humidity != null && cloudCover != null && pressure != null) {
                            response = String.format("Current weather: Temperature is %sÂ°C, humidity is %s%%, cloud cover is %s%%, surface pressure is %s hPa.",
                                    temperature, humidity, cloudCover, pressure);
                        } else {
                            response = "Some weather details are not available.";
                        }
                    } else {
                        response = "Current weather information is not available.";
                    }


                    break;
                default:
                    response = "Not Implemented Yet"; // Default response if no other conditions are met
                    break;
            }
        }
        return response;
    }

    private String findCurrentWeatherKey(Map<String, Map<String, String>> weatherForecast) {
        // This method should implement the logic to find the closest timestamp key
        // to the current time. For now, we'll just take the first key as an example.
        if (weatherForecast != null && !weatherForecast.isEmpty()) {
            // You can also add more logic here to find the exact key for the current time
            return weatherForecast.keySet().iterator().next();
        }
        return null;
    }
    private String extractSummaryFromUserInput(String userInput) {
        // Simplified extraction logic
        String keyword = "Delete event:";
        int startIndex = userInput.indexOf(keyword);
        if (startIndex != -1) {
            return userInput.substring(startIndex + keyword.length()).trim();
        }
        return null;
    }
}
