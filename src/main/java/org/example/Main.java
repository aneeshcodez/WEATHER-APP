package org.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import java.io.IOException;

public class Main {
    private static final String API_KEY = "70bc1ae7fe9e8164e188a96246010f84";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private final OkHttpClient client;

    public Main() {
        this.client = new OkHttpClient();
    }

    public WeatherData getWeatherForCity(String city) throws IOException {
        String url = String.format("%s?q=%s&appid=%s&units=metric", BASE_URL, city, API_KEY);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            String responseBody = response.body().string();
            return parseWeatherData(responseBody);
        }
    }

    private WeatherData parseWeatherData(String jsonData) {
        JSONObject json = new JSONObject(jsonData);

        double temperature = json.getJSONObject("main").getDouble("temp");
        int humidity = json.getJSONObject("main").getInt("humidity");
        String weatherCondition = json.getJSONArray("weather")
                .getJSONObject(0)
                .getString("description");

        return new WeatherData(temperature, humidity, weatherCondition);
    }

    public static void main(String[] args) {
        Main app = new Main();
        try {
            WeatherData weatherData = app.getWeatherForCity("London");
            System.out.println(weatherData);
        } catch (IOException e) {
            System.err.println("Error fetching weather data: " + e.getMessage());
        }
    }

    private static class WeatherData {
        private final double temperature;
        private final int humidity;
        private final String weatherCondition;

        public WeatherData(double temperature, int humidity, String weatherCondition) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.weatherCondition = weatherCondition;
        }

        @Override
        public String toString() {
            return String.format(
                    "Current Weather:%n" +
                            "Temperature: %.1f°C%n" +
                            "Humidity: %d%%%n" +
                            "Conditions: %s",
                    temperature, humidity, weatherCondition);
        }
    }
}