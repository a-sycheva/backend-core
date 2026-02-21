package ru.mentee.power.crm.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class LeadEqualsHashCodeTest {

  @Test
  void  shouldBeReflexiveWhenEqualsCalledOnSameObject() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead lead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    assertThat(lead).isEqualTo(lead);
  }

  @Test
  void shouldBeSymmetricWhenEqualsCalledOnTwoObjects() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    UUID id = UUID.randomUUID();

    Lead firstLead = new Lead(id, contact, "TestCorp", "NEW");
    Lead secondLead = new Lead(id, contact, "TestCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(firstLead);
  }

  @Test
  void shouldBeTransitiveWhenEqualsChainOfThreeObjects() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    UUID id = UUID.randomUUID();

    Lead firstLead = new Lead(id, contact, "TestCorp", "NEW");
    Lead secondLead = new Lead(id, contact, "TestCorp", "NEW");
    Lead thirdLead  = new Lead(id, contact, "TestCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(thirdLead);
    assertThat(firstLead).isEqualTo(thirdLead);
  }

  @Test
  void shouldBeConsistentWhenEqualsCalledMultipleTimes() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    UUID id = UUID.randomUUID();

    Lead firstLead = new Lead(id, contact, "TestCorp", "NEW");
    Lead secondLead = new Lead(id, contact, "TestCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
  }

  @Test
  void shouldReturnFalseWhenEqualsComparedWithNull() {
    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    UUID id = UUID.randomUUID();

    Lead lead = new Lead(id, contact, "TestCorp", "NEW");

    assertThat(lead).isNotEqualTo(null);
  }

  @Test
  void shouldHaveSameHashCodeWhenObjectsAreEqual() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    UUID id = UUID.randomUUID();

    Lead firstLead = new Lead(id, contact, "TestCorp", "NEW");
    Lead secondLead = new Lead(id, contact, "TestCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).hasSameHashCodeAs(secondLead);
  }

  @Test
  void shouldWorkInHashMapWhenLeadUsedAsKey() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    UUID id = UUID.randomUUID();

    Lead keyLead = new Lead(id, contact, "TestCorp", "NEW");
    Lead lookupLead = new Lead(id, contact, "TestCorp", "NEW");

    Map<Lead, String> map = new HashMap<>();
    map.put(keyLead, "CONTACTED");

    String status = map.get(lookupLead);

    assertThat(status).isEqualTo("CONTACTED");
  }

  @Test
  void shouldNotBeEqualWhenIdsAreDifferent() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Lead firstLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");
    Lead diffetentLead = new Lead(UUID.randomUUID(), contact, "TestCorp", "NEW");

    assertThat(firstLead).isNotEqualTo(diffetentLead);
  }
}
