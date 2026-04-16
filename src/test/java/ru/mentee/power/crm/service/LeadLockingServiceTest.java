package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
@ActiveProfiles("test")
// deadlock-тест вынесен отдельно в LeadDedlockServiceTest
class LeadLockingServiceTest {

  @Autowired private LeadLockingService leadLockingService;

  @Autowired private LeadRepository leadRepository;

  @AfterEach
  void tearDown() {
    leadRepository.deleteAll();
  }

  @Test
  void shouldPreventLostUpdateWhenPessimisticLockUsed() throws Exception {
    // Given: Lead с начальным статусом
    Lead lead = new Lead("Patrick", "concurrent@test.com", LeadStatus.NEW);
    lead = leadRepository.save(lead);
    UUID leadId = lead.getId();

    // When: Два потока одновременно обновляют Lead с pessimistic lock
    ExecutorService executor = Executors.newFixedThreadPool(2);

    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(2);

    Future<String> task1 =
        executor.submit(
            () -> {
              startLatch.await(); // Синхронизируем старт
              Lead updated = leadLockingService.convertLeadToDealWithLock(leadId, "CONTACTED");
              doneLatch.countDown();
              return updated.getStatus().toString();
            });

    Future<String> task2 =
        executor.submit(
            () -> {
              startLatch.await();
              Lead updated = leadLockingService.convertLeadToDealWithLock(leadId, "QUALIFIED");
              doneLatch.countDown();
              return updated.getStatus().toString();
            });

    startLatch.countDown(); // Запускаем оба потока одновременно
    doneLatch.await(10, TimeUnit.SECONDS); // Ждём завершения

    // Then: Оба обновления успешны, вторая транзакция ждала первую
    String status1 = task1.get();
    String status2 = task2.get();

    assertThat(status1).isIn("CONTACTED", "QUALIFIED");
    assertThat(status2).isIn("CONTACTED", "QUALIFIED");
    assertThat(status1).isNotEqualTo(status2); // Разные статусы (не должны быть)

    // Финальный статус — последняя commit'нутая транзакция
    Lead finalLead = leadRepository.findById(leadId).orElseThrow();
    assertThat(finalLead.getStatus().toString()).isIn("CONTACTED", "QUALIFIED");

    executor.shutdown();
  }

  @Test
  void shouldThrowOptimisticLockExceptionWhenConcurrentUpdateWithoutLock() throws Exception {
    // Given: Lead с optimistic locking через @Version
    Lead lead = new Lead("Patrick", "optimistic@test.com", LeadStatus.NEW);
    lead = leadRepository.save(lead);
    UUID leadId = lead.getId();

    // When: Два потока одновременно обновляют БЕЗ pessimistic lock
    ExecutorService executor = Executors.newFixedThreadPool(2);

    CountDownLatch startLatch = new CountDownLatch(1);

    Future<?> task1 =
        executor.submit(
            () -> {
              startLatch.await();
              leadLockingService.updateLeadStatusOptimistic(leadId, "CONTACTED");
              return null;
            });

    Future<?> task2 =
        executor.submit(
            () -> {
              startLatch.await();
              Thread.sleep(50);
              leadLockingService.updateLeadStatusOptimistic(leadId, "QUALIFIED");
              return null;
            });

    startLatch.countDown();

    // Then: Одна транзакция успешна, вторая выбрасывает OptimisticLockException
    boolean exceptionThrown = false;
    try {
      task1.get(5, TimeUnit.SECONDS);
      task2.get(5, TimeUnit.SECONDS);
    } catch (ExecutionException e) {
      // Одна из транзакций должна выбросить OptimisticLockException
      assertThat(e.getCause()).isInstanceOfAny(ObjectOptimisticLockingFailureException.class);
      exceptionThrown = true;
    }

    assertThat(exceptionThrown).isTrue();
    executor.shutdown();
  }
}
