package ru.mentee.power.crm.web;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HelloCrmServerTest {
  private final int port = 8082;
  static String host = "127.0.0.1";
  private HelloCrmServer server;
  static HttpClient client;
  static HttpRequest request;

  @BeforeEach
  void setUp() throws Exception {
    server = new HelloCrmServer(port);
    client = HttpClient.newHttpClient();

    request = HttpRequest.newBuilder()
        .uri(new URI("http://" + host + ":" + port + "/hello"))
        .timeout(Duration.ofSeconds(1))
        .GET()
        .build();
  }

  @Test
  void shouldWriteCorrectResponse() throws Exception {
    server.start();

    HttpResponse<String> response = client.send(
        request,
        HttpResponse.BodyHandlers.ofString());

    client.close();

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(response.body())
        .contains("Hello CRM!")
        .contains("<!DOCTYPE html>");
  }

  @Test
  void shouldStartAndStopServerWhenItCalled() {

    server.start();

    assertThat(isHttpServerRunning()).isTrue();

    server.stop();

    assertThat(isHttpServerRunning()).isFalse();

  }

  public static boolean isHttpServerRunning() {
    try {
      client.send(request, HttpResponse.BodyHandlers.discarding());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

}