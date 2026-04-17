package ru.mentee.power.crm.spring.client;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.spring.exception.EmailValidationException;

@SpringBootTest
@WireMockTest(httpPort = 8089)
@ActiveProfiles("test")
class EmailValidationClientWireMockTest {
  @Autowired private EmailValidationClient emailValidationClient;

  @Test
  void shouldReturnValidWhenEmailIsCorrect(WireMockRuntimeInfo wmRuntimeInfo) {
    // Given: WireMock stub возвращает valid=true
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("john@example.com"))
            .willReturn(
                okJson(
                    """
                {
                    "email": "john@example.com",
                    "valid": true,
                    "reason": "Email exists"
                }
                """)));

    // When: вызываем клиент
    EmailValidationResponse response = emailValidationClient.validateEmail("john@example.com");

    // Then: получаем корректный response
    assertThat(response).isNotNull();
    assertThat(response.valid()).isTrue();
    assertThat(response.email()).isEqualTo("john@example.com");
  }

  @Test
  void shouldReturnInvalidWhenEmailIsIncorrect(WireMockRuntimeInfo wmRuntimeInfo) {
    // Given: WireMock stub возвращает valid=false
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("invalid-email"))
            .willReturn(
                okJson(
                    """
                {
                    "email": "invalid-email",
                    "valid": false,
                    "reason": "Invalid email format"
                }
                """)));

    // When: вызываем клиент
    EmailValidationResponse response = emailValidationClient.validateEmail("invalid-email");

    // Then: email невалиден
    assertThat(response).isNotNull();
    assertThat(response.valid()).isFalse();
  }

  @Test
  void shouldHandleServerErrorWhenExternalServiceFails(WireMockRuntimeInfo wmRuntimeInfo) {
    // Given: WireMock stub возвращает 500 Internal Server Error
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(serverError().withBody("Internal Server Error")));

    assertThatThrownBy(() -> emailValidationClient.validateEmail("test@mail.ru"))
        .isInstanceOf(EmailValidationException.class)
        .hasMessageContaining("Email service error");
  }

  @Test
  void shouldHandleTimeoutWhenExternalServiceIsSlow(WireMockRuntimeInfo wmRuntimeInfo) {
    // Given: WireMock stub отвечает с задержкой 15 секунд (больше timeout)
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(okJson("{\"valid\": true}").withFixedDelay(15000))); // 15 секунд

    assertThatThrownBy(() -> emailValidationClient.validateEmail("test@mail.ru"))
        .isInstanceOf(EmailValidationException.class)
        .hasMessageContaining("Email service timeout");
  }
}
