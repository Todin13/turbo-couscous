package com.chatbot;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController {

    @FXML
    private TextArea chatArea;

    @FXML
    private TextField inputField;

    private OpenAIChatbot chatbot = new OpenAIChatbot();

    @FXML
    private void initialize() {
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
    }

    @FXML
    private void onSend() {
        String userInput = inputField.getText();
        if (!userInput.isEmpty()) {
            chatArea.appendText("You: " + userInput + "\n");
            String botResponse = chatbot.sendMessage(userInput);
            chatArea.appendText("Bot: " + botResponse + "\n");
            inputField.clear();
        }
    }
}