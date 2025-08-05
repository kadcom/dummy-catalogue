package dev.kadcom.dummyjson.models;

public class Dimensions {
    private double width;
    private double height;
    private double depth;
    
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getDepth() { return depth; }
    
    public double getVolume() {
        return width * height * depth;
    }
    
    @Override
    public String toString() {
        return "Dimensions{" +
                "width=" + width +
                ", height=" + height +
                ", depth=" + depth +
                '}';
    }
}