package org.example.model;

import java.util.Map;

public record WeatherRequest(Map<String, String> queryStringParameters) { }
