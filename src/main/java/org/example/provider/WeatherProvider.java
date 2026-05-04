package org.example.provider;

import org.example.model.Coordinates;

public interface WeatherProvider {
    double getCurrentTemperature(Coordinates coordinates);
}
