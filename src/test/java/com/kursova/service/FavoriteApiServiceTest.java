package com.kursova.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kursova.model.PageResponse;
import com.kursova.model.TourDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FavoriteApiServiceTest {

    private HttpClient mockClient;
    private ObjectMapper mapper;
    private FavoriteApiService service;

    @BeforeEach
    void setUp() {
        mockClient = mock(HttpClient.class);
        mapper = new ObjectMapper();
        service = new FavoriteApiService(mockClient, mapper);
    }

    @Test
    void getFavorites_shouldReturnCorrectResult() throws Exception {
        // Given
        String json = """
        {
          "content": [{"id": 1, "name": "Sample Tour"}],
          "number": 0,
          "totalPages": 1
        }
        """;

        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn(json);
        when(mockClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(response);

        // When
        PageResponse<TourDTO> result = service.getFavorites(
                null, null, null, null, null, null, null,
                null, null, null, 0, 5);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Sample Tour", result.getContent().get(0).getName());
    }

    @Test
    void getFavorites_shouldHandleException() throws Exception {
        when(mockClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new RuntimeException("Network fail"));

        PageResponse<TourDTO> result = service.getFavorites(
                null, null, null, null, null, null, null,
                null, null, null, 0, 5);

        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void addToFavorites_shouldSendRequest() throws Exception {
        HttpResponse<Void> response = mock(HttpResponse.class);
        when(mockClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
                .thenReturn(response);

        service.addToFavorites(99);

        verify(mockClient, times(1))
                .send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding()));
    }

    @Test
    void removeFromFavorites_shouldSendDeleteRequest() throws Exception {
        HttpResponse<Void> response = mock(HttpResponse.class);
        when(mockClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
                .thenReturn(response);

        service.removeFromFavorites(42);

        verify(mockClient, times(1))
                .send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding()));
    }

    @Test
    void addToFavorites_shouldNotThrowOnFailure() throws Exception {
        when(mockClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
                .thenThrow(new RuntimeException("fail"));

        assertDoesNotThrow(() -> service.addToFavorites(1));
    }

    @Test
    void removeFromFavorites_shouldNotThrowOnFailure() throws Exception {
        when(mockClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.discarding())))
                .thenThrow(new RuntimeException("fail"));

        assertDoesNotThrow(() -> service.removeFromFavorites(1));
    }

    @Test
    void getFavorites_shouldAppendAllQueryParameters() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn("""
        {"content": [], "number": 0, "totalPages": 1}
        """);

        final HttpRequest[] capturedRequest = new HttpRequest[1];
        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString()))).thenAnswer(invocation -> {
            capturedRequest[0] = invocation.getArgument(0);
            return response;
        });

        service.getFavorites(
                "Paris",            // name
                "Adventure",        // type
                "Breakfast",        // mealOption
                3, 10,              // minDays, maxDays
                500, 1500,          // minPrice, maxPrice
                3.5, 5.0,           // minRating, maxRating
                "Bus",              // transport
                0, 20               // page, size
        );

        String uri = capturedRequest[0].uri().toString();

        assertTrue(uri.contains("name=Paris"));
        assertTrue(uri.contains("type=Adventure"));
        assertTrue(uri.contains("mealOption=Breakfast"));
        assertTrue(uri.contains("transportName=Bus"));
        assertTrue(uri.contains("minDays=3"));
        assertTrue(uri.contains("maxDays=10"));
        assertTrue(uri.contains("minPrice=500"));
        assertTrue(uri.contains("maxPrice=1500"));
        assertTrue(uri.contains("minRating=3.5"));
        assertTrue(uri.contains("maxRating=5.0"));
        assertTrue(uri.contains("page=0"));
        assertTrue(uri.contains("size=20"));
    }

    @Test
    void getFavorites_shouldNotIncludeNullParameters() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.body()).thenReturn("""
        {"content": [], "number": 0, "totalPages": 1}
        """);

        final HttpRequest[] captured = new HttpRequest[1];
        when(mockClient.send(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenAnswer(invocation -> {
                    captured[0] = invocation.getArgument(0);
                    return response;
                });

        service.getFavorites(
                "  ", null, null, null, null,
                null, null, null, null, null, 1, 10
        );

        String uri = captured[0].uri().toString();

        assertTrue(uri.contains("page=1"));
        assertTrue(uri.contains("size=10"));
        assertFalse(uri.contains("name="));
        assertFalse(uri.contains("type="));
        assertFalse(uri.contains("mealOption="));
        assertFalse(uri.contains("transportName="));
        assertFalse(uri.contains("minDays="));
        assertFalse(uri.contains("maxRating="));
    }

}
