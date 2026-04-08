package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
@ActiveProfiles("test")
public class LeadDedlockServiceTest {

  @Autowired
  private LeadLockingService leadLockingService;

  @Autowired
  private LeadRepository leadRepository;

  @Test
  void shouldTrowExceptionWhenDeadlock() {
    Lead firstLead = new Lead("Patrick", "optimistic@test.com", LeadStatus.NEW);
    firstLead = leadRepository.save(firstLead);
    UUID firstLeadId = firstLead.getId();

    Lead secondLead = new Lead("Branon", "temperance@test.com", LeadStatus.NEW);
    secondLead = leadRepository.save(secondLead);
    UUID secondLeadId = secondLead.getId();

    ExecutorService executor = Executors.newFixedThreadPool(2);

    CountDownLatch startLatch = new CountDownLatch(1);


    Future<?> task1 = executor.submit(() -> {
      startLatch.await();
      leadLockingService.blockLeadsInOrder(firstLeadId, secondLeadId);
      return null;
    });

    Future<?> task2 = executor.submit(() -> {
      startLatch.await();
      leadLockingService.blockLeadsInOrder(secondLeadId, firstLeadId);
      return null;
    });

    startLatch.countDown();
    // Then: Одна транзакция должна выбросить CannotAcquireLockException
    boolean exceptionThrown = false;
    try {
      task1.get(5, TimeUnit.SECONDS);
      task2.get(5, TimeUnit.SECONDS);
    } catch (Exception e) {
      // Одна из транзакций должна выбросить OptimisticLockException
      assertThat(e.getCause())
          .isInstanceOfAny(CannotAcquireLockException.class);
      exceptionThrown = true;
    }

    assertThat(exceptionThrown).isTrue();
    executor.shutdown();
  }

}
