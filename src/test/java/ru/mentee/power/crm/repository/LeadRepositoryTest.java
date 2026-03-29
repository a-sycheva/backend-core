package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LeadRepositoryTest {

  @Autowired
  private LeadRepository repository;

  @Test
  void shouldSaveAndFindLeadById_whenValidData() {
    Lead lead = new Lead("test@example.com",
        "ACME", LeadStatus.NEW);

    Lead saved = repository.save(lead);
    Optional<Lead> found = repository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void shouldFindByEmailNative_whenLeadExists() {
    Lead lead = new Lead("Joe", "native@test.com",
        "TechCorp", LeadStatus.NEW);
    repository.save(lead);

    Optional<Lead> found = repository.findByEmailNative("native@test.com");

    assertThat(found).isPresent();
    assertThat(found.get().getCompany()).isEqualTo("TechCorp");
  }

  @Test
  void shouldReturnEmptyOptional_whenEmailNotFound() {
    Optional<Lead> found = repository.findByEmailNative("nonexistent@test.com");

    assertThat(found).isEmpty();
  }

  @Test
  void shouldReturnLeadsWhenFindAll() {
    Lead firstLead = new Lead("Joe", "test1@test.com",
        "FirstCorp", LeadStatus.NEW);
    Lead secondLead = new Lead("Mike", "test2@test.com",
        "SecondCorp", LeadStatus.NEW);

    repository.save(firstLead);
    repository.save(secondLead);

    List<Lead> leads = repository.findAll();

    assertThat(leads).containsExactlyInAnyOrder(firstLead,secondLead).hasSize(2);
  }

  @Test
  void shouldDeleteLeadWhenCalledDelete() {
    Lead firstLead = new Lead("Joe", "test1@test.com",
        "FirstCorp", LeadStatus.NEW);
    Lead secondLead = new Lead("Dexter", "test2@test.com",
        "SecondCorp", LeadStatus.NEW);

    repository.save(firstLead);
    repository.save(secondLead);

    repository.delete(firstLead);

    assertThat(repository.findAll()).hasSize(1).contains(secondLead);
  }
}