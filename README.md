# SWIFT Codes API

## Project Overview

This project provides a RESTful API for managing and retrieving SWIFT codes stored in a database. SWIFT codes are unique identifiers for banks and their branches. The application parses SWIFT data from a file, stores it efficiently, and exposes endpoints to retrieve or manipulate the stored information.

## Features

- Parse SWIFT code data from a file and identify headquarters and branches.
- Store parsed SWIFT data in a database for fast querying.
- Expose a RESTful API to:
  - Retrieve SWIFT code details.
  - Fetch all SWIFT codes for a given country.
  - Add new SWIFT code entries.
  - Delete existing SWIFT code entries.
- Handle errors gracefully with meaningful messages.
- Unit and integration tests for reliability.
- Containerized setup using Docker.

## Technologies Used

- Programming Language: Java
- Database: PostgreSQL
- Frameworks: Spring Boot
- Containerization: Docker, Docker Compose

## Installation & Setup

### Prerequisites

Ensure you have the following installed:
- Docker & Docker Compose 
- Java

### Clone Repository

```bash
git https://github.com/TommyFurgi/SWIFT-Codes-API.git
cd SWIFT-Codes-API
```

### Build the project

```bash
mvn clean package
```

### Run the Application

```bash
docker-compose up --build
```
The API will be accessible at `http://localhost:8080`

## API Endpoints

1. Retrieve SWIFT Code Details

GET `/v1/swift-codes/{swift-code}`
  
Response:
```json
{
  "address": "123 Bank St, City, Country",
  "bankName": "Example Bank",
  "countryISO2": "US",
  "countryName": "United States",
  "isHeadquarter": true,
  "swiftCode": "EXAMPUSXXXX",
  "branches": [
    {
      "address": "456 Branch St, City, Country",
      "bankName": "Example Bank",
      "countryISO2": "US",
      "isHeadquarter": false,
      "swiftCode": "EXAMPUSX456"
    }
  ]
}
```

2. Retrieve SWIFT Codes for a Country

GET `/v1/swift-codes/country/{countryISO2code}`

Response:
```json
{
  "countryISO2": "US",
  "countryName": "United States",
  "swiftCodes": [
    {
      "address": "123 Bank St, City, Country",
      "bankName": "Example Bank",
      "countryISO2": "US",
      "isHeadquarter": true,
      "swiftCode": "EXAMPUSXXXX"
    },
    {
      "address": "456 Branch St, City, Country",
      "bankName": "Example Bank",
      "countryISO2": "US",
      "isHeadquarter": false,
      "swiftCode": "EXAMPUSX456"
    }
  ]
}
```

3. Add a New SWIFT Code

POST `/v1/swift-codes`

Request:
```json
{
  "address": "789 New Branch, City, Country",
  "bankName": "Example Bank",
  "countryISO2": "US",
  "countryName": "United States",
  "isHeadquarter": false,
  "swiftCode": "EXAMPUSX789"
}
```

Response:
```json
{
  "message": "SWIFT code added successfully."
}
```

4. Delete a SWIFT Code

DELETE `/v1/swift-codes/{swift-code}`

Response:
```json
{
  "message": "SWIFT code deleted successfully."
}
```