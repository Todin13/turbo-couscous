package com.IaInstitut.chatbot.controllers;

import org.apache.http.HttpResponse;
import java.util.List;

public class OpenAIChatbot {

    private HttpClientHelper httpClientHelper = new HttpClientHelper();

    public String sendMessage(String message) {
        try {
            HttpResponse response = httpClientHelper.sendPostRequest(message);
            OpenAIResponse openAIResponse = httpClientHelper.parseResponse(response);
            List<OpenAIResponse.Choice> choices = openAIResponse.getChoices();
            
            // Check if choices is not null and not empty before accessing
            if (choices != null && !choices.isEmpty()) {
                OpenAIResponse.Choice choice = choices.get(0);
                if (choice.getMessage() != null) {
                    return choice.getMessage().getContent().trim(); // Access the content of the message
                } else {
                    return "The choice returned by the API does not contain a message.";
                }
            } else {
                // Handle the case where choices is null or empty
                return "No choices were returned by the API.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I couldn't process your request.";
        }
    }
    
}
