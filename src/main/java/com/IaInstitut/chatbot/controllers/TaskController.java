package com.IaInstitut.chatbot.controllers;

import com.IaInstitut.chatbot.model.ModelController;
import com.IaInstitut.chatbot.model.NlpResult;
import com.IaInstitut.chatbot.service.EmailSender;
import com.IaInstitut.chatbot.service.IPandLocationResolver;
import com.IaInstitut.chatbot.service.weatherApi;

import java.util.Map;
// Import the EmailSender class here

public class TaskController {
    private final ModelController modelController;
    private String emailTo = null;
    private String emailSubject = null;
    private String emailBody = null;
    // Adding state to keep track of what information we're collecting for the email.
    private String collectingInfoForEmail = null; // This will be null, "to", "subject", or "body"

    public TaskController() {
        this.modelController = new ModelController();
    }

    public String handleInput(String userInput) {
        NlpResult nlpResult = modelController.processUserInput(userInput);
        String response;

        // If we are in the process of collecting information for sending an email
        if (collectingInfoForEmail != null) {
            switch (collectingInfoForEmail) {
                case "to":
                    emailTo = userInput;
                    collectingInfoForEmail = "subject"; // Move to collecting the subject next
                    return "What's the subject of the email?";
                case "subject":
                    emailSubject = userInput;
                    collectingInfoForEmail = "body"; // Move to collecting the body next
                    return "What should the email body say?";
                case "body":
                    emailBody = userInput;
                    try {
                        // Here, call the EmailSender class to send the email
                        EmailSender.sendEmailWithSendGrid(emailTo, emailSubject, emailBody);
                        response = "Your email has been sent to " + emailTo + ".";
                    } catch (Exception e) {
                        response = "There was an error sending the email: " + e.getMessage();
                    }
                    // Reset the email process variables after attempting to send the email
                    emailTo = null;
                    emailSubject = null;
                    emailBody = null;
                    collectingInfoForEmail = null; // Reset the process
                    return response;
            }
        }

        // Direct response handling based on intent
        if ("responseFromDataset".equals(nlpResult.getIntent())) {
            response = nlpResult.getEntities().getOrDefault("response", new String[]{"I'm not sure how to help with that."})[0];
        } else {
            switch (nlpResult.getIntent()) {
                case "weatherForecast":
                    // Assuming IPandLocationResolver and weatherApi are properly set up
                    String[] ipAndLocation = IPandLocationResolver.getIPAndLocation();
                    String latitude = ipAndLocation[2];
                    String longitude = ipAndLocation[3];

                    // Fetch weather forecast using the location
                    Map<String, Map<String, String>> weatherForecast = weatherApi.getWeatherForecast(latitude, longitude);

                    // Check if the weather forecast is not null and print it out for debugging
                    if (weatherForecast != null) {
                        System.out.println("Weather Forecast Map: " + weatherForecast);
                        Map<String, String> currentWeather = weatherForecast.get("current");

                        // Check if the current weather map is not null
                        if (currentWeather != null) {
                            String temperature = currentWeather.get("temperature");
                            String condition = currentWeather.get("condition");
                            String humidity = currentWeather.get("humidity");
                            String windSpeed = currentWeather.get("windSpeed");

                            // Format the forecast into a response if all values are available
                            if (temperature != null && condition != null && humidity != null && windSpeed != null) {
                                response = String.format("Current weather: Temperature is %s, condition is %s, humidity is %s%%, wind speed is %s km/h.", temperature, condition, humidity, windSpeed);
                            } else {
                                response = "Unable to retrieve some weather details.";
                            }
                        } else {
                            response = "Current weather information is unavailable.";
                        }
                    } else {
                        response = "Weather forecast data is unavailable.";
                    }
                    break;
                case "scheduleAppointment":
                    response = "I can help with that. What day would you like to schedule the appointment for?";
                    break;
                case "sendEmail":
                    collectingInfoForEmail = "to"; // Start collecting information for sending an email
                    return "Who would you like to send the email to?";
                default:
                    response = "I'm not sure I understand. Could you please provide more details or ask another question?";
                    break;
            }
        }

        return response;
    }
}
