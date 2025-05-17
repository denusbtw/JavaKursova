package com.kursova.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.kursova.model.PageResponse;
import com.kursova.model.TourDTO;
import com.kursova.model.TransportDTO;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TourApiService {

    private static final Logger logger = LoggerFactory.getLogger(TourApiService.class);
    private static final String BASE_URL = "http://localhost:8080/api/tours";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public PageResponse<TourDTO> getTours(int page, int size, String nameFilter,
                                          String type, String transportName,
                                          String mealOption, Integer minPrice,
                                          Integer maxPrice, Integer minDays,
                                          Integer maxDays, Double minRating,
                                          Double maxRating) {
        try {
            StringBuilder logMessage = new StringBuilder("Fetching tours with filters:");

            logMessage.append(" page=").append(page).append(", size=").append(size);

            if (nameFilter != null && !nameFilter.isBlank())
                logMessage.append(", name='").append(nameFilter).append("'");
            if (type != null && !type.isBlank())
                logMessage.append(", type='").append(type).append("'");
            if (transportName != null && !transportName.isBlank())
                logMessage.append(", transport='").append(transportName).append("'");
            if (mealOption != null && !mealOption.isBlank())
                logMessage.append(", meal='").append(mealOption).append("'");
            if (minDays != null || maxDays != null)
                logMessage.append(", days=[").append(minDays != null ? minDays : "").append("-").append(maxDays != null ? maxDays : "").append("]");
            if (minPrice != null || maxPrice != null)
                logMessage.append(", price=[").append(minPrice != null ? minPrice : "").append("-").append(maxPrice != null ? maxPrice : "").append("]");
            if (minRating != null || maxRating != null)
                logMessage.append(", rating=[").append(minRating != null ? minRating : "").append("-").append(maxRating != null ? maxRating : "").append("]");

            logger.info(logMessage.toString());

            StringBuilder urlBuilder = new StringBuilder(BASE_URL + "?page=" + page + "&size=" + size);

            if (nameFilter != null && !nameFilter.isBlank())
                urlBuilder.append("&name=").append(URLEncoder.encode(nameFilter, StandardCharsets.UTF_8));
            if (type != null)
                urlBuilder.append("&type=").append(URLEncoder.encode(type, StandardCharsets.UTF_8));
            if (transportName != null)
                urlBuilder.append("&transportName=").append(URLEncoder.encode(transportName, StandardCharsets.UTF_8));
            if (mealOption != null)
                urlBuilder.append("&mealOption=").append(URLEncoder.encode(mealOption, StandardCharsets.UTF_8));
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

            String finalUrl = urlBuilder.toString();
            logger.debug("Final URL: {}", finalUrl);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(finalUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JavaType typeRef = mapper.getTypeFactory().constructParametricType(PageResponse.class, TourDTO.class);
            PageResponse<TourDTO> result = mapper.readValue(response.body(), typeRef);
            logger.info("Successfully fetched {} tours", result.getContent().size());
            return result;
        } catch (Exception e) {
            logger.error("Failed to fetch tours", e);
            PageResponse<TourDTO> empty = new PageResponse<>();
            empty.setContent(Collections.emptyList());
            empty.setTotalPages(1);
            empty.setNumber(0);
            return empty;
        }
    }

    public List<String> getTourTypes() {
        try {
            logger.info("Fetching tour types...");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tours/types"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<String> types = mapper.readValue(response.body(), mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            logger.info("Fetched {} tour types", types.size());
            return types;
        } catch (Exception e) {
            logger.error("Failed to fetch tour types", e);
            return Collections.emptyList();
        }
    }

    public List<String> getTransportNames() {
        try {
            logger.info("Fetching transport names...");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/transports"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<TransportDTO> transportList = mapper.readValue(
                    response.body(),
                    mapper.getTypeFactory().constructCollectionType(List.class, TransportDTO.class)
            );

            List<String> names = transportList.stream().map(TransportDTO::getName).toList();
            logger.info("Fetched {} transport names", names.size());
            return names;
        } catch (Exception e) {
            logger.error("Failed to fetch transport names", e);
            return Collections.emptyList();
        }
    }

    public List<String> getMealOptions() {
        try {
            logger.info("Fetching meal options...");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/tours/mealOptions"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<String> options = mapper.readValue(
                    response.body(),
                    mapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );

            logger.info("Fetched {} meal options", options.size());
            return options;
        } catch (Exception e) {
            logger.error("Failed to fetch meal options", e);
            return Collections.emptyList();
        }
    }
}
