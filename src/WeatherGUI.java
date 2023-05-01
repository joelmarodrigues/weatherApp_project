import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;

import org.json.JSONObject;

public class WeatherGUI extends JFrame {
    private JTextField searchField;
    private JLabel dateTimeLabel, locationLabel, temperatureLabel;
    private JTextArea weatherTextArea;
    private JButton searchButton;

    public WeatherGUI(WeatherApp weatherApp) {
        super("Weather App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 800));
        setLayout(new BorderLayout());

        // Create the search field with suggestion
        searchField = new JTextField("Search city");
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search city")) {
                    searchField.setText("");
                }
            }

            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search city");
                }
            }
        });

        // Create the search button with magnifying glass icon
        searchButton = new JButton();
        searchButton.setIcon(new ImageIcon("res/magnifier-16.png"));
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String location = searchField.getText();
                    String weatherData = weatherApp.getWeatherData(location);
                    displayWeatherData(weatherData);
                } catch (IOException ex) {
                    weatherTextArea.setText("Error retrieving weather data.");
                }
            }
        });


        // Create the search bar panel with search field and button
        JPanel searchBarPanel = new JPanel();
        searchBarPanel.setLayout(new BorderLayout());
        searchBarPanel.add(searchField, BorderLayout.CENTER);
        searchBarPanel.add(searchButton, BorderLayout.EAST);

        // Create the top panel with date/time label and search bar panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(searchBarPanel);
        topPanel.add(getDateTimeLabel());


        // Create the middle panel with location and temperature labels
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        middlePanel.add(getLocationLabel());
        middlePanel.add(getTemperatureLabel());

        // Create the bottom panel with weather data
        weatherTextArea = new JTextArea();
        weatherTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(weatherTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add the panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Set the size of the frame and make it visible
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void displayWeatherData(String weatherData) {
        // Parse the weather data and update the labels
        JSONObject json = new JSONObject(weatherData);
    
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dateTimeLabel.setText(now.format(formatter));
    
        String locationName = json.getString("name");
        String locationCountry = json.getJSONObject("sys").getString("country");
        locationLabel.setText(locationName + ", " + locationCountry);
    
        JSONObject main = json.getJSONObject("main");
        int temperature = (int) (main.getDouble("temp") - 273.15); // Convert from Kelvin to Celsius
        int feelsLike = (int) (main.getDouble("feels_like") - 273.15); // Convert from Kelvin to Celsius
    
        String summary = json.getJSONArray("weather").getJSONObject(0).getString("description");
        String formattedSummary = String.format("Feels like %d°C. %s.", feelsLike, summary);
    
        int visibility = json.getInt("visibility") / 1000; // Convert from meters to kilometers
        int humidity = main.getInt("humidity");
        double windSpeed = json.getJSONObject("wind").getDouble("speed");
    
        String weatherInfo = "Visibility: " + visibility + " km\n"
                + "Humidity: " + humidity + "%\n"
                + "Wind Speed: " + windSpeed + " m/s\n";
    
        temperatureLabel.setText(temperature + "°C");
        weatherTextArea.setText(formattedSummary + "\n\n" + weatherInfo);
    }
    
    
    private JLabel getDateTimeLabel() {
        dateTimeLabel = new JLabel();
        dateTimeLabel.setHorizontalAlignment(JLabel.RIGHT);
        return dateTimeLabel;
    }
    
    private JLabel getLocationLabel() {
        locationLabel = new JLabel();
        locationLabel.setFont(new Font("Helvetica", Font.PLAIN, 18));
        locationLabel.setHorizontalAlignment(JLabel.CENTER);
        return locationLabel;
    }
    
    private JLabel getTemperatureLabel() {
        temperatureLabel = new JLabel();
        temperatureLabel.setFont(new Font("Helvetica", Font.BOLD, 36));
        temperatureLabel.setHorizontalAlignment(JLabel.CENTER);
        return temperatureLabel;
    }
}    