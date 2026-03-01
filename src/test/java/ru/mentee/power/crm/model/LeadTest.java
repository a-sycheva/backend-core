package ru.mentee.power.crm.model;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class LeadTest {

  @Test
  void shouldGenerateSimilarHashWhenLeadsHasSimilarId() {
    UUID id = UUID.randomUUID();
    Lead firstLead = new Lead(id, "test1@example.ru", "+71234567890", LeadStatus.NEW);
    Lead secondLead = new Lead(id, "test2@example.ru", "+70987654321", LeadStatus.NEW);

    assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
  }

  @Test
  void shouldReturnFalseWhenEqualsNullOrNoninstanceObjects() {
    Lead firstLead = new Lead(UUID.randomUUID(), "test1@example.ru",
        "+71234567890", LeadStatus.NEW);
    String someString = "some string";

    assertThat(firstLead.equals(null)).isFalse();
    assertThat(firstLead.equals(someString)).isFalse();
  }

  @Test
  void shouldReturnTrueWhenEqualsItself() {
    Lead lead = new Lead(UUID.randomUUID(), "test1@example.ru", "+71234567890", LeadStatus.NEW);

    assertThat(lead.equals(lead)).isTrue();
  }

}