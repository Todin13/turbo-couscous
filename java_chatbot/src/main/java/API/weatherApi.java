package API;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class weatherApi {
    public static String[] getWeatherForecast(String latitude, String longitude) {
        String[] weatherData = new String[14]; // Data from the API

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
            // Example: For now, let's just retrieve the whole response as a string
            String weatherResponseString = weatherResponse.toString();
            weatherData[0] = weatherResponseString;

            weatherConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weatherData;
    }

    public static void main(String[] args) {
        String[] ipAndLocation = IPandLocationResolver.getIPAndLocation();

        String latitude = ipAndLocation[2];
        String longitude = ipAndLocation[3];

        String[] weatherForecast = getWeatherForecast(latitude, longitude);

        System.out.println("Weather Forecast for Latitude: " + latitude + ", Longitude: " + longitude);
        System.out.println(weatherForecast[0]); // Printing the whole response for now
    }

}