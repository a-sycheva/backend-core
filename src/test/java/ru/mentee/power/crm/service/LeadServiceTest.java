package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
@Transactional
class LeadServiceTest {

  @Autowired
  private LeadService service;

  @Autowired
  private LeadRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();

    // Создаём 3 NEW лида
    for (int i = 1; i <= 3; i++) {
      Lead lead = new Lead();
      lead.setName("Name" + i);
      lead.setEmail("lead" + i + "@example.com");
      lead.setCompany("Company " + i);
      lead.setStatus(LeadStatus.NEW);
      repository.save(lead);
    }
  }

  @Test
  void convertNewToContactedShouldUpdateMultipleLeads() {
    int updated = service.convertNewToContacted();


    assertThat(updated).isEqualTo(3);


    long contactedCount = repository.countByStatus(LeadStatus.CONTACTED);
    assertThat(contactedCount).isEqualTo(3);

    long newCount = repository.countByStatus(LeadStatus.NEW);
    assertThat(newCount).isEqualTo(0);
  }

  @Test
  void archiveOldLeadsShouldArchivedMultipleLeads() {

    assertThat(repository.findByStatus(LeadStatus.NEW)).hasSize(3);

    int archived = service.archiveOldLeads(LeadStatus.NEW);


    assertThat(archived).isEqualTo(3);
    long newLeads = repository.countByStatus(LeadStatus.NEW);
    assertThat(newLeads).isEqualTo(0);
  }

  @Test
  void searchByCompanyShouldReturnPage() {
    Lead lead = new Lead();
    lead.setName("Name" + 4);
    lead.setEmail("lead" + 4 + "@example.com");
    lead.setCompany("Company " + 1);
    lead.setStatus(LeadStatus.NEW);
    repository.save(lead);

    Page<Lead> result = service.searchByCompany("Company 1", 0, 5);

    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getTotalElements()).isEqualTo(2);

  }

  @Test
  void getFirstPageShouldReturnFirstPage() {

    Page<Lead> result = service.getFirstPage(1);

    assertThat(result.hasPrevious()).isFalse();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getTotalElements()).isEqualTo(3);
    assertThat(result.getTotalPages()).isEqualTo(3);

  }
}