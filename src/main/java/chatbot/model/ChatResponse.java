package chatbot.model;

public class ChatResponse {
    private final String message;

    public ChatResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}