package com.IaInstitut.chatbot.model;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NlpEngine {
    private SentenceDetectorME sentenceDetector;
    private Tokenizer tokenizer;
    private POSTaggerME posTagger;

    public NlpEngine() {
        try {
            initModels();
        } catch (IOException e) {
            throw new RuntimeException("Error loading NLP models.", e);
        }
    }

    private void initModels() throws IOException {
        sentenceDetector = new SentenceDetectorME(new SentenceModel(loadModel("C:/Users/HP/Documents/ChatbotJava/src/resources/models/opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin")));
        tokenizer = new TokenizerME(new TokenizerModel(loadModel("C:/Users/HP/Documents/ChatbotJava/src/resources/models/opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin")));
        posTagger = new POSTaggerME(new POSModel(loadModel("C:/Users/HP/Documents/ChatbotJava/src/resources/models/opennlp-en-ud-ewt-pos-1.0-1.9.3.bin")));
    }

    private InputStream loadModel(String filePath) throws IOException {
        File modelFile = new File(filePath);
        if (!modelFile.exists()) {
            throw new IOException("Model file does not exist: " + filePath);
        }
        return new FileInputStream(modelFile);
    }

    public NlpResult processInput(String input) {
        String[] sentences = sentenceDetector.sentDetect(input);
        Map<String, String[]> sentenceDetails = new HashMap<>();

        String determinedIntent = "unknown";  // Default to "unknown"

        for (String sentence : sentences) {
            String[] tokens = tokenizer.tokenize(sentence);
            String[] posTags = posTagger.tag(tokens);
            sentenceDetails.put(sentence, tokens); // For simplicity, just using tokens

            // Basic intent determination logic based on keywords
            for (String token : tokens) {
                if ("hello".equalsIgnoreCase(token) || "hi".equalsIgnoreCase(token)) {
                    determinedIntent = "greeting";
                    break; // Stop checking tokens if we've identified the intent
                } else if (token.matches("schedule|appointment")) {
                    determinedIntent = "scheduleAppointment";
                    break;
                } else if (token.matches("quantum|physics")) {
                    determinedIntent = "physicsInfo";
                    break;
                }
            }
        }

        return new NlpResult(determinedIntent, sentenceDetails);
    }
}