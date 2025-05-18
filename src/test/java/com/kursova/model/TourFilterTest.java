package com.kursova.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TourFilterTest {

    @Test
    void testGettersAndSetters() {
        TourFilter filter = new TourFilter();

        filter.setName("test");
        filter.setType("relax");
        filter.setTransportName("bus");
        filter.setMealOption("breakfast");
        filter.setMinDays(3);
        filter.setMaxDays(10);
        filter.setMinPrice(500);
        filter.setMaxPrice(1500);
        filter.setMinRating(3.5);
        filter.setMaxRating(5.0);

        assertEquals("test", filter.getName());
        assertEquals("relax", filter.getType());
        assertEquals("bus", filter.getTransportName());
        assertEquals("breakfast", filter.getMealOption());
        assertEquals(3, filter.getMinDays());
        assertEquals(10, filter.getMaxDays());
        assertEquals(500, filter.getMinPrice());
        assertEquals(1500, filter.getMaxPrice());
        assertEquals(3.5, filter.getMinRating());
        assertEquals(5.0, filter.getMaxRating());
    }

    @Test
    void testEqualsAndHashCode() {
        TourFilter f1 = new TourFilter();
        TourFilter f2 = new TourFilter();

        f1.setName("x");
        f2.setName("x");

        assertEquals(f1, f2);
        assertEquals(f1.hashCode(), f2.hashCode());
    }

    @Test
    void testToStringNotNull() {
        TourFilter filter = new TourFilter();
        assertNotNull(filter.toString());
    }
}
