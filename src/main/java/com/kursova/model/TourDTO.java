package com.kursova.model;


import lombok.Data;

@Data
public class TourDTO {
    private int id;
    private String name;
    private String type;
    private int transportId;
    private String mealOption;
    private int numberOfDays;
    private int price;
    private double rating;
    private String transportName;
    private Boolean isFavorite;
}
