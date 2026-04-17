package ru.mentee.power.crm.spring.config;

import java.io.IOException;
import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
        .setConnectTimeout(Duration.ofSeconds(5))
        .setReadTimeout(Duration.ofSeconds(10))
        .errorHandler(
            new DefaultResponseErrorHandler() {
              @Override
              public void handleError(ClientHttpResponse response) throws IOException {

                HttpStatusCode statusCode = response.getStatusCode();

                if (statusCode.is4xxClientError()) {
                  throw new RestClientException(
                      String.format("Client error %s: %s", statusCode, response.getStatusText()));
                }
                if (statusCode.is5xxServerError()) {
                  throw new HttpServerErrorException(statusCode, response.getStatusText());
                }
                super.handleError(response);
              }
            })
        .build();
  }
}
