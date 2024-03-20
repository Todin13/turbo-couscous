package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class weatherApi {
    public static Map<String, Map<String, String>> getWeatherForecast(String latitude, String longitude) {

        Map<String, Map<String, String>> goodMap = new HashMap<>();
        Map<String, List<String>> alldata = new HashMap<>();

        try {
            URL weatherURL = new URL("https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                    "&longitude=" + longitude +
                    "&hourly=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability," +
                    "precipitation,rain,showers,snowfall,snow_depth,weather_code,surface_pressure,cloud_cover," +
                    "cloud_cover_low,cloud_cover_mid,cloud_cover_high");
            HttpURLConnection weatherConnection = (HttpURLConnection) weatherURL.openConnection();
            weatherConnection.setRequestMethod("GET");

            BufferedReader weatherReader = new BufferedReader(new InputStreamReader(weatherConnection.getInputStream()));
            StringBuilder weatherResponse = new StringBuilder();
            String weatherLine;
            while ((weatherLine = weatherReader.readLine()) != null) {
                weatherResponse.append(weatherLine);
            }
            weatherReader.close();

            // Parse the JSON response and extract relevant data
            String jsonResponse = weatherResponse.toString();
            // Assuming the JSON structure contains an array of hourly data under the "hourly" key
            int startIndex = jsonResponse.indexOf("\"hourly\":") + "\"hourly\":".length();
            int endIndex = jsonResponse.indexOf("]}", startIndex) + 1;
            String hourlyData = jsonResponse.substring(startIndex, endIndex);
            // Split hourly data into individual hourly data objects
            String[] hourlyDataArray = hourlyData.split("\\],");

            for (String element : hourlyDataArray) {
                // Splitting the string to extract the list of integers
                String[] parts = element.split(":\\[");
                String listName = parts[0].replaceAll("[\"{}]", "");
                String data = parts[1].replaceAll("[\\[\\]\"]", ""); 
                String[] numbers = data.split(",");
                List<String> numberList = new ArrayList<>();
                for (String number : numbers) {
                    numberList.add(number);
                }
                alldata.put(listName, numberList);
            }

            weatherConnection.disconnect();

            List<String> time = alldata.get("time");

            // Loop over time data to construct the goodMap
            for (int i = 0; i < time.size(); i++) {
                Map<String, String> timeMap = new HashMap<>();
                for (Map.Entry<String, List<String>> entry : alldata.entrySet()) {
                    String key = entry.getKey();
                    List<String> value = entry.getValue();

                    // Skip time data
                    if (!key.equals("time")) {
                        timeMap.put(key, value.get(i));
                    }
                }
                goodMap.put(time.get(i), timeMap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return goodMap;
    }

    public static void main(String[] args) {
        String[] ipAndLocation = IPandLocationResolver.getIPAndLocation();
    
        String latitude = ipAndLocation[2];
        String longitude = ipAndLocation[3];
    
        Map<String, Map<String, String>> weatherForecast = getWeatherForecast(latitude, longitude);
    
        printHourlyForecast(weatherForecast);
    }
    
    private static void printHourlyForecast(Map<String, Map<String, String>> weatherForecast) {
        // Print the header
        System.out.println("Hourly Forecast:");
        
        // Iterate over each time slot
        for (Map.Entry<String, Map<String, String>> entry : weatherForecast.entrySet()) {
            String time = entry.getKey();
            Map<String, String> hourlyData = entry.getValue();
    
            // Print time
            System.out.println("Time: " + time);
    
            // Iterate over each key-value pair in hourly data
            for (Map.Entry<String, String> hourEntry : hourlyData.entrySet()) {
                String key = hourEntry.getKey();
                String value = hourEntry.getValue();
    
                // Print key-value pair
                System.out.printf("%-20s %-25s\n", key, value);
            }
    
            // Add a new line after printing all key-value pairs for this time
            System.out.println();
        }
    }    
}