import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import org.json.JSONObject;

public class WeatherGUI extends JFrame {
    private JTextField searchField;
    private JLabel dateTimeLabel, locationLabel, temperatureLabel, summaryLabel, timezoneLabel;
    private JTextArea weatherTextArea;
    private JButton searchButton;

    public WeatherGUI(WeatherApp weatherApp) {
        super("Weather App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setPreferredSize(new Dimension(600, 800));
        setLayout(new FlowLayout()); // Set the layout to FlowLayout

        // Create the search field with suggestion
        searchField = new JTextField("Search city");
        searchField.setPreferredSize(new Dimension(250, 30));
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

        // Create a key listener for the search field
        searchField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        String location = searchField.getText();
                        String weatherData = weatherApp.getWeatherData(location);
                        displayWeatherData(weatherData);
                    } catch (IOException ex) {
                        weatherTextArea.setText("Error retrieving weather data.");
                    }
                }
            }
        });

        // Create the search button with magnifying glass icon
        searchButton = new JButton();
        searchButton.setIcon(new ImageIcon("res/magnifier-16.png"));
        searchButton.setFocusPainted(false);
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
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Create the top panel with search bar
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.setPreferredSize(new Dimension(600, 50));
        topPanel.add(searchPanel, BorderLayout.NORTH);

        // Create the middle panel with location and temperature labels
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        middlePanel.setPreferredSize(new Dimension(600, 300));


        // Create the location label
        JLabel locationLabel = getLocationLabel();
        locationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        locationLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        middlePanel.add(locationLabel);

        // Create the timezone label
        JLabel timezoneLabel = getTimezoneLabel();
        timezoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timezoneLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        middlePanel.add(timezoneLabel);

        // Create the temperature label
        JLabel temperatureLabel = getTemperatureLabel();
        temperatureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        temperatureLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        middlePanel.add(temperatureLabel);

        // Create the summary label
        JLabel summaryLabel = getSummaryLabel();
        summaryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        summaryLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        middlePanel.add(summaryLabel);

        // Create the date/time label
        JLabel dateTimeLabel = getDateTimeLabel();
        dateTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dateTimeLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        middlePanel.add(dateTimeLabel);

        // Create the bottom panel with weather data
        weatherTextArea = new JTextArea();
        weatherTextArea.setEditable(false);
        weatherTextArea.setLineWrap(true);
        weatherTextArea.setWrapStyleWord(true);
        weatherTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        weatherTextArea.setBackground(getBackground());

        // Add the panels to the frame
        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(weatherTextArea, BorderLayout.SOUTH);

        // Set the size of the frame and make it visible
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void displayWeatherData(String weatherData) {
        // Parse the weather data and update the labels
        JSONObject json = new JSONObject(weatherData);
    
        // Format the date and time strings
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String date = now.format(dateFormatter);
        String time = now.format(timeFormatter);

        dateTimeLabel.setText(date + " | " + time);

        String locationName = json.getString("name");
        String locationCountry = json.getJSONObject("sys").getString("country");
        locationLabel.setText(locationName + ", " + locationCountry);
    
        JSONObject main = json.getJSONObject("main");
        int temperature = (int) (main.getDouble("temp") - 273.15); // Convert from Kelvin to Celsius
        int feelsLike = (int) (main.getDouble("feels_like") - 273.15); // Convert from Kelvin to Celsius
    
        JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
        String summary = weather.getString("description");
        summary = summary.substring(0, 1).toUpperCase() + summary.substring(1); // Capitalize the first letter
    
        String formattedSummary = String.format("%s. Feels like %d°C.", summary, feelsLike);
    
        int visibility = json.getInt("visibility") / 1000; // Convert from meters to kilometers
        int humidity = main.getInt("humidity");
        double windSpeed = json.getJSONObject("wind").getDouble("speed");

        String timezone = json.optString("timezone", ""); // Use optString() instead of getString()
        timezoneLabel.setText("Timezone: " + timezone);
        
        int timezoneOffsetInSeconds = json.getInt("timezone");
        int timezoneOffsetInHours = timezoneOffsetInSeconds / 3600; // Convert seconds to hours
        String timezoneOffsetStr = String.format("%+03d", timezoneOffsetInHours); // Format the offset as "+hh" or "-hh"
        
        timezoneLabel.setText("Timezone: UTC " + timezoneOffsetStr +  ":00"); // Display the timezone in the format "UTC±hh:00"        

        String weatherInfo = "Visibility: " + visibility + " km\n"
                + "Humidity: " + humidity + "%\n"
                + "Wind Speed: " + windSpeed + " m/s\n";
    
        summaryLabel.setText(summary);
        temperatureLabel.setText(temperature + "°C");
        weatherTextArea.setText(formattedSummary + "\n\n" + weatherInfo);
    }

    //middlePanel JLabels Start Here

    private JLabel getLocationLabel() {
        locationLabel = new JLabel();
        locationLabel.setFont(new Font("Helvetica", Font.BOLD, 36));
        locationLabel.setHorizontalAlignment(JLabel.CENTER);
        return locationLabel;
    }

    private JLabel getDateTimeLabel() {
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Helvetica", Font.PLAIN, 14));
        dateTimeLabel.setHorizontalAlignment(JLabel.CENTER);
        return dateTimeLabel;
    }

    private JLabel getTemperatureLabel() {
        temperatureLabel = new JLabel();
        temperatureLabel.setFont(new Font("Helvetica", Font.BOLD, 84));
        temperatureLabel.setHorizontalAlignment(JLabel.CENTER);
        return temperatureLabel;
    }

    private JLabel getSummaryLabel() {
        summaryLabel = new JLabel();
        summaryLabel.setFont(new Font("Helvetica", Font.PLAIN, 32));
        summaryLabel.setHorizontalAlignment(JLabel.CENTER);
        return summaryLabel;
    }

    private JLabel getTimezoneLabel() {
        timezoneLabel = new JLabel();
        timezoneLabel.setFont(new Font("Helvetica", Font.PLAIN, 14));
        timezoneLabel.setHorizontalAlignment(JLabel.CENTER);
        
        return timezoneLabel;
    }

    //middlePanel Label finishes here

}
