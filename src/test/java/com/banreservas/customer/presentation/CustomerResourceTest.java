package com.banreservas.customer.presentation;

import com.banreservas.customer.domain.port.CountryGateway;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestSecurity(user = "testUser", roles = {"user"})
class CustomerResourceTest {

    @InjectMock
    CountryGateway countryGateway;

    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    void cleanDatabase() {
        entityManager.createQuery("DELETE FROM CustomerJpaEntity").executeUpdate();
        when(countryGateway.getDemonym("DO")).thenReturn("Dominican");
        when(countryGateway.getDemonym("US")).thenReturn("American");
    }

    @Test
    void createCustomer_returnsCreated() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "email": "juan@example.com",
                    "address": "Calle 1, Santo Domingo",
                    "phone": "+1-809-555-0100",
                    "country": "DO"
                }
                """)
            .when().post("/customers")
            .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("firstName", is("Juan"))
            .body("demonym", is("Dominican"))
            .body("country", is("DO"))
            .body("createdAt", notNullValue());
    }

    @Test
    void createCustomer_duplicateEmail_returnsConflict() {
        String body = """
            {
                "firstName": "Juan",
                "lastName": "Perez",
                "email": "dup@example.com",
                "address": "Calle 1",
                "phone": "+809",
                "country": "DO"
            }
            """;
        given().contentType(ContentType.JSON).body(body).post("/customers").then().statusCode(201);

        given().contentType(ContentType.JSON).body(body)
            .when().post("/customers")
            .then()
            .statusCode(409)
            .contentType("application/problem+json")
            .body("title", is("Conflict"));
    }

    @Test
    void getAllCustomers_returnsOk() {
        given().when().get("/customers")
            .then().statusCode(200).body("$", isA(java.util.List.class));
    }

    @Test
    void getAllCustomers_withPagination_returnsPagedResponse() {
        createOne("page@example.com");

        given().queryParam("page", 0).queryParam("size", 10)
            .when().get("/customers")
            .then()
            .statusCode(200)
            .body("data", notNullValue())
            .body("total", greaterThanOrEqualTo(1))
            .body("page", is(0))
            .body("size", is(10));
    }

    @Test
    void getAllCustomers_filterByCountry_returnsFilteredList() {
        createOne("do@example.com");

        given().queryParam("country", "DO")
            .when().get("/customers")
            .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    void getCustomerById_returnsOk() {
        int id = createOne("get@example.com");

        given().when().get("/customers/{id}", id)
            .then()
            .statusCode(200)
            .body("id", is(id))
            .body("email", is("get@example.com"));
    }

    @Test
    void getCustomerById_notFound_returns404() {
        given().when().get("/customers/{id}", 999999)
            .then()
            .statusCode(404)
            .contentType("application/problem+json")
            .body("title", is("Not Found"));
    }

    @Test
    void updateCustomer_partialUpdate_returnsOk() {
        int id = createOne("update@example.com");

        given()
            .contentType(ContentType.JSON)
            .body("""
                { "address": "New Address 123" }
                """)
            .when().patch("/customers/{id}", id)
            .then()
            .statusCode(200)
            .body("address", is("New Address 123"))
            .body("email", is("update@example.com"));
    }

    @Test
    void updateCustomer_countryChange_updatesDemonym() {
        when(countryGateway.getDemonym("US")).thenReturn("American");
        int id = createOne("country@example.com");

        given()
            .contentType(ContentType.JSON)
            .body("""
                { "country": "US" }
                """)
            .when().patch("/customers/{id}", id)
            .then()
            .statusCode(200)
            .body("country", is("US"))
            .body("demonym", is("American"));
    }

    @Test
    void deleteCustomer_returnsNoContent() {
        int id = createOne("delete@example.com");

        given().when().delete("/customers/{id}", id)
            .then().statusCode(204);

        given().when().get("/customers/{id}", id)
            .then().statusCode(404);
    }

    @Test
    void deleteCustomer_notFound_returns404() {
        given().when().delete("/customers/{id}", 999999)
            .then().statusCode(404);
    }

    private int createOne(String email) {
        return given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "firstName": "Test",
                    "lastName": "User",
                    "email": "%s",
                    "address": "Calle 1",
                    "phone": "+809",
                    "country": "DO"
                }
                """.formatted(email))
            .post("/customers")
            .then().statusCode(201)
            .extract().path("id");
    }
}
