package com.IaInstitut.chatbot.model;

import java.util.Map;

public class NlpResult {
    private final String intent;
    private final Map<String, String[]> entities;

    public NlpResult(String intent, Map<String, String[]> entities) {
        this.intent = intent;
        this.entities = entities;
    }

    public String getIntent() {
        return intent;
    }

    public Map<String, String[]> getEntities() {
        return entities;
    }

    // Additional methods can be added as needed
}
