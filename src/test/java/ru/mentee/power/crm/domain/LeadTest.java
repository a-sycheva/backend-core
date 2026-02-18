package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LeadTest {

  @Test
  void shouldReturnIdWhenGetIdCalled() {

    Lead lead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String id = lead.getId();

    assertThat(id).isEqualTo("L1");
  }

  @Test
  void shouldReturnEmailWhenGetIdCalled() {

    Lead lead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String email = lead.getEmail();

    assertThat(email).isEqualTo("test@example.ru");
  }

  @Test
  void shouldReturnPhoneWhenGetIdCalled() {

    Lead lead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String phone = lead.getPhone();

    assertThat(phone).isEqualTo("+71234567890");
  }

  @Test
  void shouldReturnCompanyWhenGetIdCalled() {

    Lead lead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String company = lead.getCompany();

    assertThat(company).isEqualTo("TestCorp");
  }

  @Test
  void shouldReturnStatusWhenGetIdCalled() {

    Lead lead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String status = lead.getStatus();

    assertThat(status).isEqualTo("NEW");
  }

  @Test
  void shouldReturnFormattedStringWhenToStringCalled() {

    Lead lead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    String result = lead.toString();

    assertThat(result).contains("L1", "test@example.ru",  "+71234567890", "TestCorp", "NEW");
  }

}