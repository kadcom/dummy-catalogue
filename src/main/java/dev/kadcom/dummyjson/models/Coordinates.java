package dev.kadcom.dummyjson.models;

public class Coordinates {
    private double lat;
    private double lng;
    
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    
    @Override
    public String toString() {
        return "Coordinates{" +
                "lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}