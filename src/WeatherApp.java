import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class WeatherApp {
    private final String apiKey;

    public WeatherApp(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getWeatherData(String location) throws IOException {
        // Encode the location string for use in the URL
        String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);

        // Construct the URL of the weather API endpoint
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedLocation + "&appid=" + apiKey;
        URI uri = URI.create(apiUrl);
        URL url = uri.toURL();

        // Open a connection to the API endpoint and get the response as a string
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream responseStream = connection.getInputStream();
        Scanner scanner = new Scanner(responseStream, StandardCharsets.UTF_8);
        String responseBody = scanner.useDelimiter("\\A").next();

        // Close the resources
        scanner.close();
        responseStream.close();
        connection.disconnect();

        return responseBody;
    }

    public static void main(String[] args) {
        String apiKey = "API_KEY_HERE";
        WeatherApp weatherApp = new WeatherApp(apiKey);
        WeatherGUI weatherGUI = new WeatherGUI(weatherApp);
        weatherGUI.pack();
        weatherGUI.setLocationRelativeTo(null);
        weatherGUI.setVisible(true);
    }
}
