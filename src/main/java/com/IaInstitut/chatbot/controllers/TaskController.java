package com.IaInstitut.chatbot.controllers;

import com.IaInstitut.chatbot.model.ModelController;
import com.IaInstitut.chatbot.model.NlpResult;


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
        String response = " Response is Empty";

        // If we are in the process of collecting information for sending an email
        if (collectingInfoForEmail != null) {
            return response = "Not Implemented Yet";
        }

        // Direct response handling based on intent
        if ("responseFromDataset".equals(nlpResult.getIntent())) {
            response = nlpResult.getEntities().getOrDefault("response", new String[]{"I'm not sure how to help with that."})[0];
        } else {
            switch (nlpResult.getIntent()) {
            }
        }

        return response;
    }

}
