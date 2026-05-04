# Weather Temperature Service

An AWS Lambda function that returns the current temperature and temperature category for a given city, built with Java and the Open-Meteo API.

---

## Solution Description

The function fetches coordinates by city name, then current temperature based on those coordinates from the Open-Meteo API, classifies it into a category, and returns the result as JSON.

**Example response:**
```json
{
  "city": "Wrocław",
  "temperature": 24.8,
  "category": "Warm"
}
```

**Temperature categories:**

| Range | Category |
|-------|----------|
| Below 0°C | Freezing |
| 0–10°C | Cold |
| 10–20°C | Mild |
| 20–30°C | Warm |
| Above 30°C | Hot |

---

## Key Design Decisions

### Separation of Responsibilities

Each class has a single, clearly defined role:

- **`WeatherHandler`** - Lambda entry point only. Constructs dependencies and delegates to `WeatherService`.
- **`WeatherService`** - Orchestrates the weather lookup: resolves coordinates, fetches temperature, and classifies the result.
- **`OpenMeteoClient`** - Handles all HTTP communication with the Open-Meteo API.

### Interface-Based Design

`OpenMeteoClient` implements `WeatherProvider` and `GeocodingProvider` interfaces rather than being used directly by `WeatherService`. This means:

- `WeatherService` depends on an abstraction, not a concrete class
- Swapping to a different weather provider requires no changes to existing classes
- Different weather provider class don't have to implement `GeocodingProvider` functionality if it doesn't need to
- Dependencies can be replaced with test doubles during unit testing

### Constructor Injection

`WeatherHandler` and `OpenMeteoClient` has two constructors - one used by AWS (no-arg), and one used by tests (with args). This allows clearly separating production and test environments.

---

## Unit Testing Without the Real API

### WeatherService

Because `WeatherService` depends on the `WeatherProvider` interface, a fake implementation can be injected instead of the real client. This verifies that the service correctly assembles the response and classifies the temperature it receives:

```java
WeatherProvider stub = coordinates -> 15.0;

WeatherService service = new WeatherService(stub);
WeatherResponse response = service.getWeather("Wrocław");

assertEquals("Mild", response.category());
assertEquals(15.0, response.temperature());
```

### OpenMeteoClient

`OpenMeteoClient` owns the HTTP communication, so it cannot be meaningfully tested with a fake. Instead, [WireMock](https://wiremock.org/) can be used to spin up a real local HTTP server that the client talks to. This verifies URL construction, JSON parsing, and error handling without any real network calls.

The client accepts separate base URLs for the forecast and geocoding APIs, so both can be pointed at the same WireMock server in tests:

```java
OpenMeteoClient client = new OpenMeteoClient("http://localhost:8080", "http://localhost:8080");
 
stubFor(get(urlPathEqualTo("/v1/search"))
    .willReturn(aResponse()
        .withHeader("Content-Type", "application/json")
        .withBody("{ \"results\": [{ \"latitude\": 51.1, \"longitude\": 17.03 }] }")));
 
stubFor(get(urlPathEqualTo("/v1/forecast"))
    .willReturn(aResponse()
        .withHeader("Content-Type", "application/json")
        .withBody("{ \"current\": { \"temperature_2m\": 15.0 } }")));
 
Coordinates coords = client.getCoordinates("Wrocław");
double temp = client.getCurrentTemperature(coords);
 
assertEquals(51.1, coords.latitude());
assertEquals(15.0, temp);
```
### WeatherHandler

The handler can be tested by injecting a fake `WeatherService` through the constructor, verifying that it reads the city from the request and returns whatever the service provides:

```java
WeatherService fakeService = city -> new WeatherResponse(city, 15.0, "Mild");

WeatherHandler handler = new WeatherHandler(fakeService);
WeatherResponse response = handler.handleRequest(new WeatherRequest("Wrocław"), null);

assertEquals("Wrocław", response.city());
assertEquals("Mild", response.category());
```