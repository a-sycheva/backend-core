package ru.mentee.power.crm.core;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Lead;

class LeadRepositoryTest {

  @Test
  @DisplayName("Should automatically deduplicate leads by id")
  void shouldDeduplicateLeadsById() {
    LeadRepository leads = new LeadRepository();

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    leads.add(lead);

    assertThat(leads.add(lead)).isFalse(); //пытается добавить, и проверяет
    assertThat(leads.size()).isEqualTo(1);
  }

  @Test
  void shouldThrowExceptionWhenAddedLeadIsNull() {
    LeadRepository leads = new LeadRepository();

    assertThatThrownBy(() -> leads.add(null))
        .isInstanceOf(IllegalArgumentException.class).
        hasMessage("Lead can`t be null");
  }

  @Test
  @DisplayName("Should allow different leads with different ids")
  void shouldAllowDifferentLeads() {

    LeadRepository leads = new LeadRepository();

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead firstLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
    Lead secondLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    assertThat(leads.add(firstLead)).isTrue(); //пытается добавить, и проверяет
    assertThat(leads.add(secondLead)).isTrue(); //пытается добавить, и проверяет
    assertThat(leads.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("Should find existing lead through contains")
  void shouldFindExistingLead() {

    LeadRepository leads = new LeadRepository();

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    leads.add(lead);

    assertThat(leads.contains(lead)).isTrue();
  }

  @Test
  @DisplayName("Should return unmodifiable set from findAll")
  void shouldReturnUnmodifiableSet() {

    LeadRepository leads = new LeadRepository();

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    leads.add(lead);

    Set<Lead> unmodifiableLeads = leads.findAll();

    assertThatThrownBy(unmodifiableLeads::clear)
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  @DisplayName("Should perform contains() faster than ArrayList")
  void shouldPerformFasterThanArrayList() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Set<Lead> leadsHashSet = new HashSet<>();
    ArrayList<Lead> leadArrayList = new ArrayList<>();

    for (int i = 0; i < 10000; i++) {
      Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
      leadsHashSet.add(lead);
      leadArrayList.add(lead);
    }

    long startHashSet = System.nanoTime();
    for (Lead lead : leadsHashSet) {
      leadsHashSet.contains(lead);
    }
    long durationHashSet = System.nanoTime() - startHashSet;

    long startArrayList = System.nanoTime();
    for (Lead lead : leadArrayList) {
      leadArrayList.contains(lead);
    }
    long durationArrayList = System.nanoTime() - startArrayList;

    assertThat(durationArrayList).isGreaterThan(durationHashSet);
  }
}