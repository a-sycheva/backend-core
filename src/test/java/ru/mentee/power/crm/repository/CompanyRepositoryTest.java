package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

@DataJpaTest
@ActiveProfiles("test")
class CompanyRepositoryTest {

  @PersistenceContext private EntityManager entityManager;

  @Autowired private CompanyRepository companyRepository;

  @Autowired private LeadRepository leadRepository;

  @Test
  void shouldSaveCompanyWithLeads() {
    // Given
    Company company = new Company("Tinkoff", "Finance");

    Lead firstLead = new Lead("John", "ivan@t.ru", LeadStatus.NEW);
    Lead secondLead = new Lead("Mariya", "maria@t.ru", LeadStatus.CONTACTED);
    Lead thirdLead = new Lead("Alina", "alina@t.ru", LeadStatus.CONTACTED);

    company.addLead(firstLead);
    company.addLead(secondLead);
    company.addLead(thirdLead);

    // When
    Company saved = companyRepository.save(company);

    // Then
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getLeads()).hasSize(3);

    // Проверяем, что в БД создались записи
    Company found = companyRepository.findById(saved.getId()).orElseThrow();
    assertThat(found.getLeads()).hasSize(3);

    // проверяем что у Lead обновилась company
    assertThat(firstLead.getCompany()).isEqualTo(company);
    assertThat(secondLead.getCompany()).isEqualTo(company);
    assertThat(thirdLead.getCompany()).isEqualTo(company);
  }

  @Test
  void shouldAvoidN1WithEntityGraph() {
    // Given — создаём компанию с 5 лидами
    Company company = new Company("Тинькофф", "Finance");
    for (int i = 0; i < 5; i++) {
      Lead lead = new Lead("Lead" + i, "lead" + i + "@tinkoff.ru", LeadStatus.NEW);
      company.addLead(lead);
    }
    Company saved = companyRepository.save(company);

    // Очищаем Persistence Context для чистоты эксперимента
    entityManager.flush();
    entityManager.clear();

    // When — используем метод с @EntityGraph
    Company found = companyRepository.findByIdWithLeads(saved.getId()).orElseThrow();

    // Then — проверяем, что leads загружены
    assertThat(found.getLeads()).hasSize(5);

    // SQL логи содержат 1 SELCT-запрос с LEFT JOIN leads
  }
}
