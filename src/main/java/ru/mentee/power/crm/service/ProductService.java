package ru.mentee.power.crm.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.model.Product;
import ru.mentee.power.crm.repository.ProductJpaRepository;

@Service
@AllArgsConstructor
public class ProductService {
  private final ProductJpaRepository productRepository;

  public List<Product> findAll() {
    return productRepository.findAll();
  }

  public Product addProduct(String name, String sku, BigDecimal price, Boolean active) {
    Optional<Product> existing = productRepository.findBySku(sku);
    if (existing.isPresent()) {
      throw new IllegalStateException("Such product already exists");
    }
    return productRepository.save(new Product(name, sku, price, active));
  }
}
