package com.kursova.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransportDTOTest {

    @Test
    void testAllArgsConstructor() {
        TransportDTO transport = new TransportDTO(1, "Bus");

        assertEquals(1, transport.getId());
        assertEquals("Bus", transport.getName());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        TransportDTO transport = new TransportDTO();
        transport.setId(42);
        transport.setName("Train");

        assertEquals(42, transport.getId());
        assertEquals("Train", transport.getName());
    }

    @Test
    void testIdFieldAlone() {
        TransportDTO transport = new TransportDTO();
        transport.setId(99);

        assertEquals(99, transport.getId());
    }

    @Test
    void testToStringNotNull() {
        TransportDTO transport = new TransportDTO(5, "Plane");
        assertNotNull(transport.toString()); // optional for coverage tools
    }
}
