package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.DealStatus;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.LeadRepository;

@ExtendWith(MockitoExtension.class)
class DealServiceMockTest {
  Lead defLead;
  Deal defDeal;

  @Mock DealRepository mockDealRepository;

  @Mock LeadRepository mockLeadRepository;

  DealService dealService;

  @BeforeEach
  void setUp() {
    dealService = new DealService(mockDealRepository);
    defLead =
        new Lead(
            UUID.randomUUID(),
            "test@example.ru",
            new Company("Test Corp", "TestIndustry"),
            LeadStatus.NEW);
    defDeal =
        new Deal(
            defLead.id(),
            defLead.id(),
            BigDecimal.valueOf(10_000),
            DealStatus.NEW,
            LocalDateTime.now());
  }

  @Test
  void shouldChangeStatusWhenLeadExists() {
    when(mockDealRepository.findById(any(UUID.class))).thenReturn(Optional.of(defDeal));

    dealService.transitionDealStatus(defDeal.getId(), DealStatus.QUALIFIED);

    assertThat(defDeal.getStatus()).isEqualTo(DealStatus.QUALIFIED);
    verify(mockDealRepository).save(defDeal);
  }

  @Test
  void shouldThrowExceptionWhenTransitionCalledWithNonExistedLead() {
    when(mockDealRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThatThrownBy(
            () -> dealService.transitionDealStatus(defDeal.getId(), DealStatus.QUALIFIED))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void shouldReturnAllDealsWhenCalled() {

    Deal secondDeal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(20_000));
    Deal thirdDeal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(30_000));

    List<Deal> deals = List.of(defDeal, secondDeal, thirdDeal);

    when(mockDealRepository.findAll()).thenReturn(deals);

    assertThat(dealService.getAllDeals()).containsExactlyInAnyOrder(defDeal, secondDeal, thirdDeal);
  }

  @ParameterizedTest
  @CsvSource({"NEW, 1", "QUALIFIED, 2", "PROPOSAL_SENT, 0", "NEGOTIATION, 1", "WON, 1", "LOST, 0"})
  void shouldReturnGroupedByStatusDealsWhenCalled(String statusName, Integer count) {
    DealStatus status = DealStatus.valueOf(statusName);
    Deal secondDeal =
        new Deal(
            UUID.randomUUID(),
            UUID.randomUUID(),
            BigDecimal.valueOf(20_000),
            DealStatus.QUALIFIED,
            LocalDateTime.now());
    Deal thirdDeal =
        new Deal(
            UUID.randomUUID(),
            UUID.randomUUID(),
            BigDecimal.valueOf(30_000),
            DealStatus.QUALIFIED,
            LocalDateTime.now());
    Deal fourtDeal =
        new Deal(
            UUID.randomUUID(),
            UUID.randomUUID(),
            BigDecimal.valueOf(30_000),
            DealStatus.NEGOTIATION,
            LocalDateTime.now());
    Deal fivesDeal =
        new Deal(
            UUID.randomUUID(),
            UUID.randomUUID(),
            BigDecimal.valueOf(30_000),
            DealStatus.WON,
            LocalDateTime.now());
    List<Deal> deals = List.of(defDeal, secondDeal, thirdDeal, fourtDeal, fivesDeal);
    when(mockDealRepository.findAll()).thenReturn(deals);

    Map<DealStatus, List<Deal>> groupedDeals = dealService.getDealsByStatusForKanban();

    if (count == 0) {
      assertThat(groupedDeals).doesNotContainKey(status);
    } else {
      assertThat(groupedDeals.get(status)).hasSize(count);
    }
  }

  @Test
  void shouldAddDealWhenAddDealCalled() {
    UUID secondDealId = UUID.randomUUID();
    Deal thirdDeal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(20_000));
    when(mockDealRepository.save(thirdDeal)).thenReturn(thirdDeal);

    dealService.addDeal(secondDealId, BigDecimal.valueOf(30_000));

    verify(mockDealRepository).save(any(Deal.class));
    assertThat(dealService.addDeal(thirdDeal)).isEqualTo(thirdDeal);
  }
}
