package com.IaInstitut.chatbot.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientHelper {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = System.getenv("OPENAI_API_KEY"); // Replace with your actual API key or add to environment variables

    private ObjectMapper objectMapper = new ObjectMapper()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public HttpResponse sendPostRequest(String message) throws IOException {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    HttpPost postRequest = new HttpPost(API_URL);
    postRequest.setHeader("Authorization", "Bearer " + API_KEY);
    postRequest.setHeader("Content-Type", "application/json");

    // Construct the messages array
    List<Map<String, String>> messages = new ArrayList<>();
    Map<String, String> userMessage = new HashMap<>();
    userMessage.put("role", "user");
    userMessage.put("content", message);
    messages.add(userMessage);

    Map<String, Object> body = new HashMap<>();
    body.put("messages", messages);
    // Specify the model if required by your API endpoint
    body.put("model", "gpt-3.5-turbo");
    //body.put("model", "gpt-4-0613");
    

    String jsonBody = objectMapper.writeValueAsString(body);

    postRequest.setEntity(new StringEntity(jsonBody));
    return httpClient.execute(postRequest);
    
}
    

    public OpenAIResponse parseResponse(HttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();
        String jsonResponse = EntityUtils.toString(response.getEntity());
        
        // Log the status code and response for debugging
        System.out.println("Response Status Code: " + statusCode);
        System.out.println("Raw JSON Response: " + jsonResponse);
        
        if (statusCode >= 200 && statusCode < 300) {
            // Success response
            return objectMapper.readValue(jsonResponse, OpenAIResponse.class);
        } else {
            // Handle non-success responses
            throw new IOException("Unexpected response status: " + statusCode + " with body: " + jsonResponse);
        }
    }
    
}