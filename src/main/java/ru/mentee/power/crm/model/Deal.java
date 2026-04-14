package ru.mentee.power.crm.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "deals")
public class Deal {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID leadId;

  @Column(nullable = false)
  private  BigDecimal amount;

  @Column(name = "stage", nullable = false)
  @Enumerated(EnumType.STRING)
  private DealStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
  List<DealProduct> dealProducts = new ArrayList<>();

  public Deal() {

  }

  public Deal (UUID leadId, BigDecimal amount) {
    this.leadId = Objects.requireNonNull(leadId, "LeadId must not be null");
    this.amount = Objects.requireNonNull(amount, "amount must not be null");;
    this.status = DealStatus.NEW;
    this.createdAt = LocalDateTime.now();
  }

  // Конструктор для восстановления из БД (Sprint 7)
  public Deal(UUID id, UUID leadId, BigDecimal amount, DealStatus status, LocalDateTime createdAt) {
    this.id = id;
    this.leadId = leadId;
    this.amount = amount;
    this.status = status;
    this.createdAt = createdAt;
  }

  public void addDealProduct(DealProduct dealProduct) {
    dealProducts.add(dealProduct);
    dealProduct.setDeal(this);
  }

  public void removeDealProduct(DealProduct dealProduct) {
    dealProducts.remove(dealProduct);
    dealProduct.setDeal(null);
  }

  public void transitionTo(DealStatus newStatus) {
    if (this.status.canTransition(newStatus)) {
      this.status = newStatus;
    } else {
      throw new IllegalStateException("Cannot transition from "
          + this.status + " to " + newStatus);
    }
  }

  public UUID getId() {
    return id;
  }

  public UUID getLeadId() {
    return leadId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public DealStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public List<DealProduct> getDealProducts() {
    return dealProducts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Deal deal = (Deal) o;
    return Objects.equals(id, deal.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
