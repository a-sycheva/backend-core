package ru.mentee.power.crm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.DealStatus;
import ru.mentee.power.crm.repository.DealRepository;

@Service
@RequiredArgsConstructor
public class DealService {
  private final DealRepository dealRepository;

  public Deal transitionDealStatus (UUID dealId, DealStatus newStatus) {
    Optional<Deal> deal = dealRepository.findById(dealId);
    if (deal.isEmpty()) {
      throw new IllegalArgumentException("Deal not found: " + dealId);
    } else {
      deal.get().transitionTo(newStatus);
      dealRepository.save(deal.get());
      return deal.get();
    }
  }

  public List<Deal> getAllDeals() {
    return dealRepository.findAll();
  }

  public Map<DealStatus, List<Deal>> getDealsByStatusForKanban() {
    return dealRepository.findAll().stream()
        .collect(Collectors.groupingBy(Deal::getStatus));
  }

  public Deal addDeal(UUID leadId, BigDecimal amount) {
    return dealRepository.save(new Deal(leadId, amount));
  }

  public Deal addDeal(Deal deal) {
    return dealRepository.save(deal);
  }
}
