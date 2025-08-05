package dev.kadcom.dummyjson.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String maidenName;
    private int age;
    private String gender;
    private String email;
    private String phone;
    private String username;
    private String password;
    private String birthDate;
    private String image;
    private String bloodGroup;
    private double height;
    private double weight;
    private String eyeColor;
    private Hair hair;
    private String ip;
    private Address address;
    private String macAddress;
    private String university;
    private Bank bank;
    private Company company;
    private String ein;
    private String ssn;
    private String userAgent;
    private Crypto crypto;
    private String role;
    
    // Getters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Optional<String> getMaidenName() { return Optional.ofNullable(maidenName); }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getBirthDate() { return birthDate; }
    public Optional<String> getImage() { return Optional.ofNullable(image); }
    public Optional<String> getBloodGroup() { return Optional.ofNullable(bloodGroup); }
    public double getHeight() { return height; }
    public double getWeight() { return weight; }
    public Optional<String> getEyeColor() { return Optional.ofNullable(eyeColor); }
    public Optional<Hair> getHair() { return Optional.ofNullable(hair); }
    public Optional<String> getIp() { return Optional.ofNullable(ip); }
    public Optional<Address> getAddress() { return Optional.ofNullable(address); }
    public Optional<String> getMacAddress() { return Optional.ofNullable(macAddress); }
    public Optional<String> getUniversity() { return Optional.ofNullable(university); }
    public Optional<Bank> getBank() { return Optional.ofNullable(bank); }
    public Optional<Company> getCompany() { return Optional.ofNullable(company); }
    public Optional<String> getEin() { return Optional.ofNullable(ein); }
    public Optional<String> getSsn() { return Optional.ofNullable(ssn); }
    public Optional<String> getUserAgent() { return Optional.ofNullable(userAgent); }
    public Optional<Crypto> getCrypto() { return Optional.ofNullable(crypto); }
    public Optional<String> getRole() { return Optional.ofNullable(role); }
    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public Optional<LocalDate> getParsedBirthDate() {
        try {
            return Optional.of(LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-M-d")));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public double getBMI() {
        double heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}