package com.IaInstitut.chatbot.controllers;

import java.util.Scanner;

public class TaskControllerTest {

    public static void main(String[] args) {
        // Instantiate the TaskController
        TaskController taskController = new TaskController();

        // Create a Scanner object for reading user input from console
        Scanner scanner = new Scanner(System.in);

        // Introduction message
        System.out.println("Chatbot is ready. Type your message (type 'quit' to exit):");

        // Keep reading user input and processing it until 'quit' is entered
        while (true) {
            System.out.print("User: ");
            String userInput = scanner.nextLine();

            // Check if the user wants to quit the conversation
            if ("quit".equalsIgnoreCase(userInput)) {
                System.out.println("Chatbot: Goodbye!");
                break;
            }

            // Process the input and print the response
            String response = taskController.handleInput(userInput);
            System.out.println("Chatbot: " + response);
            System.out.println(); // Add a new line for readability
        }

        scanner.close(); // Close the scanner when we're done to prevent resource leak
    }
}
