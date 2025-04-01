package dat.routes;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.config.Populator;
import dat.entities.enums.TripCategory;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TripRouteTest {

    private Javalin app;
    private EntityManagerFactory emf;

    @BeforeAll
    void setup() {
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        app = ApplicationConfig.startServer(7070);
        RestAssured.baseURI = "http://localhost:7070/api";
    }

    @BeforeEach
    void populateTestData() {
        Populator.populate(emf);
    }

    @AfterAll
    void tearDown() {
        ApplicationConfig.stopServer(app);
    }

    @Test
    @Order(1)
    void testGetAllTrips() {
        given()
                .when()
                .get("/trips")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(2)
    void testGetTripById() {
        given()
                .pathParam("id", 1)
                .when()
                .get("/trips/{id}")
                .then()
                .statusCode(200)
                .body("trip.id", equalTo(1))
                .body("packingItems", notNullValue())
                .body("packingItems.size()", greaterThan(0))
                .body("packingItems[0].name", not(isEmptyOrNullString()))
                .body("packingItems[0].weightInGrams", greaterThan(0));
    }

    @Test
    @Order(3)
    void testCreateTrip() {
        String json = """
            {
              "name": "Test Trip",
              "price": 199.99,
              "startPosition": "56.7,12.3",
              "startTime": "2025-07-01T10:00:00",
              "endTime": "2025-07-05T16:00:00",
              "category": "BEACH",
              "guideId": 1
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/trips")
                .then()
                .statusCode(201)
                .body("name", equalTo("Test Trip"));
    }

    @Test
    @Order(4)
    void testUpdateTrip() {
        String json = """
            {
              "id": 1,
              "name": "Updated Trip",
              "price": 500.0,
              "startPosition": "56.7,12.3",
              "startTime": "2025-07-01T10:00:00",
              "endTime": "2025-07-05T16:00:00",
              "category": "CITY",
              "guideId": 1
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(json)
                .pathParam("id", 1)
                .when()
                .put("/trips/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Trip"));
    }

    @Test
    @Order(5)
    void testDeleteTrip() {
        given()
                .pathParam("id", 2)
                .when()
                .delete("/trips/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    void testAddGuideToTrip() {
        given()
                .pathParam("tripId", 1)
                .pathParam("guideId", 1)
                .when()
                .put("/trips/{tripId}/guides/{guideId}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(7)
    void testGetPackingListEndpoint() {
        given()
                .pathParam("tripId", 1)
                .when()
                .get("/trips/packinglist/{tripId}")
                .then()
                .statusCode(200)
                .body("tripId", equalTo(1))
                .body("packingItems", notNullValue());
    }

    @Test
    @Order(8)
    void testGetTotalWeightEndpoint() {
        given()
                .pathParam("tripId", 1)
                .when()
                .get("/trips/packinglist/{tripId}/weight")
                .then()
                .statusCode(200)
                .body("totalWeightInGrams", greaterThan(0));
    }
}
