package com.example.kitesurfingapp;

import java.io.Serializable;

public class KitePlace implements Serializable {

    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private double windProbability;
    private String country;
    private String whenToGo;
    private boolean isFavorite;

    public KitePlace(String id, String name, String country, String whenToGo, boolean isFavorite) {

        this.id = id;
        this.name = name;
        this.country = country;
        this.whenToGo = whenToGo;
        this.isFavorite = isFavorite;
    }

    public KitePlace(String id, String name, double latitude, double longitude, double windProbability,
                     String country, String whenToGo, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.windProbability = windProbability;
        this.country = country;
        this.whenToGo = whenToGo;
        this.isFavorite = isFavorite;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getWhenToGo() {
        return whenToGo;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public double getWindProbability() { return windProbability; }

    public void setFavourite(boolean favourite) {
        isFavorite = favourite;
    }
}
