package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ContactTest {

  @Test
  void shouldCreateContactWhenValidData() {

    Address joeAddress = new Address("NY", "123 Main st.", "456712");

    Contact contact = new Contact("John@example.ru", "+71234567890", joeAddress);

    assertThat(contact.email()).isEqualTo("John@example.ru");
    assertThat(contact.phone()).isEqualTo("+71234567890");
    assertThat(contact.address()).isEqualTo(joeAddress);
    assertThat(contact.address().city()).isEqualTo("NY");
  }

  @Test
  void shouldDelegateToAddressWhenAccessingCity() {
    Address joeAddress = new Address("NY", "123 Main st.", "456712");

    Contact contact = new Contact("John@example.ru", "+71234567890", joeAddress);

    assertThat(contact.address().city()).isEqualTo("NY");
    assertThat(contact.address().street()).isEqualTo("123 Main st.");
  }

  @Test
  void shouldThrowExceptionWhenAddressIsNull() {

    assertThatThrownBy(() -> new Contact("John@example.ru", "+71234567890", null))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Address can`t be empty");
  }

  @Test
  void shouldThrowExceptionWhenPhoneIsNull() {
    Address address = new Address("NY", "123 Main st.", "456712");

    assertThatThrownBy(() -> new Contact("John@example.ru", null, address))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Phone can`t be empty");
  }

  @Test
  void shouldThrowExceptionWhenEmailIsNull() {
    Address address = new Address("NY", "123 Main st.", "456712");

    assertThatThrownBy(() -> new Contact(null, "+71234567890", address))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("E-mail can`t be empty");
  }

  @Test
  void shouldBeEqualWhenSameData() {
    Contact firstContact = new Contact("John@example.ru", "+71234567890",
        new Address("NY", "123 Main st.", "456712"));
    Contact secondContact = new Contact("John@example.ru", "+71234567890",
        new Address("NY", "123 Main st.", "456712"));

    assertThat(firstContact.equals(secondContact)).isTrue();
    assertThat(firstContact).hasSameHashCodeAs(secondContact);
  }

  @Test
  void shouldNotBeEqualWhenDifferentData() {
    Contact firstContact = new Contact("John@example.ru",
        "+71234567890", new Address("NY", "123 Main st.", "456712"));
    Contact secondContact = new Contact("Dexter@example.ru",
        "+70987654321", new Address("Miami", "4 Police st.", "123789"));

    assertThat(firstContact.equals(secondContact)).isFalse();
    assertThat(firstContact).doesNotHaveSameHashCodeAs(secondContact);
  }

}