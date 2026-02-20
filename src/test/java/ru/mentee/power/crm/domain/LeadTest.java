package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class LeadTest {

  @Test
  void shouldReturnIdWhenGetIdCalled() {

    UUID id = UUID.randomUUID();

    Lead lead = new Lead(id, "test@example.ru", "+71234567890", "TestCorp", "NEW");

    assertThat(id).isEqualTo(lead.getId());
  }

  @Test
  void shouldReturnEmailWhenGetEmailCalled() {

    Lead lead = new Lead(UUID.randomUUID(), "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String email = lead.getEmail();

    assertThat(email).isEqualTo("test@example.ru");
  }

  @Test
  void shouldReturnPhoneWhenGetPhoneCalled() {

    Lead lead = new Lead(UUID.randomUUID(), "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String phone = lead.getPhone();

    assertThat(phone).isEqualTo("+71234567890");
  }

  @Test
  void shouldReturnCompanyWhenGetIdCalled() {

    Lead lead = new Lead(UUID.randomUUID(), "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String company = lead.getCompany();

    assertThat(company).isEqualTo("TestCorp");
  }

  @Test
  void shouldReturnStatusWhenGetStatusCalled() {

    Lead lead = new Lead(UUID.randomUUID(), "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String status = lead.getStatus();

    assertThat(status).isEqualTo("NEW");
  }

  @Test
  void shouldReturnFormattedStringWhenToStringCalled() {

    UUID id = UUID.randomUUID();

    Lead lead = new Lead(id, "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String result = lead.toString();

    assertThat(result).contains(id.toString(), "test@example.ru",
        "+71234567890", "TestCorp", "NEW");
  }

  @Test
  void shouldCreateLeadWhenValidData() {
    UUID id = UUID.randomUUID();

    Lead lead = new Lead(id, "test@example.ru", "+71234567890", "TestCorp", "NEW");

    assertThat(id).isEqualTo(lead.getId());
  }

  @Test
  void shouldGenerateUniqueIdsWhenMultipleLeads() {
    Lead firstLead = new Lead(UUID.randomUUID(), "test@example.ru", "+71234567890", "TestCorp", "NEW");
    Lead secondLead = new Lead(UUID.randomUUID(), "test@example.ru", "+71234567890", "TestCorp", "NEW");

    assertThat(firstLead.getId()).isNotEqualTo(secondLead);
  }

  void findByID (UUID id) {
    // в рамках учебного приложения здесь пусто
    //важна проверка типобезопасности
  }

  @Test
  void shouldPreventStringConfusionWhenUsingUUID() {
    UUID id = UUID.randomUUID();
    Lead lead = new Lead(id, "test@example.ru", "+71234567890", "TestCorp", "NEW");

    findByID(lead.getId()); //передаем UUID, не вызывает ошибок, компилируется

    // следующая строка не компилируется. ERROR: incompatible types
    // findById("some-string");  // String cannot be converted to UUID
  }
}