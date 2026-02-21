package ru.mentee.power.crm.storage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

class LeadStorageTest {

  @Test
  void shouldAddLeadWhenLeadIsUnique() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    LeadStorage leadStorage = new LeadStorage();

    boolean added = leadStorage.add(lead);

    assertThat(added).isTrue();
    assertThat(leadStorage.size()).isEqualTo(1);
    assertThat(leadStorage.findAll()).containsExactly(lead);
  }

  @Test
  void shouldRejectDuplicateWhenEmailAlreadyExists() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead existingLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
    Lead duplicateLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    LeadStorage storage = new LeadStorage();
    storage.add(existingLead);

    boolean added = storage.add(duplicateLead);

    assertThat(added).isFalse();
    assertThat(storage.size()).isEqualTo(1);
    assertThat(storage.findAll()).containsExactly(existingLead);
  }

  @Test
  void shouldThrowExceptionWhenStorageIsFull() {

    LeadStorage storage = new LeadStorage();

    Address address = new Address("NY", "123 Main st.", "123456");

    for (int index = 0; index < 100; index++) {

      Contact contact = new Contact("lead" + index + "@mail.ru", "+71234567890", address);

      storage.add(new Lead(UUID.randomUUID(), contact, "Company", "NEW"));

    }

    Contact contact = new Contact("lead101@mail.ru", "+71234567890", address);
    Lead hundredFirstLead = new Lead(UUID.randomUUID(), contact, "Company", "NEW");

    assertThatThrownBy(() -> storage.add(hundredFirstLead))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Storage is full");

  }

  @Test
  void shouldReturnOnlyAddedLeadsWhenFindAllCalled() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact firstContact = new Contact("ivan@mail.ru", "+71234567890", address);
    Contact secondContact = new Contact("mariya@mail.ru", "+70987654321", address);

    Lead firstLead = new Lead(UUID.randomUUID(), firstContact, "TestCorp", "NEW");
    Lead secondLead = new Lead(UUID.randomUUID(), secondContact, "ITCorp", "NEW");

    LeadStorage storage = new LeadStorage();

    storage.add(firstLead);
    storage.add(secondLead);

    Lead[] result = storage.findAll();

    assertThat(result).containsExactly(firstLead, secondLead);
    assertThat(result).hasSize(2);
  }
}