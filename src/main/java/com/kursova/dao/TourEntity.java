package com.kursova.dao;

public class TourEntity {
    private final int id;
    private String name;
    private String type;
    private TransportEntity transport;
    private String mealOption;
    private int numberOfDays;
    private int price;
    private double rating;

    public TourEntity(int id, String name, String type, TransportEntity transport, String mealOption, int numberOfDays, int price, double rating) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.transport = transport;
        this.mealOption = mealOption;
        this.numberOfDays = numberOfDays;
        this.price = price;
        this.rating = rating;
    }

    public TourEntity(String name, String type, TransportEntity transport, String mealOption, int numberOfDays, int price, double rating) {
        this(0, name, type, transport, mealOption, numberOfDays, price, rating);
    }

    public int getId() {return id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}

    public TransportEntity getTransport() {return transport;}

    public void setTransport(TransportEntity transport) {this.transport = transport;}

    public int getTransportId() {return transport.getId();}

    public String getMealOption() {return mealOption;}

    public void setMealOption(String mealOption) {this.mealOption = mealOption;}

    public int getNumberOfDays() {return numberOfDays;}

    public void setNumberOfDays(int numberOfDays) {this.numberOfDays = numberOfDays;}

    public int getPrice() {return price;}

    public void setPrice(int price) {this.price = price;}

    public double getRating() {return rating;}

    public void setRating(double rating) {this.rating = rating;}
}
