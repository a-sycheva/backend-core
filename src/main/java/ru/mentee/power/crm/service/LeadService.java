package ru.mentee.power.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
public class LeadService {
  private static final Logger LOG = LoggerFactory.getLogger(LeadService.class);
  private final LeadRepository repository;

  public LeadService(LeadRepository repository) {
    this.repository = repository;
    LOG.info("LeadService constructor called");
  }

  @PostConstruct
  void init() {
    LOG.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
  }

  public Lead addLead(String name, String email, String company, LeadStatus status) {

    Optional<Lead> existing = repository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    Lead lead = new Lead(
        name,
        email,
        company,
        status
    );

    return repository.save(lead);
  }

  public Lead addLead(String email, String company, LeadStatus status) {

    Optional<Lead> existing = repository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    Lead lead = new Lead(
        email,
        company,
        status
    );

    return repository.save(lead);
  }

  public Lead update(UUID id, Lead updatedLead) {

    Optional<Lead> existing = repository.findById(id);
    if (existing.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Cannot find lead with id " + id);
    }

    existing.get().setName(updatedLead.name());
    existing.get().setEmail(updatedLead.email());
    existing.get().setCompany(updatedLead.company());
    existing.get().setStatus(updatedLead.status());

    return repository.save(existing.get());
  }

  public void delete(UUID id) {
    if (repository.findById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Lead with id = " + id + "not exists!");
    } else {
      repository.deleteById(id);
    }
  }

  public List<Lead> findAll() {
    return repository.findAll();
  }

  public Optional<Lead> findById(UUID id) {
    return repository.findById(id);
  }

  public Optional<Lead> findByEmail(String email) {
    return  repository.findByEmail(email);
  }

  public List<Lead> findByStatus(LeadStatus status) {
    return  repository.findAll().stream()
        .filter(lead -> lead.status().equals(status))
        .collect(Collectors.toList());
  }

  public List<Lead> findLeads(String name, String email, String company, LeadStatus status) {
    Stream<Lead> stream = repository.findAll().stream();
    if (name != null && !name.isBlank()) {
      stream = stream.filter(lead -> lead.name().toLowerCase().contains(name.toLowerCase()));
    }
    if (email != null && !email.isBlank()) {
      stream = stream.filter(lead -> lead.email().toLowerCase().contains(email.toLowerCase()));
    }
    if (company != null && !company.isBlank()) {
      stream = stream.filter(lead -> lead.company().toLowerCase().contains(company.toLowerCase()));
    }
    if (status != null) {
      stream = stream.filter(lead -> lead.status().equals(status));
    }
    return stream.collect(Collectors.toList());
  }
}