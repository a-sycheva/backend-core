package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.CompanyRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
@ActiveProfiles("test")
class LeadServiceTest {

  @Autowired private LeadService service;

  @Autowired private LeadRepository repository;

  @Autowired private CompanyRepository companyRepository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
    companyRepository.deleteAll();

    // Создаём 3 NEW лида
    for (int i = 1; i <= 3; i++) {
      Lead lead = new Lead();
      lead.setName("Name" + i);
      lead.setEmail("lead" + i + "@example.com");
      Company company = new Company("Company " + i, "TestIndustry");
      lead.setStatus(LeadStatus.NEW);
      company.addLead(lead);
      companyRepository.save(company);
    }
  }

  @AfterEach
  void tearDown() {

    repository.deleteAll();
    companyRepository.deleteAll();
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
    Company company = companyRepository.findByName("Company 1").get();

    Lead lead = new Lead();
    lead.setName("Name" + 4);
    lead.setEmail("lead" + 4 + "@example.com");
    lead.setStatus(LeadStatus.NEW);
    lead.setCompany(company);
    repository.save(lead);

    // company.addLead(lead);
    // companyRepository.save(company);

    Page<Lead> result = service.searchByCompany(company, 0, 5);

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

  @Test
  void convertLeadToDealShouldCommitOnSuccess() {
    Lead lead = service.findAll().getFirst();
    assertThat(lead.status()).isEqualTo(LeadStatus.NEW);

    service.convertLeadToDeal(lead.id(), BigDecimal.valueOf(10_000));

    Lead updatedLead = service.findById(lead.id()).get();
    assertThat(updatedLead.status()).isEqualTo(LeadStatus.CONTACTED);
  }

  @Test
  @Transactional
  void convertLeadToDealShouldRollbackOnConstraintViolation() {
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> service.convertLeadToDeal(UUID.randomUUID(), BigDecimal.valueOf(10_000)));
    assertThat(exception.getMessage()).contains("Lead not found");
  }
}
