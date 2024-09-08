package entities;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;  // Changed from Id to id for consistency
    private String name;
    private int age;
    private List<CarbonRecord> carbonRecords;

    // Constructor for creating a new user without carbon records
    public User(String name, int age) {
        this.name = name;
        this.age = age;
        this.carbonRecords = new ArrayList<>();  // Initialize the list
    }

    // Constructor for loading from the database with an empty carbon records list
    public User(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.carbonRecords = new ArrayList<>();  // Initialize the list
    }

    // Constructor for loading from the database with an existing carbon records list
    public User(int id, String name, int age, List<CarbonRecord> carbonRecords) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.carbonRecords = carbonRecords != null ? carbonRecords : new ArrayList<>();  // Ensure the list is not null
    }

    // Default constructor for deserialization (optional)
    public User() {
        this.carbonRecords = new ArrayList<>();  // Initialize the list
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<CarbonRecord> getCarbonRecords() {
        return carbonRecords;
    }

    public void setCarbonRecords(List<CarbonRecord> carbonRecords) {
        this.carbonRecords = carbonRecords != null ? carbonRecords : new ArrayList<>();
    }

    @Override
    public String toString() {
        return String.format("User [id=%d, name=%s, age=%d, carbonRecords=%s]", id, name, age, carbonRecords);
    }
}
