package com.IaInstitut.chatbot.controllers;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChatbotGUI extends Application {

    private TaskController taskController = new TaskController();
    private VBox chatPane;
    private ScrollPane scrollPane;

    @Override
    public void start(Stage primaryStage) {
        chatPane = new VBox(10);
        chatPane.setPadding(new Insets(10));
        chatPane.setFillWidth(true);

        scrollPane = new ScrollPane(chatPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-padding: 0;");

        TextField userInput = new TextField();
        userInput.setPromptText("Type your message here...");
        userInput.setMaxHeight(40);
        userInput.setStyle("-fx-font-size: 16px;");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> sendMessage(userInput));
        userInput.setOnAction(event -> sendMessage(userInput));

        HBox inputArea = new HBox(userInput, sendButton);
        inputArea.setAlignment(Pos.BOTTOM_CENTER);
        inputArea.setPadding(new Insets(10));
        inputArea.setStyle("-fx-background-color: #EEE;"); // Light gray background
        HBox.setHgrow(userInput, Priority.ALWAYS); // Text field grows to fill space

        VBox mainLayout = new VBox(scrollPane, inputArea);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        Scene scene = new Scene(mainLayout, 300, 600);
        primaryStage.setTitle("Chatbot");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendMessage(TextField userInput) {
        String message = userInput.getText().trim();
        if (!message.isEmpty()) {
            addMessage(message, true);
            userInput.clear();

            // Get the chatbot's response from TaskController
            String response = taskController.handleInput(message);
            addMessage(response, false);
        }
    }

    private void addMessage(String message, boolean isUser) {
        Label senderLabel = new Label(isUser ? "You" : "Bot");
        senderLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (isUser ? "#8E24AA" : "#7EC8E3") + ";");

        Label messageLabel = new Label(message);
        messageLabel.setTextFill(Color.WHITE);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(200);
        messageLabel.setStyle("-fx-background-color: " + (isUser ? "#8E24AA" : "#7EC8E3") +
                "; -fx-background-radius: 15; -fx-padding: 10;");

        VBox messageContainer = new VBox(5); // Spacing between sender label and message label
        messageContainer.getChildren().add(senderLabel);
        messageContainer.getChildren().add(messageLabel);
        messageContainer.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        HBox messageBox = new HBox(messageContainer);
        messageBox.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messageBox.setPadding(new Insets(5));

        chatPane.getChildren().add(messageBox);

        // Scroll to the bottom
        scrollPane.applyCss();
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }
    public static void main(String[] args) {
        launch(args);
    }

    // TaskController and other classes...
}
