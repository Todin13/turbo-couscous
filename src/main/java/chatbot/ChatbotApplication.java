package chatbot;

import chatbot.ui.ChatInterface;
import chatbot.controller.ModelController;
import com.theokanning.openai.service.OpenAiService;

public class ChatbotApplication {

    private final ChatInterface chatInterface;
    private final ModelController modelController;

    public ChatbotApplication() {
        OpenAiService openAiService = new OpenAiService("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");
        this.modelController = new ModelController(openAiService);
        this.chatInterface = new ChatInterface(modelController);
    }

    public void run() {
        chatInterface.startChatSession();
    }
}