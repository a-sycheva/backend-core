package ru.mentee.power.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.repository.CompanyRepository;

@Service
@AllArgsConstructor
public class CompanyService {
  private final CompanyRepository companyRepository;

  public List<Company> findAll() {
    return companyRepository.findAll();
  }

  public Optional<Company> findById(UUID id) {
    return companyRepository.findById(id);
  }

  public List<Company> findCompanies(String name, String industry) {
    Stream<Company> stream = companyRepository.findAll().stream();
    if (name != null && !name.isBlank()) {
      stream = stream.filter(company -> company.getName()
          .toLowerCase().contains(name.toLowerCase()));
    }
    if (industry != null && !industry.isBlank()) {
      stream = stream.filter(company -> company.getIndustry()
          .toLowerCase().contains(industry.toLowerCase()));
    }
    return stream.collect(Collectors.toList());
  }

  public Company addCompany(String name, String industry) {
    Optional<Company> existing = companyRepository.findByName(name);
    if (existing.isPresent() && existing.get().getIndustry() == industry) {
      throw new IllegalStateException("Such company already exists");
    }
    return companyRepository.save(new Company(name, industry));
  }

  public Company update(UUID id, Company updatedCompany) {

    Optional<Company> existing = companyRepository.findById(id);
    if (existing.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Cannot find company with id " + id);
    }

    existing.get().setName(updatedCompany.getName());
    existing.get().setIndustry(updatedCompany.getIndustry());

    return companyRepository.save(existing.get());
  }

  public void delete(UUID id) {
    if (companyRepository.findById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Company with id = " + id + "not exists!");
    } else {
      companyRepository.deleteById(id);
    }
  }
}
