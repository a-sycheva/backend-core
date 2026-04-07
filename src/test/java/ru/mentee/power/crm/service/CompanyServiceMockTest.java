package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.repository.CompanyRepository;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceMockTest {
  private CompanyService companyService;
  private UUID id;
  private Company company;

  @Mock
  private CompanyRepository mockCompanyRepository;

  @BeforeEach
  void setUp() {
    companyService = new CompanyService(mockCompanyRepository);

    id = UUID.randomUUID();
    company = new Company("TestCorp", "TestIndustry");
    company.setId(id);
  }

  @Test
  void shouldFindByIdWhenCompanyExists() {
    when(mockCompanyRepository.findById(id)).thenReturn(Optional.of(company));

    Optional<Company> result = companyService.findById(id);

    assertThat(result).isPresent();
    assertThat(result.get().getName()).isEqualTo("TestCorp");
    verify(mockCompanyRepository).findById(id);
  }

  @Test
  void shouldReturnEmptyWhenFindByIdNotFound() {
    when(mockCompanyRepository.findById(id)).thenReturn(Optional.empty());

    Optional<Company> result = companyService.findById(id);

    assertThat(result).isEmpty();
    verify(mockCompanyRepository).findById(id);
  }

  @Test
  void shouldReturnAllCompaniesWhenNoFilters() {
    List<Company> companies = List.of(
        new Company("ACME Corp", "IT"),
        new Company("TechInc", "Finance")
    );
    when(mockCompanyRepository.findAll()).thenReturn(companies);

    List<Company> result = companyService.findCompanies(null, null);

    assertThat(result).hasSize(2);
    verify(mockCompanyRepository).findAll();
  }

  @Test
  void shouldFilterCompaniesByName() {
    List<Company> companies = List.of(
        new Company("ACME Corp", "IT"),
        new Company("TechInc", "Finance")
    );
    when(mockCompanyRepository.findAll()).thenReturn(companies);

    List<Company> result = companyService.findCompanies("Tech", null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("TechInc");
    verify(mockCompanyRepository).findAll();
  }

  @Test
  void shouldFilterCompaniesByIndustry() {
    List<Company> companies = List.of(
        new Company("ACME Corp", "IT"),
        new Company("TechInc", "Finance")
    );
    when(mockCompanyRepository.findAll()).thenReturn(companies);

    List<Company> result = companyService.findCompanies(null, "Finance");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getIndustry()).isEqualTo("Finance");
    verify(mockCompanyRepository).findAll();
  }

  @Test
  void shouldFilterCompaniesByNameAndIndustry() {
    List<Company> companies = List.of(
        new Company("ACME Corp", "IT"),
        new Company("TechInc", "Finance"),
        new Company("TechCorp", "IT")
    );
    when(mockCompanyRepository.findAll()).thenReturn(companies);

    List<Company> result = companyService.findCompanies("Tech", "IT");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("TechCorp");
    verify(mockCompanyRepository).findAll();
  }

  @Test
  void shouldReturnEmptyListWhenNoMatchByName() {
    List<Company> companies = List.of(
        new Company("ACME Corp", "IT"),
        new Company("TechInc", "Finance")
    );
    when(mockCompanyRepository.findAll()).thenReturn(companies);

    List<Company> result = companyService.findCompanies("NonExistent", null);

    assertThat(result).isEmpty();
    verify(mockCompanyRepository).findAll();
  }

  @Test
  void shouldReturnEmptyListWhenNoMatchByIndustry() {
    List<Company> companies = List.of(
        new Company("ACME Corp", "IT"),
        new Company("TechInc", "Finance")
    );
    when(mockCompanyRepository.findAll()).thenReturn(companies);

    List<Company> result = companyService.findCompanies(null, "NonExistent");

    assertThat(result).isEmpty();
    verify(mockCompanyRepository).findAll();
  }

  @Test
  void shouldAddCompanyWhenNotExists() {
    when(mockCompanyRepository.findByName("TestCorp")).thenReturn(Optional.empty());
    when(mockCompanyRepository.save(any(Company.class))).thenReturn(company);

    Company result = companyService.addCompany("TestCorp", "TestIndustry");

    assertThat(result.getName()).isEqualTo("TestCorp");
    verify(mockCompanyRepository).findByName("TestCorp");
    verify(mockCompanyRepository).save(any(Company.class));
  }

  @Test
  void shouldThrowExceptionWhenAddDuplicateCompany() {
    Company existingCompany = new Company("TestCorp", "TestIndustry");
    when(mockCompanyRepository.findByName("TestCorp")).thenReturn(Optional.of(existingCompany));

    assertThatThrownBy(() -> companyService.addCompany("TestCorp", "TestIndustry"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Such company already exists");

    verify(mockCompanyRepository).findByName("TestCorp");
    verify(mockCompanyRepository, never()).save(any(Company.class));
  }

  @Test
  void shouldUpdateCompanyWhenExists() {
    Company updatedData = new Company("Updated Corp", "UpdatedIndustry");
    when(mockCompanyRepository.findById(id)).thenReturn(Optional.of(company));
    when(mockCompanyRepository.save(any(Company.class))).thenReturn(company);

    Company result = companyService.update(id, updatedData);

    assertThat(result.getName()).isEqualTo("Updated Corp");
    verify(mockCompanyRepository).findById(id);
    verify(mockCompanyRepository).save(company);
  }

  @Test
  void shouldThrowNotFoundWhenUpdateNonExistentCompany() {
    Company updatedData = new Company("Updated Corp", "UpdatedIndustry");
    when(mockCompanyRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> companyService.update(id, updatedData))
        .isInstanceOf(ResponseStatusException.class)
        .extracting("status")
        .isEqualTo(HttpStatus.NOT_FOUND);

    verify(mockCompanyRepository).findById(id);
    verify(mockCompanyRepository, never()).save(any(Company.class));
  }

  @Test
  void shouldDeleteCompanyWhenExists() {
    when(mockCompanyRepository.findById(id)).thenReturn(Optional.of(company));
    doNothing().when(mockCompanyRepository).deleteById(id);

    companyService.delete(id);

    verify(mockCompanyRepository).findById(id);
    verify(mockCompanyRepository).deleteById(id);
  }

  @Test
  void shouldThrowNotFoundWhenDeleteNonExistentCompany() {
    when(mockCompanyRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> companyService.delete(id))
        .isInstanceOf(ResponseStatusException.class)
        .extracting("status")
        .isEqualTo(HttpStatus.NOT_FOUND);

    verify(mockCompanyRepository).findById(id);
    verify(mockCompanyRepository, never()).deleteById(any(UUID.class));
  }

}
