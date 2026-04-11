package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.DealStatus;

public interface DealRepository extends JpaRepository<Deal, UUID> {

  Optional<Deal> findById(UUID id);

  List<Deal> findAll();

  List<Deal> findByStatus(DealStatus status);

  @EntityGraph(attributePaths = {"dealProducts", "dealProducts.product"})
  @Query("SELECT d FROM Deal d WHERE d.id = :id")
  Optional<Deal> findDealWithProducts(UUID id);

  void deleteById (UUID id);
}
