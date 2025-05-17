package com.kursova.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kursova.model.PageResponse;
import com.kursova.model.TourDTO;
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
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public PageResponse<TourDTO> getFavorites(
            String name, String type, String mealOption,
            Integer minDays, Integer maxDays,
            Integer minPrice, Integer maxPrice,
            Double minRating, Double maxRating,
            String transportName,
            int page, int size
    ) {
        try {
            StringBuilder urlBuilder = new StringBuilder(URL)
                    .append("?page=").append(page).append("&size=").append(size);

            if (name != null && !name.isBlank())
                urlBuilder.append("&name=").append(URLEncoder.encode(name, StandardCharsets.UTF_8));
            if (type != null)
                urlBuilder.append("&type=").append(URLEncoder.encode(type, StandardCharsets.UTF_8));
            if (mealOption != null)
                urlBuilder.append("&mealOption=").append(URLEncoder.encode(mealOption, StandardCharsets.UTF_8));
            if (transportName != null)
                urlBuilder.append("&transportName=").append(URLEncoder.encode(transportName, StandardCharsets.UTF_8));
            if (minPrice != null)
                urlBuilder.append("&minPrice=").append(minPrice);
            if (maxPrice != null)
                urlBuilder.append("&maxPrice=").append(maxPrice);
            if (minDays != null)
                urlBuilder.append("&minDays=").append(minDays);
            if (maxDays != null)
                urlBuilder.append("&maxDays=").append(maxDays);
            if (minRating != null)
                urlBuilder.append("&minRating=").append(minRating);
            if (maxRating != null)
                urlBuilder.append("&maxRating=").append(maxRating);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
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

    public void addToFavorites(int tourId) {
        try {
            logger.info("Adding tour with id={} to favorites", tourId);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(tourId)))
                    .header("Content-Type", "application/json")
                    .build();
            client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            logger.error("Failed to add tour with id={} to favorites", tourId, e);
        }
    }

    public void removeFromFavorites(int tourId) {
        try {
            logger.info("Removing tour with id={} from favorites", tourId);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/" + tourId))
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            logger.error("Failed to remove tour with id={} from favorites", tourId, e);
        }
    }
}

