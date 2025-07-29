# Trip Planner API – Mock Backend Exam (Fall 2024)

This project is a backend application for managing guided trips. It is built using **Java**, **Javalin**, **Hibernate (JPA)**, and **PostgreSQL**. The system allows for creation, modification, assignment, and retrieval of trips and guides.
## Task 1: Setup
- `README.md` is used to document all progress and answers.
  
## Task 2: JPA and DAOs

- Entities:
  - `Trip` with fields: `id`, `name`, `price`, `startPosition`, `startTime`, `endTime`, `category`, `guide`
  - `Guide` with fields: `id`, `firstname`, `lastname`, `email`, `phone`, `yearsOfExperience`
- Enum `TripCategory` includes: `BEACH`, `CITY`, `FOREST`, `LAKE`, `SEA`, `SNOW`
- DTOs:
  - `TripDTO` with flat data and optional `guideName`
  - `GuideDTO` used internally
- DAO interfaces:
  - Generic `IDAO<T, ID>` for CRUD operations
  - `TripDAO` implements `IDAO<TripDTO, Integer>` and `ITripGuideDAO` with:
    - `addGuideToTrip(int tripId, int guideId)`
    - `getTripsByGuide(int guideId)`
- DAOs use `TripMapper` and `GuideMapper` to convert between DTOs and entities

---

## Task 3: REST API with Javalin

### Routing Setup

- REST endpoints registered using `EndpointGroup` in a modular `TripRoute` class.
- All endpoints are under `/api/trips`

### Controller Logic

- `TripController` implements `IController<TripDTO, Integer>`
- Validates:
  - Required fields (`name`, `category`)
  - Non-negative `price`
  - Valid path params

### Routes Implemented:

| Method | Route | Description |
|--------|-------|-------------|
| POST   | /trips/populate | Populates DB with sample data |
| GET    | /trips | Get all trips |
| GET    | /trips/{id} | Get trip by ID |
| POST   | /trips | Create a new trip |
| PUT    | /trips/{id} | Update a trip |
| DELETE | /trips/{id} | Delete a trip |
| PUT    | /trips/{tripId}/guides/{guideId} | Assign a guide to a trip |

---

## Task 3.3.2 – `dev.http` Testing File

- A `dev.http` file was created with HTTP requests to test all endpoints.
- The following were tested:
  - GET all trips
  - GET trip by ID
  - POST create trip
  - PUT update trip
  - DELETE trip
  - PUT add guide to trip
  - POST populate database

---

## Task 3.3.3 – Example Output from `dev.http`

### POST /trips
```json
{
  "id": 5,
  "name": "Volcano Trek",
  "price": 700.0,
  "startPosition": "15.1234,-90.1234",
  "startTime": "2025-06-15T09:00:00",
  "endTime": "2025-06-20T17:00:00",
  "category": "FOREST",
  "guideId": 1,
  "guideName": "Lars Sten"
}
```

### GET /trips/1
```json
{
  "id": 1,
  "name": "Beach Paradise",
  "price": 499.99,
  "startPosition": "55.6761,12.5683",
  "startTime": "2025-05-01T10:00:00",
  "endTime": "2025-05-02T18:00:00",
  "category": "BEACH",
  "guideId": 1,
  "guideName": "Lars Sten"
}
```

### PUT /trips/1
`204 No Content`

### DELETE /trips/3
`204 No Content`

---

## Task 3.3.4 – Trip includes guide information

- `TripDTO` was extended with a `guideName` field
- `TripMapper.toDTO` includes logic to add `guideName` from the linked `Guide`
- Verified through `GET /trips/{id}` endpoint

---

## Task 3.3.5 – Theoretical Answer

> **Why use PUT to add a guide to a trip instead of POST?**

Using `PUT` indicates that we are **updating an existing resource** — in this case, assigning a guide to a pre-existing trip.  
- PUT is **idempotent**: calling it multiple times with the same guide/trip ID results in the same state.
- POST is generally used to **create new resources** (e.g., a new trip), not update relationships.
---

## Task 4: REST Error Handling

### JSON error responses
- When trying to get or delete a trip by ID and it doesn’t exist, a structured JSON error is returned.
- This is handled using `ApiRuntimeException`.

**Examples:**
```json
{
  "warning": "Trip with ID 9999 not found for deletion",
  "status": "404 Not Found"
}
```

---

## Task 5: Streams and Queries

### 5.1 Filter trips by category
```http
GET /api/trips/category/{category}
```
Filters trips using either JPA or Java streams based on the `TripCategory` enum.

### 5.2 Total price per guide
```http
GET /api/trips/guides/totalprice
```
Returns the total price of all trips per guide using stream aggregation.

**Sample output:**
```json
[
  {
    "guideId": 1,
    "guideName": "Lars Sten",
    "totalPrice": 1199.99
  },
  {
    "guideId": 2,
    "guideName": "Bobby San",
    "totalPrice": 899.99
  }
]
```

---

## Task 6: External API Integration

### 6.1 Packing items from external API

API used:
```
GET https://packingapi.cphbusinessapps.dk/packinglist/{category}
```

Items returned include:
- name, description
- weightInGrams, quantity
- buyingOptions (shopName, price, url)

### DTOs for external API integration

#### `PackingItemDTO`
```java
public class PackingItemDTO {
    private String name;
    private int weightInGrams;
    private int quantity;
    private String description;
    private String category;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<BuyingOptionDTO> buyingOptions;
}
```

#### `BuyingOptionDTO`
```java
public class BuyingOptionDTO {
    private String shopName;
    private String shopUrl;
    private double price;
}
```
These are used to map the external packing list items returned by the packing API.

### Jackson configuration for ZonedDateTime

The ObjectMapper is configured like this to handle ZonedDateTime formats:
```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerModule(new JavaTimeModule());
objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
```

### PackingService logic

The `PackingService.getPackingItems(TripCategory category)` method:
- Sends a `GET` request to the external API using the category
- Parses the result into a list of `PackingItemDTO`
- Throws an `ApiException` if the request fails

Used in:
- `TripController.getTripById(...)`
- `TripController.getPackingList(...)`
- `TripController.getTotalPackingWeight(...)`

### 6.2 Endpoint: Packing list by trip ID
```http
GET /api/trips/packinglist/{tripId}
```
Uses the trip’s category to fetch external packing items.

### 6.3 Packing items included in trip response
```http
GET /api/trips/{id}
```
Response includes:
```json
{
  "trip": { ... },
  "packingItems": [ ... ]
}
```

### 6.4 Total packing weight endpoint
```http
GET /api/trips/packinglist/{tripId}/weight
```
Returns the total weight:
```json
{
  "tripId": 1,
  "category": "FOREST",
  "totalWeightInGrams": 5240
}
```

---
## Task 7: Testing REST Endpoints (15%)

### 7.1 Test Class Created
A dedicated `TripRouteTest` class was created using **JUnit 5**, **RestAssured**, and **Hamcrest** to test all REST endpoints related to trips.

### 7.2 Server Setup
`@BeforeAll` initializes:
- The Javalin server
- The test database via `HibernateConfig.setTest(true)`
- The test data using `Populator.populate(...)`
- `RestAssured` base URI pointing to the test server

### 7.3 Data Setup
`@BeforeEach` ensures the database is repopulated before each test, guaranteeing consistent test state.

### 7.4 Endpoint Tests
Each of the following endpoints is covered:

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | /trips | Tests fetching all trips |
| GET    | /trips/{id} | Tests fetching a specific trip and includes packing items |
| POST   | /trips | Tests creating a new trip |
| PUT    | /trips/{id} | Tests updating a trip |
| DELETE | /trips/{id} | Tests deleting a trip |
| PUT    | /trips/{tripId}/guides/{guideId} | Tests assigning a guide |
| GET    | /trips/packinglist/{tripId} | Tests getting packing items |
| GET    | /trips/packinglist/{tripId}/weight | Tests getting total weight of packing items |

All assertions include status code and body field validation using Hamcrest matchers.

### 7.5 Trip by ID includes Packing Items

Verified in the test for `GET /trips/{id}`:

```json
{
  "trip": {
    "id": 1,
    "name": "Beach Paradise",
    "...": "..."
  },
  "packingItems": [
    {
      "name": "Sunscreen",
      "weightInGrams": 200,
      "quantity": 1
    },
    {
      "name": "Beach Towel",
      "weightInGrams": 500,
      "quantity": 1
    }
  ]
}
```
This confirms the packing items from the external API are integrated in the trip detail response.

---
