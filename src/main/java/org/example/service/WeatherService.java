package org.example.service;

import org.example.model.Category;
import org.example.model.Coordinates;
import org.example.model.WeatherResponse;
import org.example.provider.WeatherProvider;

public class WeatherService {

    private final WeatherProvider weatherProvider;

    public WeatherService(WeatherProvider weatherClient) {
        this.weatherProvider = weatherClient;
    }

    public WeatherResponse getWeather(String city) {
        Coordinates coordinates = new Coordinates(51.1, 17.03); // coordinates for Wrocław
        double temperature = weatherProvider.getCurrentTemperature(coordinates);
        Category category = this.categorizeTemperature(temperature);

        return new WeatherResponse(city, temperature, category.toString());
    }

    private Category categorizeTemperature(double temperature) {
        if (temperature < 0) return Category.FREEZING;
        if (temperature >= 0 && temperature < 10) return Category.COLD;
        if (temperature >= 10 && temperature < 20) return Category.MILD;
        if (temperature >= 20 && temperature < 30) return Category.WARM;

        return Category.HOT;
    }
}
