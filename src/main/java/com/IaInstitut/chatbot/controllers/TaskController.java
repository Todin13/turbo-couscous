package com.IaInstitut.chatbot.controllers;

import com.IaInstitut.chatbot.model.ModelController;
import com.IaInstitut.chatbot.model.NlpResult;

public class TaskController {
    private final ModelController modelController;
    private String lastIntent = null;
    public TaskController() {
        this.modelController = new ModelController();
    }

    public String handleInput(String userInput) {
        NlpResult nlpResult = modelController.processUserInput(userInput);
        String response;

        // Check if the intent is directly mapped to a response
        if ("responseFromDataset".equals(nlpResult.getIntent())) {
            // When the intent is a generic placeholder for dataset responses,
            // retrieve the actual response directly from the NlpResult's details.
            response = nlpResult.getEntities().getOrDefault("response", new String[]{"I'm not sure how to help with that."})[0];
        } else {
            // Handle predefined intents with custom responses
            switch (nlpResult.getIntent()) {
                case "scheduleAppointment":
                    response = "I can help with that. What day would you like to schedule the appointment for?";
                    break;
                case "sendEmail":
                    response = "Sure, I can assist with sending an email. What would you like to include in the message?";
                    break;
                // Include other predefined intents as needed
                default:
                    response = "I'm not sure I understand. Could you please provide more details or ask another question?";
                    break;
            }
        }
        lastIntent = nlpResult.getIntent();
        return response;
    }
}
