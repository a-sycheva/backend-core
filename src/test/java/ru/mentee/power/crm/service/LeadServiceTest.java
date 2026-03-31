package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
}