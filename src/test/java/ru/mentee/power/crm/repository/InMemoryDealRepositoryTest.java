package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.DealStatus;

class InMemoryDealRepositoryTest {
  InMemoryDealRepository repository;
  Deal deal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(10_000));

  @BeforeEach
  void setUp() {
    repository = new InMemoryDealRepository();
  }

  @Test
  void shouldSaveDealWhenItCalled() {
    repository.save(deal);
    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldFindByIdWhenItCalled() {
    repository.save(deal);
    Deal secondDeal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(10_000));
    repository.save(secondDeal);

    Deal foundDeal = repository.findById(deal.getId()).get();

    assertThat(deal).isEqualTo(foundDeal);
  }

  @Test
  void shouldFindAllWhenItCalled() {
    repository.save(deal);
    Deal secondDeal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(10_000));
    repository.save(secondDeal);
    Deal thirdDeal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(10_000));
    repository.save(thirdDeal);

    List<Deal> deals = repository.findAll();

    assertThat(deals).hasSize(3)
        .containsExactlyInAnyOrder(deal, secondDeal, thirdDeal);
  }

  @Test
  void shouldFindByStatusWhenItCalled() {
    repository.save(deal);
    Deal secondDeal = new Deal(UUID.randomUUID(), UUID.randomUUID(),
        BigDecimal.valueOf(10_000), DealStatus.NEGOTIATION, LocalDateTime.now());
    repository.save(secondDeal);
    Deal thirdDeal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(10_000));
    repository.save(thirdDeal);

    List<Deal> deals = repository.findByStatus(DealStatus.NEW);
    assertThat(deals).hasSize(2)
        .containsExactlyInAnyOrder(deal, thirdDeal);
  }

  @Test
  void shouldDeleteWhenItCalled() {
    repository.save(deal);
    Deal seconcDeal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(10_000));
    repository.save(seconcDeal);

    repository.deleteById(deal.getId());
    List<Deal> deals = repository.findAll();

    assertThat(deals).hasSize(1)
        .contains(seconcDeal);
  }
}