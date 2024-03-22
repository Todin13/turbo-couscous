package com.IaInstitut.chatbot.model;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.IaInstitut.chatbot.controllers.OpenAIChatbot;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NlpEngine {
    private SentenceDetectorME sentenceDetector;
    private Tokenizer tokenizer;
    private POSTaggerME posTagger;
    private JSONObject dataset;
    private OpenAIChatbot openAIChatbot = new OpenAIChatbot();

    public NlpEngine() {
        try {
            initModels();
            loadDataset();
        } catch (IOException e) {
            throw new RuntimeException("Error loading NLP models or dataset.", e);
        }
    }

    private void initModels() throws IOException {
        sentenceDetector = new SentenceDetectorME(new SentenceModel(loadModel("src\\resources\\models\\opennlp-en-ud-ewt-sentence-1.0-1.9.3.bin")));
        tokenizer = new TokenizerME(new TokenizerModel(loadModel("src\\resources\\models\\opennlp-en-ud-ewt-tokens-1.0-1.9.3.bin")));
        posTagger = new POSTaggerME(new POSModel(loadModel("src\\resources\\models\\opennlp-en-ud-ewt-pos-1.0-1.9.3.bin")));
    }

    private InputStream loadModel(String filePath) throws IOException {
        File modelFile = new File(filePath);
        if (!modelFile.exists()) {
            throw new IOException("Model file does not exist: " + filePath);
        }
        return new FileInputStream(modelFile);
    }

    private void loadDataset() {
        JSONParser parser = new JSONParser();
        try {
            dataset = (JSONObject) parser.parse(new FileReader("src\\resources\\intents.json"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load the dataset.");
        }
    }

    public NlpResult processInput(String input) {
        String determinedIntent = "unknown";
        Map<String, String[]> sentenceDetails = new HashMap<>();

        // Use sentence detection, tokenization, and POS tagging as needed
        String[] sentences = sentenceDetector.sentDetect(input);
        for (String sentence : sentences) {
            String[] tokens = tokenizer.tokenize(sentence);
            // Skipping POS tagging for now, but you can use it if needed
            // String[] posTags = posTagger.tag(tokens);

            // Add all tokens to the sentenceDetails map
            sentenceDetails.put(sentence, tokens);

            // Intent determination logic
            determinedIntent = determineIntent(tokens);
        }

        // Query the dataset if the intent is unknown
        String response = "unknown".equals(determinedIntent) ? queryDatasetForIntent(input) : null;

        // Use response from dataset or default response
        if (response != null) {
            // Return the response from dataset
            return new NlpResult("responseFromDataset", Map.of("response", new String[]{response}));
        } else {
            // Return the default response
            return new NlpResult(determinedIntent, sentenceDetails);
        }
    }

    private String determineIntent(String[] tokens) {
        for (String token : tokens) {
            if (token.matches("schedule|appointment")) {
                return "scheduleAppointment";
            } else if (token.toLowerCase().contains("email")) {
                return "sendEmail";
            } else if (token.toLowerCase().matches("weather|forecast|temperature")) {
                return "weatherForecast";
            }else if (token.toLowerCase().matches("calendar")) {
                return "showAppointmentSummary";
            } else if (token.toLowerCase().matches("delete event")){
                return "deleteEvent";
            }
            
        }
        return "unknown";
    }


    private String queryDatasetForIntent(String input) {
        // Normalize the input: lowercase and remove punctuation.
        String inputLower = input.toLowerCase().replaceAll("\\p{Punct}", "").trim();

        JSONArray intents = (JSONArray) dataset.get("intents");
        for (Object intentObj : intents) {
            JSONObject intent = (JSONObject) intentObj;
            JSONArray patterns = (JSONArray) intent.get("patterns");

            for (Object patternObj : patterns) {
                String pattern = ((String) patternObj).toLowerCase().replaceAll("\\p{Punct}", "");

                // Create a regex pattern to match the whole word, case-insensitive.
                Pattern p = Pattern.compile("\\b" + pattern + "\\b", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(inputLower);

                if (m.find()) { // If the pattern matches,
                    JSONArray responses = (JSONArray) intent.get("responses");
                    int index = new Random().nextInt(responses.size());
                    String response = (String) responses.get(index); // Select a random response.
                    return response;
                }
            }
        }

        return openAIChatbot.sendMessage(input);
    }


}


