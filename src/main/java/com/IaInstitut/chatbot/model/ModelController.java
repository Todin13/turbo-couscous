package com.IaInstitut.chatbot.model;

public class ModelController {
    private final NlpEngine nlpEngine;

    public ModelController() {
        this.nlpEngine = new NlpEngine();
    }

    public NlpResult processUserInput(String userInput) {
        // Use NlpEngine to process the input and get results
        return nlpEngine.processInput(userInput);
    }
}
