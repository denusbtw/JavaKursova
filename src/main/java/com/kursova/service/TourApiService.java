package com.kursova.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.kursova.model.PageResponse;
import com.kursova.model.TourDTO;
import com.kursova.model.TransportDTO;

import java.net.URI;
import java.net.http.*;
import java.util.List;

import com.kursova.util.QueryParamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TourApiService {

    private static final Logger logger = LoggerFactory.getLogger(TourApiService.class);
    private static final String BASE_URL = "http://localhost:8080/api/tours";
    private final HttpClient client;
    private final ObjectMapper mapper;

    public TourApiService() {
        this(HttpClient.newHttpClient(), new ObjectMapper());
    }

    public TourApiService(HttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public PageResponse<TourDTO> getTours(int page, int size, String nameFilter,
                                          String type, String transportName,
                                          String mealOption, Integer minPrice,
                                          Integer maxPrice, Integer minDays,
                                          Integer maxDays, Double minRating,
                                          Double maxRating) {

        logger.info("Fetching tours with filters: page={}, size={}, name='{}', type='{}', transport='{}', meal='{}'",
                page, size, nameFilter, type, transportName, mealOption);

        String query = new QueryParamBuilder()
                .add("page", page)
                .add("size", size)
                .add("name", nameFilter)
                .add("type", type)
                .add("transportName", transportName)
                .add("mealOption", mealOption)
                .add("minPrice", minPrice)
                .add("maxPrice", maxPrice)
                .add("minDays", minDays)
                .add("maxDays", maxDays)
                .add("minRating", minRating)
                .add("maxRating", maxRating)
                .build();

        String url = BASE_URL + "?" + query;
        logger.debug("Final URL: {}", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        JavaType typeRef = mapper.getTypeFactory().constructParametricType(PageResponse.class, TourDTO.class);
        PageResponse<TourDTO> fallback = new PageResponse<>();
        fallback.setContent(List.of());
        fallback.setTotalPages(1);
        fallback.setNumber(0);

        PageResponse<TourDTO> result = sendAndParse(request, typeRef, fallback, "tours");
        logger.info("Fetched {} tours", result.getContent().size());
        return result;
    }

    public List<String> getTourTypes() {
        return getStringList("http://localhost:8080/api/tours/types", "tour types");
    }

    public List<String> getMealOptions() {
        return getStringList("http://localhost:8080/api/tours/mealOptions", "meal options");
    }

    public List<String> getTransportNames() {
        String url = "http://localhost:8080/api/transports";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        JavaType typeRef = mapper.getTypeFactory().constructCollectionType(List.class, TransportDTO.class);
        List<TransportDTO> transports = sendAndParse(request, typeRef, List.of(), "transports");

        return transports.stream().map(TransportDTO::getName).toList();
    }

    private List<String> getStringList(String url, String logLabel) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, String.class);
        return sendAndParse(request, listType, List.of(), logLabel);
    }

    private <T> T sendAndParse(HttpRequest request, JavaType typeRef, T defaultOnError, String logLabel) {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), typeRef);
        } catch (Exception e) {
            logger.error("Failed to fetch " + logLabel, e);
            return defaultOnError;
        }
    }
}
