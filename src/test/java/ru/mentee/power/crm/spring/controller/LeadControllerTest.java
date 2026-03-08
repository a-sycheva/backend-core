package ru.mentee.power.crm.spring.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LeadControllerTest {

  @Autowired
  TestRestTemplate testRestTemplate;

  @Test
  void shouldReturnHtmlTableWhenDoGetCalled() {
    ResponseEntity<String> response = testRestTemplate.getForEntity("/leads", String.class);

    assertThat(response.getBody())
        .contains("<title>CRM - Lead Management</title>")
        .contains("Email</th>")
        .contains("<p>&copy; 2025 CRM Project</p>");
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }
}