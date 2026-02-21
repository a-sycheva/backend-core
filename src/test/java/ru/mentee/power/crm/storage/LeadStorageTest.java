package ru.mentee.power.crm.storage;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Lead;

class LeadStorageTest {

  @Test
  void shouldAddLeadWhenLeadIsUnique() {

    Lead lead = new Lead("1", "ivan@mail.ru", "+71234567890", "TechCorp", "NEW");
    LeadStorage leadStorage = new LeadStorage();

    boolean added = leadStorage.add(lead);

    assertThat(added).isTrue();
    assertThat(leadStorage.size()).isEqualTo(1);
    assertThat(leadStorage.findAll()).containsExactly(lead);
  }

  @Test
  void shouldRejectDuplicateWhenEmailAlreadyExists() {

    Lead existingLead = new Lead("1", "ivan@mail.ru", "+71234567890", "TechCorp", "NEW");
    Lead duplicateLead = new Lead("2", "ivan@mail.ru", "+70987654321", "ITCorp", "NEW");
    LeadStorage storage = new LeadStorage();
    storage.add(existingLead);

    boolean added = storage.add(duplicateLead);

    assertThat(added).isFalse();
    assertThat(storage.size()).isEqualTo(1);
    assertThat(storage.findAll()).containsExactly(existingLead);
  }

  @Test
  void shouldThrowExceptionWhenStorageIsFull() {

    LeadStorage storage = new LeadStorage();

    for (int index = 0; index < 100; index++) {

      storage.add(new Lead(String.valueOf(index), "lead" + index + "@mail.ru",
          "+71234567890", "Company", "NEW"));

    }

    Lead hunfredFirstLead = new Lead("101", "lead101@mail.ru", "+71234567890", "Company", "NEW");

    assertThatThrownBy(() -> storage.add(hunfredFirstLead))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Storage is full");

  }

  @Test
  void shouldReturnOnlyAddedLeadsWhenFindAllCalled() {

    LeadStorage storage = new LeadStorage();
    Lead firstLead = new Lead("1", "ivan@mail.ru", "+71234567890", "TechCorp", "NEW");
    Lead secondLead = new Lead("2", "mariya@mail.ru", "++70987654321", "ITCorp", "NEW");
    storage.add(firstLead);
    storage.add(secondLead);

    Lead[] result = storage.findAll();

    assertThat(result).containsExactly(firstLead, secondLead);
    assertThat(result).hasSize(2);
  }

  @Test
  void shouldAddNullEmailLeadWithoutDuplicate() {
    LeadStorage leads = new LeadStorage();

    Lead firtsLead = new Lead("123", null, "123", "comp1", "NEW");
    Lead secondLead = new Lead("456", "notnull@mail.ru", "321", "comp2", "NEW");
    Lead thirdLead = new Lead("789", null, "789", "comp3", "NEW");

    leads.add(firtsLead);
    leads.add(secondLead);
    leads.add(thirdLead);

    assertThat(leads.size()).isEqualTo(2);
    assertThat(leads.findAll()).containsOnly(firtsLead, secondLead);
  }

}