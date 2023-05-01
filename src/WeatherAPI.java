import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class WeatherAPI {
    private String apiKey;
    
    public WeatherAPI(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getWeatherData(String location) throws IOException {
        String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=" + apiKey;
        URL url = new URL(apiUrl);
        URLConnection connection = url.openConnection();
        connection.connect();
        
        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        scanner.close();
        
        return stringBuilder.toString();
    }
}