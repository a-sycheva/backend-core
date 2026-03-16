package ru.mentee.power.crm.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
public class LeadService {
  private static final Logger LOG = LoggerFactory.getLogger(LeadService.class);
  private final LeadRepository<Lead> repository;

  public LeadService(LeadRepository<Lead> repository) {
    this.repository = repository;
    LOG.info("LeadService constructor called");
  }

  @PostConstruct
  void init() {
    LOG.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
  }

  public Lead addLead(String email, String company, LeadStatus status) {

    Optional<Lead> existing = repository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    Lead lead = new Lead(
        UUID.randomUUID(),
        email,
        company,
        status
    );

    return repository.save(lead);
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
}