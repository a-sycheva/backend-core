package ru.mentee.power.crm.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class DealStatusTest {

  @ParameterizedTest
  @CsvSource({
      "NEW, QUALIFIED, true",
      "NEW, LOST, true",
      "NEW, WON, false",
      "QUALIFIED, PROPOSAL_SENT, true",
      "PROPOSAL_SENT, NEGOTIATION, true",
      "NEGOTIATION, WON, true",
      "NEGOTIATION, LOST, true",
      "WON, NEW, false",
      "LOST, QUALIFIED, false"
  })
  void shouldValidateTransitions(DealStatus from, DealStatus to, boolean expected) {
    assertThat(from.canTransition(to)).isEqualTo(expected);
  }

  @ParameterizedTest
  @EnumSource (DealStatus.class)
  void terminalStatesShouldNotAllowAnyTransitions(DealStatus to) {
    assertThat(DealStatus.LOST.canTransition(to)).isFalse();
  }

}