package com.banreservas.customer.presentation;

import com.banreservas.customer.domain.port.CountryGateway;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestSecurity(user = "testUser", roles = {"user"})
class CustomerValidationTest {

    @InjectMock
    CountryGateway countryGateway;

    @Test
    void createCustomer_missingFirstName_returns400() {
        given().contentType(ContentType.JSON)
            .body("""
                {
                    "lastName": "Perez",
                    "email": "a@example.com",
                    "address": "Calle 1",
                    "phone": "+809",
                    "country": "DO"
                }
                """)
            .when().post("/customers")
            .then()
            .statusCode(400)
            .contentType("application/problem+json")
            .body("title", is("Validation Error"))
            .body("detail", containsString("firstName"));
    }

    @Test
    void createCustomer_missingLastName_returns400() {
        given().contentType(ContentType.JSON)
            .body("""
                {
                    "firstName": "Juan",
                    "email": "b@example.com",
                    "address": "Calle 1",
                    "phone": "+809",
                    "country": "DO"
                }
                """)
            .when().post("/customers")
            .then()
            .statusCode(400)
            .body("detail", containsString("lastName"));
    }

    @Test
    void createCustomer_invalidEmail_returns400() {
        given().contentType(ContentType.JSON)
            .body("""
                {
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "email": "not-an-email",
                    "address": "Calle 1",
                    "phone": "+809",
                    "country": "DO"
                }
                """)
            .when().post("/customers")
            .then()
            .statusCode(400)
            .body("detail", containsString("email"));
    }

    @Test
    void createCustomer_missingAddress_returns400() {
        given().contentType(ContentType.JSON)
            .body("""
                {
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "email": "c@example.com",
                    "phone": "+809",
                    "country": "DO"
                }
                """)
            .when().post("/customers")
            .then()
            .statusCode(400)
            .body("detail", containsString("address"));
    }

    @Test
    void createCustomer_missingPhone_returns400() {
        given().contentType(ContentType.JSON)
            .body("""
                {
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "email": "d@example.com",
                    "address": "Calle 1",
                    "country": "DO"
                }
                """)
            .when().post("/customers")
            .then()
            .statusCode(400)
            .body("detail", containsString("phone"));
    }

    @Test
    void createCustomer_invalidCountryFormat_returns400() {
        given().contentType(ContentType.JSON)
            .body("""
                {
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "email": "e@example.com",
                    "address": "Calle 1",
                    "phone": "+809",
                    "country": "DOMI"
                }
                """)
            .when().post("/customers")
            .then()
            .statusCode(400)
            .body("detail", containsString("country"));
    }

    @Test
    void createCustomer_missingCountry_returns400() {
        given().contentType(ContentType.JSON)
            .body("""
                {
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "email": "f@example.com",
                    "address": "Calle 1",
                    "phone": "+809"
                }
                """)
            .when().post("/customers")
            .then()
            .statusCode(400)
            .body("detail", containsString("country"));
    }
}
