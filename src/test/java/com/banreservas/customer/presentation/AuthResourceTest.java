package com.banreservas.customer.presentation;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class AuthResourceTest {

    @Test
    void token_givenValidCredentials_returnsToken() {
        given().contentType(ContentType.JSON)
            .body("""
                { "username": "admin", "password": "secret123" }
                """)
            .when().post("/auth/token")
            .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("token", not(emptyString()));
    }

    @Test
    void token_givenInvalidPassword_returns401() {
        given().contentType(ContentType.JSON)
            .body("""
                { "username": "admin", "password": "wrong" }
                """)
            .when().post("/auth/token")
            .then()
            .statusCode(401)
            .contentType("application/problem+json")
            .body("title", is("Unauthorized"));
    }

    @Test
    void token_givenInvalidUsername_returns401() {
        given().contentType(ContentType.JSON)
            .body("""
                { "username": "unknown", "password": "secret123" }
                """)
            .when().post("/auth/token")
            .then()
            .statusCode(401);
    }

    @Test
    void customersEndpoint_withoutToken_returns401() {
        given()
            .when().get("/customers")
            .then()
            .statusCode(401);
    }

    @Test
    void customersEndpoint_withValidToken_returnsOk() {
        String token = given().contentType(ContentType.JSON)
            .body("""
                { "username": "admin", "password": "secret123" }
                """)
            .post("/auth/token")
            .then().statusCode(200)
            .extract().path("token");

        given()
            .header("Authorization", "Bearer " + token)
            .when().get("/customers")
            .then()
            .statusCode(200);
    }
}
