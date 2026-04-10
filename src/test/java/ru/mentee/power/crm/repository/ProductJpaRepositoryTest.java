package ru.mentee.power.crm.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.model.Product;

@DataJpaTest
@ActiveProfiles("test")
class ProductJpaRepositoryTest {
  @Autowired
  private ProductJpaRepository productRepository;

  @Test
  void shouldSaveAndFindProductWhenValidData() {
    Product product = new Product();
    product.setName("Консультация по архитектуре");
    product.setSku("CONSULT-ARCH-001");
    product.setPrice(new BigDecimal("50000.00"));
    product.setActive(true);

    Product saved = productRepository.save(product);

    assertThat(saved.getId()).isNotNull();
    Optional<Product> found = productRepository.findById(saved.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getSku()).isEqualTo("CONSULT-ARCH-001");
  }

  @Test
  void shouldReturnProductWhenFindBySku() {
    Product product = new Product();
    product.setName("Ноутбук");
    product.setSku("LAPTOP-001");
    product.setPrice(new BigDecimal("50000.00"));
    product.setActive(true);

    productRepository.save(product);

    Optional<Product> foundProduct = productRepository.findBySku("LAPTOP-001");

    assertThat(foundProduct).isPresent();
    assertThat(foundProduct.get().getSku()).isEqualTo("LAPTOP-001");
  }

  @Test
  void shouldReturnListOfActiveProductsWhenFindByActiveTrue() {
    Product firstProduct = new Product();
    firstProduct.setName("Консультация по архитектуре");
    firstProduct.setSku("CONSULT-ARCH-001");
    firstProduct.setPrice(new BigDecimal("50000.00"));
    firstProduct.setActive(true);

    productRepository.save(firstProduct);

    Product secondProduct = new Product();
    secondProduct.setName("Ноутбук");
    secondProduct.setSku("LAPTOP-001");
    secondProduct.setPrice(new BigDecimal("25000.00"));
    secondProduct.setActive(false);

    productRepository.save(secondProduct);

    Product thirdProduct = new Product();
    thirdProduct.setName("Смартфон");
    thirdProduct.setSku("SMARTPHONE-001");
    thirdProduct.setPrice(new BigDecimal("10000.00"));
    thirdProduct.setActive(true);

    productRepository.save(thirdProduct);

    List<Product> activeProducts = productRepository.findByActiveTrue();

    assertThat(activeProducts).hasSize(2)
        .containsExactlyInAnyOrder(firstProduct, thirdProduct);
  }

  @Test
  void  shouldThrowExceptionWhenSaveProductWithSameSku() {
    Product firstProduct = new Product();
    firstProduct.setName("Консультация по архитектуре");
    firstProduct.setSku("CONSULT-ARCH-001");
    firstProduct.setPrice(new BigDecimal("50000.00"));
    firstProduct.setActive(true);

    productRepository.saveAndFlush(firstProduct);

    Product secondProduct = new Product();
    secondProduct.setName("Консультация по бизнес-аналитике");
    secondProduct.setSku("CONSULT-ARCH-001");
    secondProduct.setPrice(new BigDecimal("50000.00"));
    secondProduct.setActive(false);

    assertThatThrownBy(() -> productRepository.saveAndFlush(secondProduct))
        .isInstanceOf(DataIntegrityViolationException.class);
  }
}