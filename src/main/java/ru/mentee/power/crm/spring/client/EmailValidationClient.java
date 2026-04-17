package ru.mentee.power.crm.spring.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.mentee.power.crm.spring.exception.EmailValidationException;

@Component
public class EmailValidationClient {
  private final RestTemplate restTemplate;
  private final String baseUrl;

  public EmailValidationClient(
      RestTemplate restTemplate, @Value("${email.validation.base-url}") String baseUrl) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
  }

  public EmailValidationResponse validateEmail(String email) {
    String url =
        UriComponentsBuilder.fromHttpUrl(baseUrl)
            .path("/api/validate/email")
            .queryParam("email", email)
            .toUriString();

    try {
      EmailValidationResponse response =
          restTemplate.getForObject(url, EmailValidationResponse.class);
      return response;
    } catch (HttpServerErrorException e) {
      throw new EmailValidationException("Email service error", e.getStatusCode());
    } catch (ResourceAccessException e) {
      throw new EmailValidationException("Email service timeout", null);
    } catch (RestClientException e) {
      // 4xx и другие ошибки
      throw new EmailValidationException("Email validation failed", null);
    }
  }
}
