package ru.mentee.power.crm.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

class InMemoryLeadRepositoryTest {

  @Test
  void  shouldAddLeadWhenDataIsValid () {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();

    leadRepository.add(lead);

    assertThat(leadRepository.findAll().size()).isEqualTo(1);
  }

  @Test
  void shouldThrowExceptionWhenAddDuplicateByIdLeads() {
    Address firstAddress = new Address("NY", "123 Main st.", "123456");
    Contact firstContact = new Contact("mail@example.ru", "+71234567890", firstAddress);

    Address secondAddress = new Address("NY", "123 Main st.", "123456");
    Contact secondContact = new Contact("mail@example.ru", "+71234567890", secondAddress);

    UUID id = UUID.randomUUID();

    Lead firstLead = new Lead(id, firstContact, "TestCorp", "NEW");
    Lead secondLead = new Lead(id, secondContact, "TestCorp", "NEW");

    InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();

    leadRepository.add(firstLead);

    assertThatThrownBy(() -> leadRepository.add(secondLead))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("This lead already exists!");
  }

  @Test
  void shouldReturnEmptyWhenFindByNullId() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();

    leadRepository.add(lead);

    assertThat(leadRepository.findById(null)).isNotPresent();
  }

  @Test
  void shouldReturnEmptyWhenTryToFindDontExistLead() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();

    leadRepository.add(lead);

    assertThat(leadRepository.findById(UUID.randomUUID())).isNotPresent();
  }

  @Test
  void shouldReturnLeadWhenTryToFindExistLead() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead firstLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
    Lead secondLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();

    leadRepository.add(firstLead);
    leadRepository.add(secondLead);

    Lead foundLead = leadRepository.findById(firstLead.id()).orElse(null);

    assertThat(foundLead).isEqualTo(firstLead);
  }

  @Test
  void shouldRemoveLeadWhenItCalled() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead firstLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
    Lead secondLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
    Lead thirdLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
    Lead fourthLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
    Lead fivesLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();

    leadRepository.add(firstLead);
    leadRepository.add(secondLead);
    leadRepository.add(thirdLead);
    leadRepository.add(fourthLead);
    leadRepository.add(fivesLead);

    leadRepository.remove(thirdLead.id());

    assertThat(leadRepository.findAll().size()).isEqualTo(4);
    assertThat(leadRepository.findById(thirdLead.id())).isNotPresent();
  }

  @Test
  void shouldThrowExceptionWhenRemoveNonExistentLead() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();

    leadRepository.add(lead);

    assertThatThrownBy(() -> leadRepository.remove(UUID.randomUUID()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("This lead does not exists!");
  }

  @Test
  void shouldNotChangeInternalStorage() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead firstLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
    Lead secondLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    InMemoryLeadRepository leadRepository = new InMemoryLeadRepository();

    leadRepository.add(firstLead);

    leadRepository.findAll().add(secondLead);

    assertThat(leadRepository.findAll().size()).isEqualTo(1);
  }

}