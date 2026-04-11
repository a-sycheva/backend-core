package ru.mentee.power.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentee.power.crm.model.Product;
import ru.mentee.power.crm.repository.ProductJpaRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceMockTest {

  private ProductService productService;

  @Mock
  private ProductJpaRepository productRepository;

  @BeforeEach
  void setUp() {
    productService = new ProductService(productRepository);
  }

  @Test
  void shouldFindAllProducts() {
    List<Product> expectedProducts = List.of(
        new Product("Первый продукт", "ПРОД-ТЕСТ-001",
            new BigDecimal("10000.00"), true),
        new Product("Второй продукт", "ПРОД-ТЕСТ-002",
            new BigDecimal("20000.00"), true)
    );
    when(productRepository.findAll()).thenReturn(expectedProducts);

    List<Product> result = productService.findAll();

    assertThat(result).hasSize(2);
    assertThat(result).isEqualTo(expectedProducts);
    verify(productRepository).findAll();
  }

  @Test
  void shouldAddProductWhenSkuDoesNotExist() {
    String name = "Первый продукт";
    String sku = "ПРОД-ТЕСТ-001";
    BigDecimal price = new BigDecimal("15000.00");
    Boolean active = true;
    Product savedProduct = new Product(name, sku, price, active);
    savedProduct.setId(UUID.randomUUID());
    when(productRepository.findBySku(sku)).thenReturn(Optional.empty());
    when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

    Product result = productService.addProduct(name, sku, price, active);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(name);
    assertThat(result.getSku()).isEqualTo(sku);
    assertThat(result.getPrice()).isEqualTo(price);
    assertThat(result.getActive()).isEqualTo(active);
    verify(productRepository).findBySku(sku);
    verify(productRepository).save(any(Product.class));
  }

  @Test
  void shouldThrowExceptionWhenAddProductWithExistingSku() {
    String name = "Существующий продукт";
    String sku = "ПРОД-ТЕСТ-001";
    BigDecimal price = new BigDecimal("15000.00");
    Boolean active = true;
    Product existingProduct = new Product(name, sku, price, true);
    existingProduct.setId(UUID.randomUUID());

    when(productRepository.findBySku(sku)).thenReturn(Optional.of(existingProduct));

    assertThatThrownBy(() -> productService.addProduct(name, sku, price, active))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Such product already exists");

    verify(productRepository).findBySku(sku);
    verify(productRepository, never()).save(any(Product.class));
  }
}
