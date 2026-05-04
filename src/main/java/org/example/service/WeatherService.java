package org.example.service;

import org.example.model.Category;
import org.example.model.Coordinates;
import org.example.model.WeatherResponse;
import org.example.provider.GeocodingProvider;
import org.example.provider.WeatherProvider;

public class WeatherService {

    private final WeatherProvider weatherProvider;
    private final GeocodingProvider geocodingProvider;

    public WeatherService(WeatherProvider weatherClient, GeocodingProvider geocodingClient) {
        this.weatherProvider = weatherClient;
        this.geocodingProvider = geocodingClient;
    }

    public WeatherResponse getWeather(String city) {
        Coordinates coordinates = geocodingProvider.getCoordinates(city);
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
