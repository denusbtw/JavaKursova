package com.kursova.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PageResponseTest {

    @Test
    void allArgsConstructorShouldSetAllFieldsCorrectly() {
        TourDTO dto = new TourDTO(
                1,
                "Name",
                "Type",
                2,
                "Breakfast",
                7,
                1000,
                4.5,
                "Bus",
                true
        );

        PageResponse<TourDTO> page = new PageResponse<>(
                List.of(dto),
                3,
                30,
                0,
                10
        );

        assertEquals(3, page.getTotalPages());
        assertEquals(30, page.getTotalElements());
        assertEquals(0, page.getNumber());
        assertEquals(10, page.getSize());

        assertNotNull(page.getContent());
        assertEquals(1, page.getContent().size());
        assertEquals("Name", page.getContent().get(0).getName());
    }

    @Test
    void jacksonShouldDeserializePageResponseOfTourDTO() throws Exception {
        String json = """
            {
              "content": [
                {
                  "id": 1,
                  "name": "Test Tour",
                  "type": "Adventure",
                  "transportId": 2,
                  "mealOption": "Breakfast",
                  "numberOfDays": 7,
                  "price": 1500,
                  "rating": 4.3,
                  "transportName": "Bus",
                  "isFavorite": true
                }
              ],
              "totalPages": 5,
              "totalElements": 50,
              "number": 0,
              "size": 10
            }
        """;

        ObjectMapper mapper = new ObjectMapper();
        PageResponse<TourDTO> response = mapper.readValue(json, new TypeReference<>() {});

        assertNotNull(response);
        assertEquals(5, response.getTotalPages());
        assertEquals(50, response.getTotalElements());
        assertEquals(0, response.getNumber());
        assertEquals(10, response.getSize());

        List<TourDTO> content = response.getContent();
        assertNotNull(content);
        assertEquals(1, content.size());

        TourDTO tour = content.get(0);
        assertEquals("Test Tour", tour.getName());
        assertEquals("Adventure", tour.getType());
        assertEquals("Bus", tour.getTransportName());
        assertEquals(7, tour.getNumberOfDays());
        assertEquals(1500, tour.getPrice());
        assertEquals(4.3, tour.getRating());
        assertTrue(tour.getIsFavorite());
    }
}
