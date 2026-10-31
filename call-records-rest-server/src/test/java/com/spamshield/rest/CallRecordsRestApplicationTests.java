package com.spamshield.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CallRecordsRestApplicationTests {

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToServer().baseUrl("http://127.0.0.1:" + port).build();
    }

    @Test
    void contextLoads() {}

    @Test
    void rejectsAnEmptyCreate() {
        client.post()
                .uri("/api/call-records")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
