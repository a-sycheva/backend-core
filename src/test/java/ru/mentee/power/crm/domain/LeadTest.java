package ru.mentee.power.crm.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class LeadTest {

  @Test
  void shouldCreateLeadWhenValidData() {

    UUID id = UUID.randomUUID();
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(id, contact, "TestCorp", "NEW");

    assertThat(lead.id()).isEqualTo(id);
    assertThat(lead.contact()).isEqualTo(contact);
    assertThat(lead.company()).isEqualTo("TestCorp");
    assertThat(lead.status()).isEqualTo("NEW");
  }

  @Test
  void shouldAccessEmailThroughDelegationWhenLeadCreated() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("test@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    String email = lead.contact().email();

    assertThat(email).isEqualTo("test@example.ru");
  }

  @Test
  void shouldAccessPhoneThroughDelegationWhenLeadCreated() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    String phone = lead.contact().phone();

    assertThat(phone).isEqualTo("+71234567890");
  }

  @Test
  void shouldDemonstrateThreeLevelCompositionWhenAccessingCity() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    String city = lead.contact().address().city();

    assertThat(city).isEqualTo("NY");
  }

  @Test
  void shouldDemonstrateThreeLevelCompositionWhenAccessingStreet() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    String street = lead.contact().address().street();

    assertThat(street).isEqualTo("123 Main st.");
  }

  @Test
  void shouldDemonstrateThreeLevelCompositionWhenAccessingZip() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    String zip = lead.contact().address().zip();

    assertThat(zip).isEqualTo("123456");
  }

  @Test
  void shouldGenerateUniqueIdsWhenMultipleLeads() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead firstLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
    Lead secondLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    assertThat(firstLead.id()).isNotEqualTo(secondLead.id());
  }

  @Test
  void shouldBeEqualWhenSameIdButDifferentContact() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact firstContact = new Contact("mail@example.ru", "+71234567890", address);
    Contact secondContact = new Contact("ya@example.ru", "+70987654321", address);

    UUID id = UUID.randomUUID();

    Lead firstLead = new Lead(id, firstContact, "TestCorp", "NEW");
    Lead secondLead = new Lead(id, secondContact, "TestCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
  }

  @Test
  void shouldThrowExceptionWhenIdIsNull() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    assertThatThrownBy(() -> new Lead(null, contact, "TestCorp", "NEW"))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Id can`t be empty");
  }

  @Test
  void shouldThrowExceptionWhenContactIsNull() {

    assertThatThrownBy(() -> new Lead(UUID.randomUUID(), null, "TestCorp", "NEW"))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Contact can`t be empty");
  }

  @Test
  void shouldThrowExceptionWhenStatusIsNull() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    assertThatThrownBy(() -> new Lead(UUID.randomUUID(), contact, "TestCorp", null))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Status can`t be empty");
  }

  @Test
  void shouldThrowExceptionWhenInvalidStatus() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    assertThatThrownBy(() -> new Lead(UUID.randomUUID(), contact, "TestCorp", "status"))
        .isInstanceOf(IllegalArgumentException.class).hasMessage("Invalid status!");
  }

  void findByID (UUID id) {
    // в рамках учебного приложения здесь пусто
    //важна проверка типобезопасности
  }

  @Test
  void shouldPreventStringConfusionWhenUsingUUID() {
    UUID id = UUID.randomUUID();

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(id, contact, "TestCorp", "NEW");

    findByID(lead.id()); //передаем UUID, не вызывает ошибок, компилируется

    // следующая строка не компилируется. ERROR: incompatible types
    // findById("some-string");  // String cannot be converted to UUID
  }
}