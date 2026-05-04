package org.example.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.example.model.WeatherRequest;
import org.example.model.WeatherResponse;
import org.example.provider.openmeteo.OpenMeteoClient;
import org.example.service.WeatherService;

public class WeatherHandler implements RequestHandler<WeatherRequest, WeatherResponse> {

    private final WeatherService weatherService;

    public WeatherHandler() {
        OpenMeteoClient client = new OpenMeteoClient();
        this.weatherService = new WeatherService(client, client);
    }

    public WeatherHandler(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public WeatherResponse handleRequest(WeatherRequest request, Context context) {
       return weatherService.getWeather(request.city());
    }
}
