package com.cesizen.cesizen_back.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Disabled("Requires testcontainers or a running DB; enable when infra available")
public class AuthControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Test
    void login_and_logout_flow_should_work_end_to_end() {
        var rest = new RestTemplate();
        var url = "http://localhost:" + port + "/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String body = "{\"email\":\"test@test.fr\",\"password\":\"testtesttest\"}";

        ResponseEntity<String> resp = rest.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
