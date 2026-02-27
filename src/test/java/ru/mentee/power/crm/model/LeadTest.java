package ru.mentee.power.crm.model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class LeadTest {
  @Test
  void shouldGenerateSimilarHashWhenLeadsHasSimilarId() {
    Lead firstLead = new Lead("1", "test1@example.ru", "+71234567890", "FirstCompany", "NEW");
    Lead secondLead = new Lead("1", "test2@example.ru", "+70987654321", "SecondCompany", "NEW");

    assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
  }

  @Test
  void shouldReturnFalseWhenEqualsNullOrNoninstanceObjects() {
    Lead firstLead = new Lead("1", "test1@example.ru", "+71234567890", "FirstCompany", "NEW");
    String someString = "some string";

    assertThat(firstLead.equals(null)).isFalse();
    assertThat(firstLead.equals(someString)).isFalse();
  }

  @Test
  void shouldReturnTrueWhenEqualsItself() {
    Lead lead = new Lead("1", "test1@example.ru", "+71234567890", "FirstCompany", "NEW");

    assertThat(lead.equals(lead)).isTrue();
  }

}