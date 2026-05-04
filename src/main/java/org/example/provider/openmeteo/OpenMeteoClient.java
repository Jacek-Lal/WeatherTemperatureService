package org.example.provider.openmeteo;

import org.example.model.Coordinates;
import org.example.provider.WeatherProvider;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class OpenMeteoClient implements WeatherProvider {

    private static final String FORECAST_PATH =
            "/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m";

    private final String baseUrl;
    private final ObjectMapper mapper;
    private final HttpClient http;

    public OpenMeteoClient() {
        this("https://api.open-meteo.com");
    }

    public OpenMeteoClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.mapper = new ObjectMapper();
        this.http = HttpClient.newHttpClient();
    }

    @Override
    public double getCurrentTemperature(Coordinates coordinates) {
        try {
            String url = String.format(Locale.US, baseUrl + FORECAST_PATH,
                    coordinates.latitude(), coordinates.longitude());
            JsonNode root = fetchJson(url);
            return root.get("current").get("temperature_2m").asDouble();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch temperature", e);
        }
    }

    private JsonNode fetchJson(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body());
    }
}