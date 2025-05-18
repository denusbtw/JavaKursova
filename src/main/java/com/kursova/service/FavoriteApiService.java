package com.kursova.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kursova.model.PageResponse;
import com.kursova.model.TourDTO;
import com.kursova.util.QueryParamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FavoriteApiService {
    private static final Logger logger = LoggerFactory.getLogger(FavoriteApiService.class);
    private static final String URL = "http://localhost:8080/api/favorites";

    private final HttpClient client;
    private final ObjectMapper mapper;

    // Конструктор за замовчуванням — для продакшну
    public FavoriteApiService() {
        this(HttpClient.newHttpClient(), new ObjectMapper());
    }

    // Конструктор для тестів — ін'єкція залежностей
    public FavoriteApiService(HttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public PageResponse<TourDTO> getFavorites(
            String name, String type, String mealOption,
            Integer minDays, Integer maxDays,
            Integer minPrice, Integer maxPrice,
            Double minRating, Double maxRating,
            String transportName,
            int page, int size
    ) {
        try {
            String queryParams = new QueryParamBuilder()
                    .add("page", page)
                    .add("size", size)
                    .add("name", name)
                    .add("type", type)
                    .add("mealOption", mealOption)
                    .add("transportName", transportName)
                    .add("minPrice", minPrice)
                    .add("maxPrice", maxPrice)
                    .add("minDays", minDays)
                    .add("maxDays", maxDays)
                    .add("minRating", minRating)
                    .add("maxRating", maxRating)
                    .build();

            String fullUrl = URL + "?" + queryParams;
            logger.debug("GET {}", fullUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JavaType typeRef = mapper.getTypeFactory()
                    .constructParametricType(PageResponse.class, TourDTO.class);

            return mapper.readValue(response.body(), typeRef);

        } catch (Exception e) {
            logger.error("Failed to fetch favorites", e);
            PageResponse<TourDTO> empty = new PageResponse<>();
            empty.setContent(List.of());
            empty.setNumber(0);
            empty.setTotalPages(1);
            return empty;
        }
    }

    private void sendRequest(HttpRequest request, String operation, int tourId) {
        try {
            client.send(request, HttpResponse.BodyHandlers.discarding());
            logger.info("{} tour with id={}", operation, tourId);
        } catch (Exception e) {
            logger.error("Failed to {} tour with id={}", operation.toLowerCase(), tourId, e);
        }
    }

    public void addToFavorites(int tourId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(tourId)))
                .header("Content-Type", "application/json")
                .build();
        sendRequest(request, "Add to favorites", tourId);
    }

    public void removeFromFavorites(int tourId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/" + tourId))
                .DELETE()
                .build();
        sendRequest(request, "Remove from favorites", tourId);
    }

}
