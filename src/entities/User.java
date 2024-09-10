package entities;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;  // Changed from Id to id for consistency
    private String name;
    private int age;
    private List<CarbonRecord> carbonRecords;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
        this.carbonRecords = new ArrayList<>();  // Initialize the list
    }

    public User(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.carbonRecords = new ArrayList<>();  // Initialize the list
    }

    public User(int id, String name, int age, List<CarbonRecord> carbonRecords) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.carbonRecords = carbonRecords != null ? carbonRecords : new ArrayList<>();  // Ensure the list is not null
    }

    public User() {
        this.carbonRecords = new ArrayList<>();  // Initialize the list
    }

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
        return "User [id=" + id + ", name=" + name + "]";
    }

    public void addCarbonRecord(CarbonRecord record) {
        this.carbonRecords.add(record);
    }


}
