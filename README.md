# Carbon Consumption Tracker

## Overview
The **Carbon Consumption Tracker** is a Java-based console application that allows users to manage their profiles, track carbon consumption, and generate detailed consumption reports. The application helps users analyze their habits by tracking daily, weekly, and monthly carbon consumption, providing insights into their environmental impact.

## Features

### User Management:
- Create, update, delete, and retrieve user profiles.
- Each user has a unique ID, name, and age.

### Carbon Consumption Tracking:
- Add carbon consumption records for users, specifying the start and end dates of each consumption period.
- Track multiple carbon consumption records for each user.

### Consumption Report Generation:
- Generate daily, weekly, or monthly consumption reports.
- Report consumption based on the inserted data, even when the dates are irregular or separated.

### Advanced Features:
- **Inheritance and Polymorphism**:
    - Implement sub-classes: `Transport`, `Logement`, and `Alimentation` that inherit from the base class `Consommation`.
    - An abstract method `calculerImpact()` in the base class `Consommation` is implemented differently in each subclass for calculating the environmental impact:
        - `Transport` (attributes: `distanceParcourue`, `typeDeVehicule`): Car has an impact of `0.5`, and train `0.1`.
        - `Logement` (attributes: `consommationEnergie`, `typeEnergie`): Electricity has an impact of `1.5`, and gas `2.0`.
        - `Alimentation` (attributes: `typeAliment`, `poids`): Meat has an impact of `5.0`, and vegetables `0.5`.

- **Design Patterns**:
    - **Singleton**: Manages the database connection, ensuring only one instance of the connection is active at any given time.
    - **Repository Pattern**: Manages database operations for users and carbon consumption records, providing a clear separation between the application and the database layer.

### Data Persistence:
- **Database Integration**:
    - Uses JDBC to store and retrieve user and consumption data in a relational database.
    - A PostgreSQL database is used, running in a Docker container to ensure easy setup and consistent environment management.
    - A Singleton pattern is used for the database connection to manage resources efficiently.

- **Transaction Management**:
    - Ensures data integrity using JDBC transactions with `commit()` and `rollback()` to maintain consistency during record insertion or updates.

- **Average Consumption Calculation**:
    - Calculates the average carbon consumption for a user over a given period.

- **Filtering & Sorting**:
    - Filter users who have exceeded a threshold of `3000 KgCO2eq`.
    - Sort users based on their total carbon consumption.

- **Inactive User Detection**:
    - Identifies users who haven’t logged carbon consumption over a specified time period.

## Project Structure

### Classes Overview

- **User.java**:
    - Represents a user in the system.
    - Attributes: `userId`, `name`, `age`, and a list of consumption records.
    - Provides methods to manage consumption records.

- **Consommation.java**:
    - Base class for carbon consumption records.
    - Attributes: `startDate`, `endDate`, `carbonConsumed`.

- **Transport.java, Logement.java, Alimentation.java**:
    - Inherited from `Consommation`.
    - Contains specific attributes like `distanceParcourue`, `typeDeVehicule` for `Transport`; `consommationEnergie`, `typeEnergie` for `Logement`; and `typeAliment`, `poids` for `Alimentation`.

- **UserService.java**:
    - Core service class managing users and carbon consumption records.
    - Provides methods to create, update, delete users, and add carbon consumption records.
    - Generates daily, weekly, and monthly reports based on consumption data.

- **DatabaseConnection.java**:
    - Singleton class to manage the connection to the PostgreSQL database running in Docker.

- **Main.java**:
    - Entry point of the program.
    - Provides a console-based menu for interacting with the application.

## Database Setup with Docker

This project uses a **PostgreSQL** database hosted in a **Docker** container for data persistence.

1. Install [Docker](https://docs.docker.com/get-docker/).
2. Run the following command to pull and start a PostgreSQL container:
   ```bash
   docker run --name carbon-tracker-db -e POSTGRES_USER=yourUsername -e POSTGRES_PASSWORD=yourPassword -e POSTGRES_DB=carbon_tracker -p 5432:5432 -d postgres
