package com.kursova.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kursova.model.PageResponse;
import com.kursova.model.TourDTO;
import com.kursova.model.TransportDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TourApiServiceTest {

    private HttpClient client;
    private ObjectMapper mapper;
    private TourApiService service;

    @BeforeEach
    void setup() {
        client = mock(HttpClient.class);
        mapper = spy(new ObjectMapper()); // потрібен реальний для JSON
        service = new TourApiService(client, mapper);
    }

    @Test
    void getTours_shouldIncludeAllParams() throws Exception {
        String json = """
        {
          "content": [{"id": 1, "name": "Alps"}],
          "number": 0,
          "totalPages": 1
        }
        """;

        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn(json);
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(response);

        PageResponse<TourDTO> result = service.getTours(
                0, 10, "Alps", "Adventure", "Train", "Full",
                100, 2000, 5, 14, 3.5, 5.0
        );

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Alps", result.getContent().get(0).getName());
    }

    @Test
    void getTours_shouldFallbackOnException() throws Exception {
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new RuntimeException("Connection error"));

        PageResponse<TourDTO> result = service.getTours(
                0, 10, null, null, null, null,
                null, null, null, null, null, null
        );

        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void getTourTypes_shouldReturnList() throws Exception {
        String json = "[\"Adventure\", \"Leisure\"]";

        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn(json);
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(response);

        List<String> types = service.getTourTypes();

        assertEquals(2, types.size());
        assertTrue(types.contains("Adventure"));
    }

    @Test
    void getTransportNames_shouldReturnParsedNames() throws Exception {
        String json = """
        [
          {"id":1,"name":"Bus"},
          {"id":2,"name":"Plane"}
        ]
        """;

        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn(json);
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(response);

        List<String> names = service.getTransportNames();

        assertEquals(2, names.size());
        assertTrue(names.contains("Bus"));
        assertTrue(names.contains("Plane"));
    }

    @Test
    void getMealOptions_shouldReturnValues() throws Exception {
        String json = "[\"Breakfast\", \"All Inclusive\"]";

        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn(json);
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(response);

        List<String> result = service.getMealOptions();

        assertEquals(2, result.size());
        assertTrue(result.contains("Breakfast"));
    }

    @Test
    void getTourTypes_shouldHandleFailure() throws Exception {
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new RuntimeException("fail"));

        List<String> types = service.getTourTypes();
        assertTrue(types.isEmpty());
    }

    @Test
    void getTransportNames_shouldHandleFailure() throws Exception {
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new RuntimeException("fail"));

        List<String> names = service.getTransportNames();
        assertTrue(names.isEmpty());
    }

    @Test
    void getMealOptions_shouldHandleFailure() throws Exception {
        when(client.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new RuntimeException("fail"));

        List<String> meals = service.getMealOptions();
        assertTrue(meals.isEmpty());
    }
}
