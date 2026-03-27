package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.DealStatus;

public interface DealRepository {
  void save(Deal deal);

  Optional<Deal> findById(UUID id);

  List<Deal> findAll();

  List<Deal> findByStatus(DealStatus status);

  void deleteById (UUID id);
}
