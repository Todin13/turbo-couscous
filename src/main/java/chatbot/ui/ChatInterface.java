package chatbot.ui;

import chatbot.controller.ModelController;
import chatbot.model.ChatResponse;

import java.util.Scanner;

public class ChatInterface {

    private final ModelController modelController;
    private final Scanner scanner;

    public ChatInterface(ModelController modelController) {
        this.modelController = modelController;
        this.scanner = new Scanner(System.in);
    }

    public void startChatSession() {
        System.out.println("Welcome to the Chatbot Interface!");
        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();
            if ("exit".equalsIgnoreCase(userInput)) {
                System.out.println("Exiting chat session.");
                break;
            }
            ChatResponse response = modelController.getChatResponse(userInput);
            System.out.println("Bot: " + response.getMessage());
        }
    }
}