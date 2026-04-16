package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.model.Product;

public interface ProductJpaRepository extends JpaRepository<Product, UUID> {

  Optional<Product> findBySku(String sku);

  List<Product> findByActiveTrue();
}
