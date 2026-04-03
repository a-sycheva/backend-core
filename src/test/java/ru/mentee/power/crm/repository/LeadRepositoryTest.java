package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class LeadRepositoryTest {

  @Autowired
  private LeadRepository repository;

  Lead firstLead;
  Lead secondLead;

  @BeforeEach
  void setUp() {
    firstLead = new Lead();
    firstLead.setName("John");
    firstLead.setEmail("john@example.com");
    firstLead.setCompany("ACME Corp");
    firstLead.setStatus(LeadStatus.NEW);
    firstLead.setCreatedAt(LocalDateTime.now().minusDays(5));
    repository.save(firstLead);

    secondLead = new Lead();
    secondLead.setName("Jane");
    secondLead.setEmail("jane@example.com");
    secondLead.setCompany("Tech Inc");
    secondLead.setStatus(LeadStatus.CONTACTED);
    secondLead.setCreatedAt(LocalDateTime.now().minusDays(2));
    repository.save(secondLead);
  }

  @Test
  void shouldSaveAndFindLeadByIdWhenValidData() {
    Lead lead = new Lead("test@example.com",
        "ACME", LeadStatus.NEW);

    Lead saved = repository.save(lead);
    Optional<Lead> found = repository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  void shouldFindByEmailNativeWhenLeadExists() {
    Lead lead = new Lead("Joe", "native@test.com",
        "TechCorp", LeadStatus.NEW);
    repository.save(lead);

    Optional<Lead> found = repository.findByEmailNative("native@test.com");

    assertThat(found).isPresent();
    assertThat(found.get().getCompany()).isEqualTo("TechCorp");
  }

  @Test
  void shouldReturnEmptyOptionalWhenEmailNotFound() {
    Optional<Lead> found = repository.findByEmailNative("nonexistent@test.com");

    assertThat(found).isEmpty();
  }

  @Test
  void shouldReturnLeadsWhenFindAll() {

    List<Lead> leads = repository.findAll();

    assertThat(leads).containsExactlyInAnyOrder(firstLead, secondLead).hasSize(2);
  }

  @Test
  void shouldDeleteLeadWhenCalledDelete() {

    repository.delete(firstLead);

    assertThat(repository.findAll()).hasSize(1).contains(secondLead);
  }

  @Test
  void findByEmailShouldReturnLeadWhenExists() {
    Optional<Lead> found = repository.findByEmail("john@example.com");

    assertThat(found).isPresent();
    assertThat(found.get().getCompany()).isEqualTo("ACME Corp");
  }

  @Test
  void findByStatusShouldReturnFilteredLeads() {
    List<Lead> newLeads = repository.findByStatus(LeadStatus.NEW);

    assertThat(newLeads).hasSize(1);
    assertThat(newLeads.get(0).getEmail()).isEqualTo("john@example.com");
  }

  @Test
  void findByStatusInShouldReturnLeadsWithMultipleStatuses() {
    List<LeadStatus> statuses = List.of(LeadStatus.NEW, LeadStatus.CONTACTED);

    List<Lead> found = repository.findByStatusIn(statuses);

    assertThat(found).hasSize(2);
  }

  @Test
  void findAllWithPageableShouldReturnPage() {
    PageRequest pageRequest = PageRequest.of(0, 1);

    Page<Lead> page = repository.findAll(pageRequest);

    assertThat(page.getContent()).hasSize(1);
    assertThat(page.getTotalElements()).isEqualTo(2);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.getNumber()).isEqualTo(0); // текущая страница
  }

  @Test
  void contByStatusShouldReturnCountOfLeadsWithGivenStatus() {
    assertThat(repository.countByStatus(LeadStatus.NEW)).isEqualTo(1);
    assertThat(repository.countByStatus(LeadStatus.CONTACTED)).isEqualTo(1);
  }

  @Test
  void existByEmailShouldReturnTrueWhenLeadWithGivenEmailExists() {
    assertThat(repository.existsByEmail("john@example.com")).isTrue();
  }

  @Test
  void existByEmailShouldReturnFalseWhenLeadWithGivenEmailNotExists() {
    assertThat(repository.existsByEmail("notexist@example.com")).isFalse();
  }

  @Test
  void findByStatusAndCompanyShouldReturnSearchedLeads() {
    Lead thirdLead = new Lead();
    thirdLead.setName("Mike");
    thirdLead.setEmail("mike@example.com");
    thirdLead.setCompany("ACME Corp");
    thirdLead.setStatus(LeadStatus.NEW);
    thirdLead.setCreatedAt(LocalDateTime.now().minusDays(5));
    repository.save(thirdLead);

    Lead fourthLead = new Lead();
    fourthLead.setName("Fred");
    fourthLead.setEmail("fred@example.com");
    fourthLead.setCompany("ACME Corp");
    fourthLead.setStatus(LeadStatus.CONTACTED);
    fourthLead.setCreatedAt(LocalDateTime.now().minusDays(2));
    repository.save(fourthLead);
    PageRequest pageRequest = PageRequest.of(0, 5);

    Page<Lead> result = repository.findByStatusAndCompany(
        LeadStatus.NEW, "ACME Corp", pageRequest);

    assertThat(result.getTotalElements()).isEqualTo(2);
    assertThat(result.getContent()).containsExactlyInAnyOrder(firstLead, thirdLead);
  }

  @Test
  void updateStatusBulkShouldUpdateLeadsWithGivenStatus() {
    Lead thirdLead = new Lead();
    thirdLead.setName("Mike");
    thirdLead.setEmail("mike@example.com");
    thirdLead.setCompany("ACME Corp");
    thirdLead.setStatus(LeadStatus.NEW);
    thirdLead.setCreatedAt(LocalDateTime.now().minusDays(5));
    repository.save(thirdLead);

    Lead fourthLead = new Lead();
    fourthLead.setName("Fred");
    fourthLead.setEmail("fred@example.com");
    fourthLead.setCompany("ACME Corp");
    fourthLead.setStatus(LeadStatus.QUALIFIED);
    fourthLead.setCreatedAt(LocalDateTime.now().minusDays(2));
    repository.save(fourthLead);

    int result = repository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
    List<Lead> leads = repository.findByStatus(LeadStatus.CONTACTED);

    assertThat(result).isEqualTo(2);
    assertThat(leads).hasSize(3)
        .containsExactlyInAnyOrder(firstLead, secondLead, thirdLead);
  }
}