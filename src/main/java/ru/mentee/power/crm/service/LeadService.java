package ru.mentee.power.crm.service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Company;
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
  private final LeadProcessor leadProcessor;

  public LeadService(
      LeadRepository leadRepository, DealRepository dealRepository, LeadProcessor leadProcessor) {
    this.leadRepository = leadRepository;
    this.dealRepository = dealRepository;
    this.leadProcessor = leadProcessor;

    LOG.info("LeadService constructor called");
  }

  @PostConstruct
  void init() {
    LOG.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
  }

  public Lead addLead(String name, String email, Company company, LeadStatus status) {

    Optional<Lead> existing = leadRepository.findByEmail(email);
    if (existing.isPresent()) {
      throw new IllegalStateException("Lead with email already exists: " + email);
    }

    Lead lead = new Lead(name, email, company, status);

    return leadRepository.save(lead);
  }

  public Optional<Lead> updateLead(UUID id, Lead updatedLead) {

    Optional<Lead> existing = leadRepository.findById(id);
    if (existing.isEmpty()) {
      return Optional.empty();
    }

    existing.get().setName(updatedLead.name());
    existing.get().setEmail(updatedLead.email());
    existing.get().setCompany(updatedLead.company());
    existing.get().setStatus(updatedLead.status());

    return Optional.of(leadRepository.save(existing.get()));
  }

  public Lead update(UUID id, Lead updatedLead) {

    Optional<Lead> existing = leadRepository.findById(id);
    if (existing.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot find lead with id " + id);
    }

    existing.get().setName(updatedLead.name());
    existing.get().setEmail(updatedLead.email());
    existing.get().setCompany(updatedLead.company());
    existing.get().setStatus(updatedLead.status());

    return leadRepository.save(existing.get());
  }

  public void delete(UUID id) {
    if (leadRepository.findById(id).isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "Lead with id = " + id + "not exists!");
    } else {
      leadRepository.deleteById(id);
    }
  }

  public boolean deleteLead(UUID id) {
    if (leadRepository.findById(id).isEmpty()) {
      return  false;
    }
    leadRepository.deleteById(id);
    return  true;
  }

  public List<Lead> findAll() {
    return leadRepository.findAll();
  }

  public Optional<Lead> findById(UUID id) {
    return leadRepository.findById(id);
  }

  public Optional<Lead> findByEmail(String email) {
    return leadRepository.findByEmail(email);
  }

  public List<Lead> findByStatuses(LeadStatus... statuses) {
    return leadRepository.findByStatusIn(List.of(statuses));
  }

  public List<Lead> findByStatus(LeadStatus status) {
    return leadRepository.findAll().stream()
        .filter(lead -> lead.status().equals(status))
        .collect(Collectors.toList());
  }

  public List<Lead> findLeads(String name, String email, String companyName, LeadStatus status) {
    Stream<Lead> stream = leadRepository.findAll().stream();
    if (name != null && !name.isBlank()) {
      stream = stream.filter(lead -> lead.name().toLowerCase().contains(name.toLowerCase()));
    }
    if (email != null && !email.isBlank()) {
      stream = stream.filter(lead -> lead.email().toLowerCase().contains(email.toLowerCase()));
    }
    if (companyName != null && !companyName.isBlank()) {
      stream =
          stream.filter(
              lead ->
                  lead.company() != null
                      && lead.company()
                          .getName()
                          .toLowerCase()
                          .contains(companyName.toLowerCase()));
    }
    if (status != null) {
      stream = stream.filter(lead -> lead.status().equals(status));
    }
    return stream.collect(Collectors.toList());
  }

  public Page<Lead> searchByCompany(Company company, int pageNumber, int pageSize) {
    PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
    return leadRepository.findByCompany(company, pageRequest);
  }

  public Page<Lead> getFirstPage(int pageSize) {
    PageRequest pageRequest = PageRequest.of(0, pageSize, Sort.by("createdAt").descending());
    return leadRepository.findAll(pageRequest);
  }

  @Transactional
  public int convertNewToContacted() {
    int updated = leadRepository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
    // логи
    System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
    return updated;
  }

  @Transactional
  public int archiveOldLeads(LeadStatus status) {
    return leadRepository.deleteByStatusBulk(status);
  }

  @Transactional
  public void convertLeadToDeal(UUID leadId, BigDecimal amount) {
    Lead lead =
        leadRepository
            .findById(leadId)
            .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));
    Deal deal = new Deal(leadId, amount);
    lead.setStatus(LeadStatus.CONTACTED);
    dealRepository.save(deal);
  }

  public String processLeads(List<UUID> ids) {
    String transactionName = "None";
    for (UUID id : ids) {
      try {
        transactionName = leadProcessor.processSingleLead(id);
      } catch (Exception e) {
        // Перехват исключения
        System.out.println("Failed to process lead: " + id);
      }
    }
    return transactionName;
  }

  // self-invocation
  public void processLeadsWithInvocationProblem(List<UUID> ids) {
    for (UUID id : ids) {
      try {
        this.processSingleLead(id);
      } catch (Exception e) {
        // Перехват исключения
        System.out.println("Failed to process lead: " + id);
      }
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  private void processSingleLead(UUID id) {
    if (leadRepository.existsById(id)) {
      leadRepository.findById(id).get().setStatus(LeadStatus.CONTACTED);
    } else {
      throw new IllegalArgumentException(); // ошибка для rollback
    }
  }

  public String processLeadsWithRequires(List<UUID> ids) {
    String transactionName = "none";
    for (UUID id : ids) {
      try {
        transactionName = leadProcessor.processSingleLeadWithRequired(id);
      } catch (Exception e) {
        // Перехват исключения
        System.out.println("Failed to process lead: " + id);
      }
    }

    return transactionName;
  }

  public String processLeadsWithMandatory(List<UUID> ids) {
    String transactionName = "none";
    for (UUID id : ids) {
      try {
        transactionName = leadProcessor.processSingleLeadWithMandatory(id);
      } catch (IllegalArgumentException e) {
        // Перехват исключения
        System.out.println("Failed to process lead: " + id);
      }
    }

    return transactionName;
  }

  // Транзакция A (читает) для последовательного вызова
  @Transactional(isolation = Isolation.READ_COMMITTED)
  public List<String> readThenWriteThenReadAgainWithReadCommitted(UUID leadId, String newName) {
    List<String> results = new ArrayList<>();

    // Транзакция A читает Lead (name = "John")
    Lead lead = leadRepository.findById(leadId).orElseThrow();
    results.add(lead.getName()); // "John"

    // Транзакция B обновляет Lead (name = "Jane") и commit
    updateLeadName(leadId, newName); // обновляет на "Jane"

    // Транзакция A читает Lead повторно
    // должны увидеть "Jane" при READ_COMMITTED
    lead = leadRepository.findById(leadId).orElseThrow();
    results.add(lead.getName());

    return results;
  }

  // Транзакция B (обновляет)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateLeadName(UUID leadId, String newName) {
    Lead lead = leadRepository.findById(leadId).orElseThrow();
    lead.setName(newName);
    // Транзакция Б завершается и КОММИТИТ
  }

  // Метод для REPEATABLE_READ в параллельном тесте
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public List<String> readLeadNameWithRepeatableRead(UUID leadId) throws InterruptedException {
    List<String> results = new ArrayList<>();

    Lead lead = leadRepository.findById(leadId).orElseThrow();
    results.add(lead.getName());

    Thread.sleep(100);

    lead = leadRepository.findById(leadId).orElseThrow();
    results.add(lead.getName());

    return results;
  }
}
