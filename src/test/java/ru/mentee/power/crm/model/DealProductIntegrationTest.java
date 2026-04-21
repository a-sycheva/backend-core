package ru.mentee.power.crm.model;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.repository.DealRepository;
import ru.mentee.power.crm.repository.ProductRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DealProductIntegrationTest {
  @PersistenceContext private EntityManager entityManager;

  @Autowired private DealRepository dealRepository;

  @Autowired private ProductRepository productRepository;

  @Test
  void testSaveDealWithProducts() {
    Deal deal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(100_000));

    Product firstProduct = new Product();
    firstProduct.setName("Консультация по архитектуре");
    firstProduct.setSku("CONSULT-ARCH-001");
    firstProduct.setPrice(new BigDecimal("50000.00"));
    firstProduct.setActive(true);

    Product secondProduct = new Product();
    secondProduct.setName("Консультация по бизнес-аналитике");
    secondProduct.setSku("CONSULT-BI-001");
    secondProduct.setPrice(new BigDecimal("70000.00"));
    secondProduct.setActive(false);

    productRepository.save(firstProduct);
    productRepository.save(secondProduct);

    DealProduct firstDealProduct = new DealProduct(deal, firstProduct, 2, firstProduct.getPrice());
    DealProduct secondDealProduct =
        new DealProduct(deal, secondProduct, 1, secondProduct.getPrice());

    deal.addDealProduct(firstDealProduct);
    deal.addDealProduct(secondDealProduct);

    dealRepository.save(deal);

    Deal foundDeal = dealRepository.findById(deal.getId()).get();

    assertThat(foundDeal.getDealProducts()).hasSize(2);

    assertThat(foundDeal.getDealProducts().get(0).getQuantity()).isEqualTo(2);
    assertThat(foundDeal.getDealProducts().get(0).getUnitPrice())
        .isEqualTo(new BigDecimal("50000.00"));

    assertThat(foundDeal.getDealProducts().get(1).getQuantity()).isEqualTo(1);
    assertThat(foundDeal.getDealProducts().get(1).getUnitPrice())
        .isEqualTo(new BigDecimal("70000.00"));
  }

  @Test
  void testEntityGraphSolvesNPlusOne() {
    Deal deal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(100_000));

    Product firstProduct = new Product();
    firstProduct.setName("Консультация по архитектуре");
    firstProduct.setSku("CONSULT-ARCH-001");
    firstProduct.setPrice(new BigDecimal("50000.00"));
    firstProduct.setActive(true);

    Product secondProduct = new Product();
    secondProduct.setName("Консультация по бизнес-аналитике");
    secondProduct.setSku("CONSULT-BI-001");
    secondProduct.setPrice(new BigDecimal("70000.00"));
    secondProduct.setActive(true);

    Product thirdProduct = new Product();
    thirdProduct.setName("ноутбук DELL");
    thirdProduct.setSku("LAPTOP-001");
    thirdProduct.setPrice(new BigDecimal("90000.00"));
    thirdProduct.setActive(true);

    productRepository.save(firstProduct);
    productRepository.save(secondProduct);
    productRepository.save(thirdProduct);

    DealProduct firstDealProduct = new DealProduct(deal, firstProduct, 1, firstProduct.getPrice());
    DealProduct secondDealProduct =
        new DealProduct(deal, secondProduct, 1, secondProduct.getPrice());
    DealProduct thirdDealProduct = new DealProduct(deal, thirdProduct, 1, thirdProduct.getPrice());

    deal.addDealProduct(firstDealProduct);
    deal.addDealProduct(secondDealProduct);
    deal.addDealProduct(thirdDealProduct);

    dealRepository.save(deal);

    entityManager.flush();
    entityManager.clear();

    // без EntityGraph
    // первый - SELECT deal
    Deal foundDeal = dealRepository.findById(deal.getId()).get();

    // второй - SELECT deal_product
    for (DealProduct dp : foundDeal.getDealProducts()) {
      // еще по одному на каждом шаге цикла - SELECT products
      System.out.println(dp.getProduct().getName());
    }
    // итого 5 SELECT-запросов

    entityManager.flush();
    entityManager.clear();

    // с EntityGraph
    // один SELECT deal с двумя LEFT JOIN
    Deal anothetFoundDeal = dealRepository.findDealWithProducts(deal.getId()).get();

    // второй - SELECT deal_product
    for (DealProduct dp : anothetFoundDeal.getDealProducts()) {
      System.out.println(dp.getProduct().getName());
    }
    // итого 1 SELECT-запрос

    entityManager.clear();

    assertThat(foundDeal.getDealProducts()).hasSize(3);
    assertThat(anothetFoundDeal.getDealProducts()).hasSize(3);

    assertThat(foundDeal.getDealProducts()).containsAll(anothetFoundDeal.getDealProducts());
    assertThat(anothetFoundDeal.getDealProducts()).containsAll(foundDeal.getDealProducts());
  }
}
