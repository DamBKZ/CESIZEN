package com.cesizen.cesizen_back.functional;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
@Disabled("Functional test template - enable when infra/Testcontainers configured")
public class AuthControllerFunctionalTest {

    @Test
    void login_logout_end_to_end() {
        Response r = RestAssured.given()
                .contentType("application/json")
                .body("{\"email\":\"test@test.fr\",\"password\":\"testtesttest\"}")
                .post("/auth/login")
                .andReturn();

        assertThat(r.statusCode()).isEqualTo(200);
    }
}
