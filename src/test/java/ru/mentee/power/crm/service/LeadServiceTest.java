package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepository;

class LeadServiceTest {
  private LeadService service;
  private LeadRepository repository;

  @BeforeEach
  void setUp() {
    repository = new InMemoryLeadRepository();
    service = new LeadService(repository);
  }

  @Test
  void shouldCreateLeadWhenEmailIsUnique() {
    String email = "test@example.com";
    String company = "Test Company";
    LeadStatus status = LeadStatus.NEW;

    Lead result = service.addLead(email, company, status);

    assertThat(result).isNotNull();
    assertThat(result.email()).isEqualTo(email);
    assertThat(result.company()).isEqualTo(company);
    assertThat(result.status()).isEqualTo(status);
    assertThat(result.id()).isNotNull();
  }

  @Test
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    String email = "duplicate@example.com";
    service.addLead(email, "First Company", LeadStatus.NEW);

    assertThatThrownBy(() ->
        service.addLead(email, "Second Company", LeadStatus.NEW)
    )
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Lead with email already exists");
  }

  @Test
  void shouldFindAllLeads() {
    service.addLead("one@example.com", "Company 1", LeadStatus.NEW);
    service.addLead("two@example.com", "Company 2", LeadStatus.CONTACTED);

    List<Lead> result = service.findAll();

    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  void shouldFindLeadById() {
    Lead created = service.addLead("find@example.com", "Company", LeadStatus.NEW);

    Optional<Lead> result = service.findById(created.id());

    assertThat(result).isPresent();
    assertThat(result.get().email()).isEqualTo("find@example.com");
  }

  @Test
  void shouldFindLeadByEmail() {
    service.addLead("search@example.com", "Company", LeadStatus.NEW);

    Optional<Lead> result = service.findByEmail("search@example.com");

    assertThat(result).isPresent();
    assertThat(result.get().company()).isEqualTo("Company");
  }

  @Test
  void shouldReturnEmptyWhenLeadNotFound() {
    Optional<Lead> result = service.findByEmail("nonexistent@example.com");

    assertThat(result).isEmpty();
  }

  @ParameterizedTest
  @CsvSource({
      "NEW, 3",
      "CONTACTED, 5",
      "QUALIFIED, 2"
  })
  void shouldReturnOnlyNeededLeadsWhenFindByStatus(String status, int count) {
    service.addLead("one@example.com", "Company 1", LeadStatus.NEW);
    service.addLead("two@example.com", "Company 2", LeadStatus.CONTACTED);
    service.addLead("three@example.com", "Company 3", LeadStatus.QUALIFIED);
    service.addLead("four@example.com", "Company 4", LeadStatus.CONTACTED);
    service.addLead("five@example.com", "Company 5", LeadStatus.NEW);
    service.addLead("six@example.com", "Company 6", LeadStatus.CONTACTED);
    service.addLead("seven@example.com", "Company 7", LeadStatus.QUALIFIED);
    service.addLead("eight@example.com", "Company 8", LeadStatus.CONTACTED);
    service.addLead("nine@example.com", "Company 9", LeadStatus.NEW);
    service.addLead("ten@example.com", "Company 10", LeadStatus.CONTACTED);

    List<Lead> result = service.findByStatus(LeadStatus.valueOf(status));

    assertThat(result).hasSize(count);
    assertThat(result).allMatch(lead -> lead.status().equals(LeadStatus.valueOf(status)));
  }

  @Test
  void shouldReturnEmptyListWhenNoLeadsWithStatusQualified() {
    service.addLead("one@example.com", "Company 1", LeadStatus.NEW);
    service.addLead("two@example.com", "Company 2", LeadStatus.CONTACTED);
    service.addLead("three@example.com", "Company 3", LeadStatus.NEW);

    List<Lead> result = service.findByStatus(LeadStatus.QUALIFIED);

    assertThat(result).hasSize(0);
  }

  @Test
  void shouldReturnEmptyListWhenNoLeadsWithStatusNew() {
    service.addLead("one@example.com", "Company 1", LeadStatus.QUALIFIED);
    service.addLead("two@example.com", "Company 2", LeadStatus.CONTACTED);
    service.addLead("three@example.com", "Company 3", LeadStatus.QUALIFIED);

    List<Lead> result = service.findByStatus(LeadStatus.NEW);

    assertThat(result).hasSize(0);
  }

  @Test
  void shouldReturnEmptyListWhenNoLeadsWithStatusContact() {
    service.addLead("one@example.com", "Company 1", LeadStatus.NEW);
    service.addLead("two@example.com", "Company 2", LeadStatus.QUALIFIED);
    service.addLead("three@example.com", "Company 3", LeadStatus.NEW);

    List<Lead> result = service.findByStatus(LeadStatus.CONTACTED);

    assertThat(result).hasSize(0);
  }

}