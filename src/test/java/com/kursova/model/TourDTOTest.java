package com.kursova.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TourDTOTest {

    @Test
    void allArgsConstructorShouldSetAllFieldsCorrectly() {
        TourDTO dto = new TourDTO(
                1,
                "Tour Name",
                "Leisure",
                10,
                "Full Board",
                7,
                1500,
                4.2,
                "Plane",
                true
        );

        assertEquals(1, dto.getId());
        assertEquals("Tour Name", dto.getName());
        assertEquals("Leisure", dto.getType());
        assertEquals(10, dto.getTransportId());
        assertEquals("Full Board", dto.getMealOption());
        assertEquals(7, dto.getNumberOfDays());
        assertEquals(1500, dto.getPrice());
        assertEquals(4.2, dto.getRating());
        assertEquals("Plane", dto.getTransportName());
        assertTrue(dto.getIsFavorite());
    }

    @Test
    void jacksonShouldDeserializeTourDTO() throws Exception {
        String json = """
        {
            "id": 1,
            "name": "Test Tour",
            "type": "Adventure",
            "transportId": 2,
            "mealOption": "Breakfast",
            "numberOfDays": 5,
            "price": 1000,
            "rating": 4.5,
            "transportName": "Bus",
            "isFavorite": true
        }
    """;

        ObjectMapper mapper = new ObjectMapper();
        TourDTO dto = mapper.readValue(json, TourDTO.class);

        assertEquals(1, dto.getId());
        assertEquals("Test Tour", dto.getName());
        assertEquals("Adventure", dto.getType());
        assertEquals(2, dto.getTransportId());
        assertEquals("Breakfast", dto.getMealOption());
        assertEquals(5, dto.getNumberOfDays());
        assertEquals(1000, dto.getPrice());
        assertEquals(4.5, dto.getRating());
        assertEquals("Bus", dto.getTransportName());
        assertTrue(dto.getIsFavorite());
    }
}
