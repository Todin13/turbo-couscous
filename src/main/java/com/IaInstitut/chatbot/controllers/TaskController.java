package com.IaInstitut.chatbot.controllers;

import com.IaInstitut.chatbot.model.ModelController;
import com.IaInstitut.chatbot.model.NlpResult;

public class TaskController {
    private final ModelController modelController;

    public TaskController() {
        this.modelController = new ModelController();
    }

    public String handleInput(String userInput) {
        NlpResult nlpResult = modelController.processUserInput(userInput);
        String response;

        switch (nlpResult.getIntent()) {
            case "greeting":
                response = "Hello! I'm fine, thank you. How can I help you today?";
                break;
            case "scheduleAppointment":
                response = "I can help with that. What day would you like to schedule the appointment for?";
                break;
            case "physicsInfo":
                response = "Quantum physics explores the behavior of matter and energy at the smallest scales. It's quite fascinating!";
                break;
            default:
                response = "I'm not sure I understand. Could you please provide more details or ask another question?";
                break;
        }
        return response;
    }
}
