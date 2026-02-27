package ru.mentee.power.crm.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

class LeadRepositoryTest {

  private LeadRepository repository;
  private Address address;
  private Contact contact;
  private Lead lead;
  private UUID leadId;

  @BeforeEach
  void setUp() {
    repository = new LeadRepository();
    address = new Address("Moscow", "Yamskaya", "123");
    contact = new Contact("test@example.ru", "+71234567890", address);
    leadId = UUID.randomUUID();
    lead = new Lead(leadId, contact, "SomeCompany", "NEW");
  }

  @Test
  void shouldSaveAndFindLeadByIdWhenLeadSaved() {
    repository.save(lead);
    assertThat(repository.findById(leadId)).isNotNull();
  }

  @Test
  void shouldReturnNullWhenLeadNotFound() {
    assertThat(repository.findById(leadId)).isNull();
  }

  @Test
  void shouldReturnAllLeadsWhenMultipleLeadsSaved() {
    Contact secondContact = new Contact("test2@example.ru", "+70987654321", address);
    Contact thirdContact = new Contact("test3@example.ru", "+71029384756", address);

    Lead secondLead = new Lead(UUID.randomUUID(), secondContact, "SecondCompany", "NEW");
    Lead thirdLead = new Lead(UUID.randomUUID(), thirdContact, "ThirdCompany", "NEW");

    repository.save(lead);
    repository.save(secondLead);
    repository.save(thirdLead);

    assertThat(repository.findAll().size()).isEqualTo(3);
  }

  @Test
  void shouldDeleteLeadWhenLeadExists() {

    repository.save(lead);
    repository.delete(leadId);

    assertThat(repository.findById(leadId)).isNull();
    assertThat(repository.size()).isEqualTo(0);
  }

  @Test
  void shouldOverwriteLeadWhenSaveWithSameId() {
    Contact secondContact = new Contact("test2@example.ru", "+70987654321", address);

    Lead secondLead = new Lead(leadId, secondContact, "SecondCompany", "NEW");

    repository.save(lead);
    repository.save(secondLead);

    assertThat(repository.findById(leadId)).isEqualTo(secondLead);
    assertThat(repository.size()).isEqualTo(1);
  }

  @Test
  @DisplayName("Иллюстрация проблемы отсутствия бизнес-логики")
  void shouldSaveBothLeadsEvenWithSameEmailAndPhoneBecauseRepositoryDoesNotCheckBusinessRules() {

    Lead duplicateLead = new Lead(UUID.randomUUID(), contact, "SecondCompany", "NEW");

    repository.save(lead);
    repository.save(duplicateLead);

    assertThat(repository.size()).isEqualTo(2);
  }

  @Test
  void shouldFindFasterWithMapThanWithListFilter() {

    List<Lead> leadList = new ArrayList<>();

    for (int i = 0; i < 1000; i++) {
      UUID id = UUID.randomUUID();
      Contact newContact = new Contact(
          "email" + i + "@test.com",
          "+7" + i,
          new Address("City" + i, "Street" + i, "ZIP" + i)
      );
      Lead newLead = new Lead(id, newContact, "Company" + i, "NEW");
      repository.save(newLead);
      leadList.add(newLead);
    }

    UUID targetId = leadList.get(500).id();  // Средний элемент

    long mapStart = System.nanoTime();
    Lead foundInMap = repository.findById(targetId);
    long mapDuration = System.nanoTime() - mapStart;

    long listStart = System.nanoTime();
    Lead foundInList = leadList.stream()
        .filter(foundedLead -> foundedLead.id().equals(targetId))
        .findFirst()
        .orElse(null);
    long listDuration = System.nanoTime() - listStart;

    // Then: Map должен быть минимум в 10 раз быстрее
    assertThat(foundInMap).isEqualTo(foundInList);
    assertThat(listDuration).isGreaterThan(mapDuration * 10);

    System.out.println("Map поиск: " + mapDuration + " ns");
    System.out.println("List поиск: " + listDuration + " ns");
    System.out.println("Ускорение: " + (listDuration / mapDuration) + "x");
  }
}