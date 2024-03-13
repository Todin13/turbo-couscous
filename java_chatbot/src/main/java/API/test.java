package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class test {
    public static Map<String, Map<String, Object>> getWeatherForecast(String latitude, String longitude) {
        Map<String, Map<String, Object>> hourlyWeatherData = new HashMap<>(); // Map to store hourly weather data

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
            int endIndex = jsonResponse.indexOf("]", startIndex) + 1;
            String hourlyData = jsonResponse.substring(startIndex, endIndex);

            // Split hourly data into individual hourly data objects
            String[] hourlyDataArray = hourlyData.split("\\},\\{");

            // Process each hour's data
            for (String hourData : hourlyDataArray) {
                Map<String, Object> hourWeatherData = new HashMap<>(); // Map to store weather data for one hour

                // Extract specific fields for this hour and add to the map
                hourWeatherData.put("temperature_2m", extractFieldValue(hourData, "temperature_2m"));
                hourWeatherData.put("relative_humidity_2m", extractFieldValue(hourData, "relative_humidity_2m"));
                hourWeatherData.put("apparent_temperature", extractFieldValue(hourData, "apparent_temperature"));
                hourWeatherData.put("precipitation_probability", extractFieldValue(hourData, "precipitation_probability"));
                hourWeatherData.put("precipitation", extractFieldValue(hourData, "precipitation"));
                hourWeatherData.put("rain", extractFieldValue(hourData, "rain"));
                hourWeatherData.put("showers", extractFieldValue(hourData, "showers"));
                hourWeatherData.put("snowfall", extractFieldValue(hourData, "snowfall"));
                hourWeatherData.put("snow_depth", extractFieldValue(hourData, "snow_depth"));
                hourWeatherData.put("weather_code", extractFieldValue(hourData, "weather_code"));
                hourWeatherData.put("surface_pressure", extractFieldValue(hourData, "surface_pressure"));
                hourWeatherData.put("cloud_cover", extractFieldValue(hourData, "cloud_cover"));
                hourWeatherData.put("cloud_cover_low", extractFieldValue(hourData, "cloud_cover_low"));
                hourWeatherData.put("cloud_cover_mid", extractFieldValue(hourData, "cloud_cover_mid"));
                hourWeatherData.put("cloud_cover_high", extractFieldValue(hourData, "cloud_cover_high"));
                // Add more fields as needed...

                // Extract time for this hour and use it as the key in the outer map
                String time = extractFieldValue(hourData, "time");
                hourlyWeatherData.put(time, hourWeatherData);
            }

            weatherConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hourlyWeatherData;
    }

    // Helper method to extract field value from JSON object string
    private static String extractFieldValue(String jsonObjectString, String fieldName) {
        int fieldIndex = jsonObjectString.indexOf("\"" + fieldName + "\":");
        if (fieldIndex != -1) {
            int valueStartIndex = jsonObjectString.indexOf("\"", fieldIndex + fieldName.length() + 3) + 1;
            int valueEndIndex = jsonObjectString.indexOf("\"", valueStartIndex);
            return jsonObjectString.substring(valueStartIndex, valueEndIndex);
        }
        return null; // Field not found
    }

    public static void main(String[] args) {
        String[] ipAndLocation = IPandLocationResolver.getIPAndLocation();
    
        String latitude = ipAndLocation[2];
        String longitude = ipAndLocation[3];
    
        Map<String, Map<String, Object>> weatherForecast = getWeatherForecast(latitude, longitude);
    
        printHourlyForecast(weatherForecast);
    }
    
    private static void printHourlyForecast(Map<String, Map<String, Object>> weatherForecast) {
        // Print the header
        System.out.println("Hourly Forecast:");
        
        // Iterate over each time slot
        for (Map.Entry<String, Map<String, Object>> entry : weatherForecast.entrySet()) {
            String time = entry.getKey();
            Map<String, Object> hourlyData = entry.getValue();
    
            // Print time
            System.out.println("Time: " + time);
    
            // Iterate over each key-value pair in hourly data
            for (Map.Entry<String, Object> hourEntry : hourlyData.entrySet()) {
                String key = hourEntry.getKey();
                Object value = hourEntry.getValue();
    
                // Print key-value pair
                System.out.printf("%-20s %-25s\n", key, value);
            }
    
            // Add a new line after printing all key-value pairs for this time
            System.out.println();
        }
    }    
}