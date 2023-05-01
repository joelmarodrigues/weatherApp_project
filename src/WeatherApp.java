import java.io.IOException;

public class WeatherApp {
    private WeatherAPI weatherAPI;
    private WeatherGUI weatherGUI;
    
    public WeatherApp(WeatherAPI weatherAPI) {
        this.weatherAPI = weatherAPI;
        this.weatherGUI = new WeatherGUI(this);
    }
    
    public String getWeatherData(String location) throws IOException {
        String weatherData = weatherAPI.getWeatherData(location);
        weatherGUI.displayWeatherData(weatherData);
        return weatherData;
    }
    
    public static void main(String[] args) {
        String apiKey = "2e36f31b1146ad8813f4a891c96327bc";
        WeatherAPI weatherAPI = new WeatherAPI(apiKey);
        new WeatherApp(weatherAPI);
    }
}