package ru.mentee.power.crm.spring.service;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

@SpringBootTest
@WireMockTest(httpPort = 8089)
@ActiveProfiles("test")
@Transactional
public class LeadServiceRetryTest {

  @Autowired private LeadService leadService;

  @Test
  void shouldRetryAndSucceedWhenFirstAttemptFails() {
    // Given: Первые 2 вызова возвращают 500, третий — успех
    // WireMock Scenarios позволяют менять ответ между вызовами
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Retry Test")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(serverError())
            .willSetStateTo("First Retry"));

    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Retry Test")
            .whenScenarioStateIs("First Retry")
            .willReturn(serverError())
            .willSetStateTo("Second Retry"));

    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Retry Test")
            .whenScenarioStateIs("Second Retry")
            .willReturn(
                okJson(
                    """
                {"email": "test@example.com", "valid": true, "reason": "OK"}
                """)));

    // When: создаём лида
    Lead created = leadService.addLead("Joe", "test@example.com", null, LeadStatus.NEW);

    // Then: лид создан после 3 попыток
    assertThat(created).isNotNull();

    // Verify: было ровно 3 HTTP вызова
    verify(3, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldUseFallbackWhenAllRetriesFail() {
    // Given: Все 3 вызова возвращают 500
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(serverError().withBody("Service Unavailable")));

    // When: создаём лида
    Lead created = leadService.addLead("Joe", "test@example.com", null, LeadStatus.NEW);

    // Then: fallback сработал — лид создан без валидации
    assertThat(created).isNotNull();

    // Verify: было 3 попытки (max-attempts)
    verify(3, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldNotRetryWhenClientErrorOccurs() {
    // Given: 400 Bad Request (клиентская ошибка)
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(badRequest().withBody("{\"error\": \"Invalid format\"}")));

    // When/Then: исключение без retry
    // 4xx ошибки в ignore-exceptions — не повторяем
    String email = "invalid";

    // В зависимости от реализации: либо исключение, либо fallback
    // Проверяем что был только 1 вызов (без retry)
    try {
      Lead created = leadService.addLead("Joe", email, null, LeadStatus.NEW);
    } catch (Exception ignored) {
      // Ожидаем исключение для 4xx
    }

    // Verify: только 1 попытка — retry НЕ сработал для 4xx
    verify(1, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }

  @Test
  void shouldRetryWhenTimeoutOccurs() {
    // Given: Первый вызов — timeout, второй — успех
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Timeout Retry")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(ok().withFixedDelay(10000)) // 10 секунд — больше timeout
            .willSetStateTo("After Timeout"));

    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .inScenario("Timeout Retry")
            .whenScenarioStateIs("After Timeout")
            .willReturn(
                okJson(
                    """
                {"email": "test@example.com", "valid": true, "reason": "OK"}
                """)));

    // When: создаём лида (первый вызов timeout, второй успех)
    Lead created = leadService.addLead("Joe", "test@example.com", null, LeadStatus.NEW);

    // Then: лид создан после retry
    assertThat(created).isNotNull();

    // Verify: было 2 попытки
    verify(2, getRequestedFor(urlPathEqualTo("/api/validate/email")));
  }
}
