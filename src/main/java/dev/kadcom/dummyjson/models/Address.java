package dev.kadcom.dummyjson.models;

import java.util.Optional;

public class Address {
    private String address;
    private String city;
    private String state;
    private String stateCode;
    private String postalCode;
    private Coordinates coordinates;
    private String country;
    
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getStateCode() { return stateCode; }
    public String getPostalCode() { return postalCode; }
    public Optional<Coordinates> getCoordinates() { return Optional.ofNullable(coordinates); }
    public Optional<String> getCountry() { return Optional.ofNullable(country); }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null) sb.append(address);
        if (city != null) sb.append(", ").append(city);
        if (state != null) sb.append(", ").append(state);
        if (postalCode != null) sb.append(" ").append(postalCode);
        if (country != null) sb.append(", ").append(country);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return "Address{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}