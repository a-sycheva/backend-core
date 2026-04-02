package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

@SpringBootTest
public class IsolationTest {
  @Autowired
  private LeadService leadService;

  @Autowired
  private LeadRepository leadRepository;

  @BeforeEach
  void setUp() {
    // Очищаем БД перед каждым тестом
    leadRepository.deleteAll();
  }

  @Test
    //для REPEATABLE_READ c параллельными транзакциями
    //тест READ_COMMITED с последовательным вызовов транзакций A-> B в LeadServiceTest
  void isolationRepeatableReadPreventsNonRepeatableRead() throws Exception {
    // Given: создаем лида
    Lead lead = new Lead();
    lead.setName("John");
    lead.setEmail("john@test.ru");
    lead.setCompany("Company");
    lead.setStatus(LeadStatus.NEW);
    leadRepository.save(lead);
    UUID leadId = lead.getId();

    // When: читатель и писатель работают параллельно
    CompletableFuture<List<String>> reader = CompletableFuture.supplyAsync(() -> {
      try {
        return leadService.readLeadNameWithRepeatableRead(leadId);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    CompletableFuture<Void> writer = CompletableFuture.runAsync(() -> {
      leadService.updateLeadName(leadId, "Jane");
    });

    CompletableFuture.allOf(reader, writer).join();

    // Then: при REPEATABLE_READ читатель НЕ видит изменение
    List<String> results = reader.get();
    assertThat(results).containsExactly("John", "John");
  }

}
