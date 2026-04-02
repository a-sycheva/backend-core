package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.DealStatus;

public interface DealRepository extends JpaRepository<Deal, UUID> {

  Optional<Deal> findById(UUID id);

  List<Deal> findAll();

  List<Deal> findByStatus(DealStatus status);

  void deleteById (UUID id);
}
