package chatbot.controller;

import chatbot.model.ChatResponse;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;

public class ModelController {

    private final OpenAiService openAiService;

    public ModelController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    public ChatResponse getChatResponse(String userInput) {
        CompletionRequest request = new CompletionRequest.Builder()
                .prompt(userInput)
                .maxTokens(150)
                .build();

        CompletionResult result = openAiService.createCompletion(request);
        String message = result.getChoices().get(0).getText().trim();
        return new ChatResponse(message);
    }
}