package ru.mentee.power.crm.service;

import io.github.resilience4j.retry.annotation.Retry;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.spring.client.EmailValidationFeignClient;
import ru.mentee.power.crm.spring.client.EmailValidationResponse;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;
import ru.mentee.power.crm.spring.exception.EmailAlreadyExistsException;
import ru.mentee.power.crm.spring.exception.EntityNotFoundException;
import ru.mentee.power.crm.spring.mapper.LeadMapper;

@Service
public class LeadService {
  private static final Logger LOG = LoggerFactory.getLogger(LeadService.class);
  private final LeadRepository leadRepository;
  private final DealRepository dealRepository;
  private final EmailValidationFeignClient emailValidationClient;
  private final LeadMapper leadMapper;

  public LeadService(
      LeadRepository leadRepository,
      DealRepository dealRepository,
      EmailValidationFeignClient emailValidationClient,
      LeadMapper leadMapper) {
    this.leadRepository = leadRepository;
    this.dealRepository = dealRepository;
    this.emailValidationClient = emailValidationClient;
    this.leadMapper = leadMapper;

    LOG.info("LeadService constructor called");
  }

  @PostConstruct
  void init() {
    LOG.info("LeadService @PostConstruct init() called - Bean lifecycle phase");
  }

  @Retry(name = "email-validation", fallbackMethod = "addLeadFallback")
  public Lead addLead(String name, String email, Company company, LeadStatus status) {
    System.out.println("=== addLead called for: " + email + " ===");

    Lead lead = new Lead(name, email, company, status);

    EmailValidationResponse validation = emailValidationClient.validateEmail(lead.getEmail());

    if (!validation.valid()) {
      throw new IllegalArgumentException("Invalid email: " + validation.reason());
    }

    Optional<Lead> existing = leadRepository.findByEmail(email);
    if (existing.isPresent()) {
      throw new EmailAlreadyExistsException("Email already exists!");
    }

    return leadRepository.save(lead);
  }

  // Fallback метод — вызывается после исчерпания retry попыток
  public Lead addLeadFallback(
      String name, String email, Company company, LeadStatus status, Exception ex) {
    LOG.warn(
        "Email validation service unavailable after retries. "
            + "Creating lead without validation. Error: {}",
        ex.getMessage());

    //не создавать без валидации при невалидном email
    Optional<Lead> existing = leadRepository.findByEmail(email);
    if (existing.isPresent()) {
      throw new EmailAlreadyExistsException("Email already exists!");
    }
    // Graceful degradation: создаём лида без валидации
    // В production можно: 1) пометить для последующей проверки
    //                     2) отправить в очередь на валидацию
    //                     3) отклонить запрос (throw new ServiceUnavailableException)
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

  // методы REST-контроллера с кастомными исключениями
  public LeadResponse getLeadById(UUID id) {
    Lead lead =
        leadRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Lead", id.toString()));

    return leadMapper.toResponse(lead);
  }

  public LeadResponse updateLead(UUID id, UpdateLeadRequest request) {
    Lead lead =
        leadRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Lead", id.toString()));

    leadMapper.updateEntity(request, lead);

    return leadMapper.toResponse(leadRepository.save(lead));
  }

  public boolean deleteLead(UUID id) {
    if (!leadRepository.existsById(id)) {
      throw new EntityNotFoundException("Lead", id.toString());
    }
    leadRepository.deleteById(id);
    return true;
  }
}
