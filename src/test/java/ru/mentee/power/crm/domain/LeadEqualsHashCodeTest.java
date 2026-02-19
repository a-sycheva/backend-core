package ru.mentee.power.crm.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class LeadEqualsHashCodeTest {

  @Test
  void  shouldBeReflexiveWhenEqualsCalledOnSameObject() {
    Lead lead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    assertThat(lead).isEqualTo(lead);
  }

  @Test
  void shouldBeSymmetricWhenEqualsCalledOnTwoObjects() {
    Lead firstLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");
    Lead secondLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(firstLead);
  }

  @Test
  void shouldBeTransitiveWhenEqualsChainOfThreeObjects() {

    Lead firstLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");
    Lead secondLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");
    Lead thirdLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(secondLead).isEqualTo(thirdLead);
    assertThat(firstLead).isEqualTo(thirdLead);
  }

  @Test
  void shouldBeConsistentWhenEqualsCalledMultipleTimes() {
    Lead firstLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");
    Lead secondLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead).isEqualTo(secondLead);
  }

  @Test
  void shouldReturnFalseWhenEqualsComparedWithNull() {

    Lead lead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    assertThat(lead).isNotEqualTo(null);
  }

  @Test
  void shouldHaveSameHashCodeWhenObjectsAreEqual() {

    Lead firstLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");
    Lead secondLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    assertThat(firstLead).isEqualTo(secondLead);
    assertThat(firstLead.hashCode()).isEqualTo(secondLead.hashCode());
  }

  @Test
  void shouldWorkInHashMapWhenLeadUsedAsKey() {

    Lead keyLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");
    Lead lookupLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    Map<Lead, String> map = new HashMap<>();
    map.put(keyLead, "CONTACTED");

    String status = map.get(lookupLead);

    assertThat(status).isEqualTo("CONTACTED");
  }

  @Test
  void shouldNotBeEqualWhenIdsAreDifferent() {
    Lead firstLead = new Lead("L1", "test@example.ru", "+71234567890", "TestCorp", "NEW");
    Lead diffetentLead = new Lead("L2", "test@example.ru", "+71234567890", "TestCorp", "NEW");

    assertThat(firstLead).isNotEqualTo(diffetentLead);
  }
}
