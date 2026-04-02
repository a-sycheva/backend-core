package ru.mentee.power.crm.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
public class LeadProcessor {
  private final LeadRepository leadRepository;

  public LeadProcessor(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public String processSingleLead(UUID id) {
    String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
    updateSingleLead(id);
    return transactionName;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public String processSingleLeadWithRequired(UUID id) {
    String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
    updateSingleLead(id);
    return transactionName;
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public String processSingleLeadWithMandatory(UUID id) {
    String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
    updateSingleLead(id);
    return transactionName;
  }

  private void updateSingleLead(UUID id) {
    if (leadRepository.existsById(id)) {
      leadRepository.findById(id).get().setStatus(LeadStatus.CONTACTED);
    } else {
      throw new IllegalArgumentException(); //ошибка для rollback
    }
  }
}
