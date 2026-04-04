package ru.mentee.power.crm.service;

import static java.lang.Thread.sleep;

import java.util.UUID;

import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@Service
public class LeadLockingService {

  private final LeadRepository leadRepository;

  public LeadLockingService(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  // Критическая операция с pessimistic lock
  @Transactional
  public Lead convertLeadToDealWithLock(UUID leadId, String newStatus) {
    // Блокируем Lead эксклюзивно до конца транзакции
    Lead lead = leadRepository.findByIdForUpdate(leadId)
        .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

    // Здесь могла бы быть сложная бизнес-логика конверсии:
    // - создание Deal
    // - обновление статуса Lead
    // - отправка уведомлений
    // Другие транзакции ЖДУТ завершения этой операции

    lead.setStatus(LeadStatus.valueOf(newStatus));
    return leadRepository.save(lead);
  }

  // Обычное обновление с optimistic lock (через @Version)
  @Transactional
  public Lead updateLeadStatusOptimistic(UUID leadId, String newStatus) {
    Lead lead = leadRepository.findById(leadId)
        .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

    // Блокировки НЕТ — другие транзакции могут читать и изменять
    // При сохранении JPA проверит version и выбросит OptimisticLockException если конфликт

    try { //задержка для увеличения шанса конфликта
      sleep(50);
    } catch (InterruptedException anotherE) {
      Thread.currentThread().interrupt();
    }

    lead.setStatus(LeadStatus.valueOf(newStatus));
    return leadRepository.save(lead);
    // UPDATE leads SET status=?, version=version+1 WHERE id=? AND version=?
  }

  @Transactional
  public Lead updateWithRetry(UUID leadId, String newStatus) {
    int maxRetries = 3;
    int attempt = 0;
    while (attempt < maxRetries) {
      try {
        return updateLeadStatusOptimistic(leadId, newStatus);
      } catch (OptimisticLockException e) {
        attempt++;

        if (attempt >= maxRetries) {
          throw new RuntimeException(
              "Не удалось обновить лида после " + maxRetries
                  + " попыток", e);
        }

        // задержка перед повторной попыткой
        try {
          sleep(50);
        } catch (InterruptedException anotherE) {
          Thread.currentThread().interrupt();
        }

      }
    }
    throw new RuntimeException("Цикл заверщен без возврата значения");
  }

  @Transactional
  public void blockLeadsInOrder(UUID leadId1, UUID leadId2) throws InterruptedException {
    // Блокируем первый
    Lead lead1 = leadRepository.findByIdForUpdate(leadId1).get();

    sleep(50);  // Даем второму потоку время заблокировать второй лид

    // Блокируем второй
    Lead lead2 = leadRepository.findByIdForUpdate(leadId2).get();
  }
}