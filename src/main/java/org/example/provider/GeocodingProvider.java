package org.example.provider;

import org.example.model.Coordinates;

public interface GeocodingProvider {
    Coordinates getCoordinates(String city);
}