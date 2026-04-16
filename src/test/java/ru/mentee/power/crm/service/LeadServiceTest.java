package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
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

  @Test // self-invocation problem
  void demonstrateSelfInvocationProblem() {
    List<LeadStatus> statusesBefore =
        service.findByStatus(LeadStatus.NEW).stream()
            .map(Lead::getStatus)
            .collect(Collectors.toList());
    List<UUID> ids = new ArrayList<>();
    for (Lead lead : service.findAll()) {
      ids.add(lead.id());
    }
    // Ошибка в одном processSingleLead
    ids.add(UUID.randomUUID());

    service.processLeadsWithInvocationProblem(ids);

    List<LeadStatus> statusesAfter =
        service.findByStatus(LeadStatus.NEW).stream()
            .map(Lead::getStatus)
            .collect(Collectors.toList());

    // статусы лидов не изменились, откат по всем
    assertThat(statusesBefore).isEqualTo(statusesAfter);
    // нет лидов со статусом CONTACTED
    assertThat(statusesAfter).hasSize(3);
  }

  @Test // self-invocation problem solved
  void processLeadsShouldIsolateTransactionsPerLead() {

    List<LeadStatus> statusesBefore =
        service.findByStatus(LeadStatus.NEW).stream()
            .map(Lead::getStatus)
            .collect(Collectors.toList());
    List<UUID> ids = new ArrayList<>();
    for (Lead lead : service.findAll()) {
      ids.add(lead.id());
    }
    // Ошибка в одном processSingleLead
    ids.add(UUID.randomUUID());

    String transactionName = service.processLeads(ids);

    List<LeadStatus> statusesAfter =
        service.findByStatus(LeadStatus.NEW).stream()
            .map(Lead::getStatus)
            .collect(Collectors.toList());

    // создает новую транзакцию
    assertThat(transactionName).contains("LeadProcessor").contains("processSingleLead");
    // статусы лидов изменились, откат только по ошибочной транзакции
    assertThat(statusesBefore).isNotEqualTo(statusesAfter);
    // нет лидов со статусом NEW
    assertThat(statusesAfter).hasSize(0);
  }

  @Transactional
  @ParameterizedTest
  // REQUIRES_NEW показан в предыдущем
  // тесте processLeadsShouldIsolateTransactionsPerLead
  @EnumSource(
      value = Propagation.class,
      names = {"REQUIRED", "MANDATORY"})
  void testPropagation(Propagation propagation) {

    List<UUID> ids = new ArrayList<>();
    for (Lead lead : service.findAll()) {
      ids.add(lead.id());
    }

    switch (propagation) {
      case REQUIRED: // присоединяется к имеющейся транзакции, не создает свою
        assertThat(service.processLeadsWithRequires(ids))
            .contains("testPropagation")
            .doesNotContain("LeadProcessor")
            .doesNotContain("processSingleLeadWithRequired");
        break;
      case MANDATORY: // присоединяется к имеющейся транзакции, если есть
        assertThat(service.processLeadsWithMandatory(ids))
            .contains("testPropagation")
            .doesNotContain("LeadProcessor")
            .doesNotContain("processSingleLeadWithMandatory");
        break;
    }
  }

  @Test // MANDATORY без активной транзакции
  void testPropagationMandatoryMethodShouldTrowExceptionWithoutTransaction() {

    List<UUID> ids = new ArrayList<>();
    for (Lead lead : service.findAll()) {
      ids.add(lead.id());
    }
    // Ошибка в одном processSingleLead
    ids.add(UUID.randomUUID());

    // ошибка, если нет активных транзакций
    assertThrows(
        IllegalTransactionStateException.class, () -> service.processLeadsWithMandatory(ids));
  }

  @Test
  // тест READ_COMMITED с последовательным вызовом транзакций A-> B
  // для REPEATABLE_READ так не получилось,
  // в отдельном классе isolationTest параллельный тест
  void isolationReadCommitedAllowsNonRepeatableRead() {
    // Given
    Lead lead = new Lead();
    lead.setName("John");
    lead.setEmail("john" + "@example.com");
    Company company = new Company("Company ", "TestIndustry");
    lead.setStatus(LeadStatus.NEW);
    company.addLead(lead);
    companyRepository.save(company);

    // When транзакции A-> B внутри метода readThenWriteThenReadAgainWithReadCommitted
    List<String> results =
        service.readThenWriteThenReadAgainWithReadCommitted(lead.getId(), "Jane");

    // Then должны увидеть "Jane" при READ_COMMITTED
    assertThat(results).containsExactly("John", "Jane");
  }
}
