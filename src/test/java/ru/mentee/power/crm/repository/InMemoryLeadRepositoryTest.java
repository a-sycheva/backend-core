package ru.mentee.power.crm.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

class InMemoryLeadRepositoryTest {
  InMemoryLeadRepository leadRepository;
  private Lead lead;
  private UUID leadId;

  @BeforeEach
  void setUp() {
    leadId = UUID.randomUUID();
    leadId = UUID.randomUUID();
    lead = new Lead(leadId, "test@example.ru", "SomeCompany", LeadStatus.NEW);

    leadRepository = new InMemoryLeadRepository();
  }

  @Test
  void  shouldAddLeadWhenDataIsValid () {
    leadRepository.save(lead);

    assertThat(leadRepository.findAll().size()).isEqualTo(1);
  }

  @Test
  void shouldNotAddDuplicateByIdLeads() {

    Lead duplicatedLead = new Lead(leadId, "test1@example.ru", "FirstCompany", LeadStatus.NEW);

    leadRepository.save(lead);

    assertThat(leadRepository.findAll().size()).isEqualTo(1);
  }

  @Test
  void shouldReturnEmptyWhenFindByNullId() {
    assertThat(leadRepository.findById(null)).isNotPresent();
  }

  @Test
  void shouldReturnEmptyWhenTryToFindDontExistLead() {
    assertThat(leadRepository.findById(UUID.randomUUID())).isNotPresent();
  }

  @Test
  void shouldReturnLeadWhenTryToFindExistLead() {

    Lead secondLead = new Lead(UUID.randomUUID(), "test2@example.ru",
        "SomeCompany", LeadStatus.NEW);

    leadRepository.save(lead);
    leadRepository.save(secondLead);

    Lead foundLead = leadRepository.findById(lead.id()).orElse(null);

    assertThat(foundLead).isEqualTo(lead);
  }

  @Test
  void shouldReturnEmptyWhenFindByNullEmail() {
    assertThat(leadRepository.findByEmail(null)).isNotPresent();
  }

  @Test
  void shouldReturnEmptyWhenTryToFindByEmailDontExistLead() {
    assertThat(leadRepository.findByEmail("no@exist.ru")).isNotPresent();
  }

  @Test
  void shouldReturnLeadWhenTryToFindByEmailExistLead() {

    Lead secondLead = new Lead(UUID.randomUUID(), "test2@example.ru",
        "SomeCompany", LeadStatus.NEW);

    leadRepository.save(lead);
    leadRepository.save(secondLead);

    Lead foundLead = leadRepository.findByEmail("test@example.ru").orElse(null);

    assertThat(foundLead).isEqualTo(lead);
  }

  @Test
  void shouldRemoveLeadWhenItCalled() {

    Lead secondLead = new Lead(UUID.randomUUID(), "test2@example.ru",
        "SecondCompany", LeadStatus.NEW);
    Lead thirdLead = new Lead(UUID.randomUUID(), "test3@example.ru",
        "ThirdCompany", LeadStatus.NEW);
    Lead fourthLead = new Lead(UUID.randomUUID(), "test4@example.ru",
        "FourthCompany", LeadStatus.NEW);
    Lead fivesLead = new Lead(UUID.randomUUID(), "test5@example.ru",
        "FivesCompany", LeadStatus.NEW);

    leadRepository.save(lead);
    leadRepository.save(secondLead);
    leadRepository.save(thirdLead);
    leadRepository.save(fourthLead);
    leadRepository.save(fivesLead);

    leadRepository.delete(thirdLead.id());

    assertThat(leadRepository.findAll().size()).isEqualTo(4);
    assertThat(leadRepository.findById(thirdLead.id())).isNotPresent();
  }

  @Test
  void shouldIgnoreWhenRemoveNonExistentLead() {
    assertThat(leadRepository.findAll().size()).isEqualTo(0);
  }

  @Test
  void shouldNotChangeInternalStorage() {

    Lead secondLead = new Lead(UUID.randomUUID(), "test2@example.ru",
        "SecondCompany", LeadStatus.NEW);

    leadRepository.save(lead);

    leadRepository.findAll().add(secondLead);

    assertThat(leadRepository.findAll().size()).isEqualTo(1);
  }
}