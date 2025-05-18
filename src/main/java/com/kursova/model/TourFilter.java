package com.kursova.model;

import lombok.Data;

@Data
public class TourFilter {
    private String name;
    private String type;
    private String transportName;
    private String mealOption;
    private Integer minDays;
    private Integer maxDays;
    private Integer minPrice;
    private Integer maxPrice;
    private Double minRating;
    private Double maxRating;
}
