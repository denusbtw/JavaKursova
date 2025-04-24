package com.kursova.entity;

public class TransportEntity {
    private final int id;
    private String name;

    public TransportEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public TransportEntity(String name) {
        this(0, name);
    }

    public int getId() {return id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}
}
