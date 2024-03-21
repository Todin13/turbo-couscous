package com.IaInstitut.chatbot.controllers;

import java.util.List;
import java.util.Map;

public class OpenAIResponse {
    private List<Choice> choices;
    private String id;
    private String object;
    private long created;
    private String model;
    private Map<String, Object> usage;
    private String system_fingerprint;

    // Getters and setters for new fields
    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }
    public long getCreated() { return created; }
    public void setCreated(long created) { this.created = created; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Map<String, Object> getUsage() { return usage; }
    public void setUsage(Map<String, Object> usage) { this.usage = usage; }
    public String getSystem_fingerprint() { return system_fingerprint; }
    public void setSystem_fingerprint(String system_fingerprint) { this.system_fingerprint = system_fingerprint; }

    public static class Choice {
        private int index;
        private Message message;
        private Object logprobs;
        private String finish_reason;

        // Getters and setters
        public int getIndex() { return index; }
        public void setIndex(int index) { this.index = index; }
        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }
        public Object getLogprobs() { return logprobs; }
        public void setLogprobs(Object logprobs) { this.logprobs = logprobs; }
        public String getFinish_reason() { return finish_reason; }
        public void setFinish_reason(String finish_reason) { this.finish_reason = finish_reason; }

        public static class Message {
            private String role;
            private String content;
            // Getters and setters
            public String getRole() { return role; }
            public void setRole(String role) { this.role = role; }
            public String getContent() { return content; }
            public void setContent(String content) { this.content = content; }
        }
    }
}
