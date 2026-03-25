package ru.mentee.power.crm.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class DealTest {
  UUID leadId = UUID.randomUUID();
  BigDecimal amount = new BigDecimal(100_000.00);

  Deal deal = new Deal(leadId, amount);

  @Test
  void shouldCreateDealWithNewStatus() {
    assertThat(deal.getId()).isNotNull();
    assertThat(deal.getLeadId()).isEqualTo(leadId);
    assertThat(deal.getAmount()).isEqualTo(amount);
    assertThat(deal.getStatus()).isEqualTo(DealStatus.NEW);
    assertThat(deal.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldTransitionToValidStatus() {
    deal.transitionTo(DealStatus.QUALIFIED);
    assertThat(deal.getStatus()).isEqualTo(DealStatus.QUALIFIED);
  }

  @Test
  void shouldThrowExceptionWhenTransitionInvalid() {

    Deal wonDeal = new Deal(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.valueOf(100_000),
        DealStatus.WON, LocalDateTime.now());

    assertThatThrownBy(() -> wonDeal.transitionTo(DealStatus.NEW))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Cannot transition from " + wonDeal.getStatus()
            + " to " + DealStatus.NEW);
  }

  @Test
  void shouldReturnFalseWhenEqualsCalledWithNull() {
    assertThat(deal.equals(null)).isFalse();
  }

  @Test
  void shouldBeEqualsAndHasSameHashCodeWhenDealsHasSimilarIds() {
    Deal anotherDeal = new Deal(deal.getId(), UUID.randomUUID(), BigDecimal.valueOf(100_000),
        DealStatus.WON, LocalDateTime.now());

    assertThat(anotherDeal).isEqualTo(deal);
    assertThat(anotherDeal).hasSameHashCodeAs(deal);
  }

  @Test
  void shouldNotBeEqualsWhenDealsHasDifferentIds() {
    Deal anotherDeal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(20_000));
    assertThat(anotherDeal).isNotEqualTo(deal);
  }

}