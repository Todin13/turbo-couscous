package com.IaInstitut.chatbot.controllers;

public class TaskControllerTest {

    public static void main(String[] args) {
        // Instantiate the TaskController
        TaskController taskController = new TaskController();

        // Simulate user input
        String userInputGreeting = "Hello";
        String userInputSchedule = "help to schedule appointment.";
        String userInputUnknown = "bye bye";

        // Process the input and print the responses
        System.out.println("User Input: " + userInputGreeting);
        System.out.println("Response: " + taskController.handleInput(userInputGreeting));
        System.out.println();

        System.out.println("User Input: " + userInputSchedule);
        System.out.println("Response: " + taskController.handleInput(userInputSchedule));
        System.out.println();

        System.out.println("User Input: " + userInputUnknown);
        System.out.println("Response: " + taskController.handleInput(userInputUnknown));
    }
}
