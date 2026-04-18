package ru.mentee.power.crm.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "companies")
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @EqualsAndHashCode.Include
  private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column private String industry;

  @OneToMany(mappedBy = "company", cascade = CascadeType.PERSIST)
  private List<Lead> leads = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_group_id")
  private CompanyGroup companyGroup;

  public Company(String name, String industry) {
    this.name = name;
    this.industry = industry;
  }

  public Company(String name, String industry, CompanyGroup companyGroup) {
    this.name = name;
    this.industry = industry;
    this.companyGroup = companyGroup;
  }

  public void addLead(Lead lead) {
    leads.add(lead);
    lead.setCompany(this);
  }

  public void removeLead(Lead lead) {
    leads.remove(lead);
    lead.setCompany(null);
  }
}
