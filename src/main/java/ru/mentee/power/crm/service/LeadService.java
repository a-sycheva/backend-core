package ru.mentee.power.crm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
public class LeadService {
  private static final Logger LOG = LoggerFactory.getLogger(LeadService.class);
  private final LeadRepository leadRepository;
  private final DealRepository dealRepository;

  public LeadService(LeadRepository leadRepository, DealRepository dealRepository) {
    this.leadRepository = leadRepository;
    this.dealRepository = dealRepository;

    LOG.info("LeadService constructor called");
  }

  @PostConstruct
  void init() {
    LOG.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
  }

  public Lead addLead(String name, String email, String company, LeadStatus status) {

    Optional<Lead> existing = leadRepository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    Lead lead = new Lead(
        name,
        email,
        company,
        status
    );

    return leadRepository.save(lead);
  }

  public Lead addLead(String email, String company, LeadStatus status) {

    Optional<Lead> existing = leadRepository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    Lead lead = new Lead(
        email,
        company,
        status
    );

    return leadRepository.save(lead);
  }

  public Lead update(UUID id, Lead updatedLead) {

    Optional<Lead> existing = leadRepository.findById(id);
    if (existing.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          "Cannot find lead with id " + id);
    }

    existing.get().setName(updatedLead.name());
    existing.get().setEmail(updatedLead.email());
    existing.get().setCompany(updatedLead.company());
    existing.get().setStatus(updatedLead.status());

    return leadRepository.save(existing.get());
  }

  public void delete(UUID id) {
    if (leadRepository.findById(id).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Lead with id = " + id + "not exists!");
    } else {
      leadRepository.deleteById(id);
    }
  }

  public List<Lead> findAll() {
    return leadRepository.findAll();
  }

  public Optional<Lead> findById(UUID id) {
    return leadRepository.findById(id);
  }

  public Optional<Lead> findByEmail(String email) {
    return  leadRepository.findByEmail(email);
  }

  public List<Lead> findByStatuses(LeadStatus... statuses) {
    return  leadRepository.findByStatusIn(List.of(statuses));
  }

  public List<Lead> findByStatus(LeadStatus status) {
    return  leadRepository.findAll().stream()
        .filter(lead -> lead.status().equals(status))
        .collect(Collectors.toList());
  }

  public List<Lead> findLeads(String name, String email, String company, LeadStatus status) {
    Stream<Lead> stream = leadRepository.findAll().stream();
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

  public Page<Lead> searchByCompany (String company, int pageNumber, int pageSize) {
    PageRequest pageRequest = PageRequest.of(
        pageNumber, pageSize);
    return leadRepository.findByCompany(company, pageRequest);
  }

  public Page<Lead> getFirstPage(int pageSize) {
    PageRequest pageRequest = PageRequest.of(
        0, pageSize, Sort.by("createdAt").descending());
    return leadRepository.findAll(pageRequest);
  }

  @Transactional
  public int convertNewToContacted() {
    int updated = leadRepository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
    //логи
    System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
    return updated;
  }

  @Transactional
  public int archiveOldLeads(LeadStatus status) {
    return leadRepository.deleteByStatusBulk(status);
  }

  @Transactional
  public void convertLeadToDeal (UUID leadId, BigDecimal amount) {
    if (leadRepository.existsById(leadId)) {
      Deal deal = new Deal(leadId, amount);
      dealRepository.save(deal);
    } else {
      throw new IllegalArgumentException("Lead not found: " + leadId);
    }
  }
}