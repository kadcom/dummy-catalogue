package dev.kadcom.dummyjson.models;

public class Hair {
    private String color;
    private String type;
    
    public String getColor() { return color; }
    public String getType() { return type; }
    
    @Override
    public String toString() {
        return "Hair{" +
                "color='" + color + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}