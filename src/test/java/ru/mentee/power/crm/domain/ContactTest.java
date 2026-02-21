package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContactTest {

  @Test
  void shouldCreateContactWhenValidData() {
    Contact contact = new Contact("John", "Doe", "john@example.com");

    assertThat(contact.firstName()).isEqualTo("John");
    assertThat(contact.lastName()).isEqualTo("Doe");
    assertThat(contact.email()).isEqualTo("john@example.com");

  }

  @Test
  void shouldBeEqualWhenSameData() {
    Contact firstContact = new Contact("John", "Doe", "john@example.com");
    Contact secondContact = new Contact("John", "Doe", "john@example.com");

    assertThat(firstContact.equals(secondContact)).isTrue();
    assertThat(firstContact).hasSameHashCodeAs(secondContact);
  }

  @Test
  void shouldNotBeEqualWhenDifferentData() {
    Contact firstContact = new Contact("John", "Doe", "john@example.com");
    Contact secondContact = new Contact("Dexter", "Morgan", "dexter@example.com");

    assertThat(firstContact.equals(secondContact)).isFalse();
    assertThat(firstContact).doesNotHaveSameHashCodeAs(secondContact);
  }

}