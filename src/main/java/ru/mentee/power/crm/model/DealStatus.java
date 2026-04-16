package ru.mentee.power.crm.model;

import java.util.Map;
import java.util.Set;

public enum DealStatus {
  NEW, // начальное состояние
  QUALIFIED, // известны потребность, бюджет, отв. лица
  PROPOSAL_SENT, // отправлено комм. предложение
  NEGOTIATION, // переговоры
  WON, // выиграна
  LOST; // проиграна/клиент ушел и т.д.

  private static final Map<DealStatus, Set<DealStatus>> VALID_TRANSITIONS =
      Map.of(
          NEW,
          Set.of(QUALIFIED, LOST),
          QUALIFIED,
          Set.of(PROPOSAL_SENT, LOST),
          PROPOSAL_SENT,
          Set.of(NEGOTIATION, LOST),
          NEGOTIATION,
          Set.of(WON, LOST),
          WON,
          Set.of(),
          LOST,
          Set.of());

  public boolean canTransition(DealStatus target) {
    return VALID_TRANSITIONS.get(this).contains(target);
  }
}
