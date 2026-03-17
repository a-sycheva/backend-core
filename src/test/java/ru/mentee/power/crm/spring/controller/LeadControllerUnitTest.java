package ru.mentee.power.crm.spring.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.spring.MockLeadService;

public class LeadControllerUnitTest {
  MockLeadService mockService = new MockLeadService();
  LeadController controller = new LeadController(mockService);
  Model model = new BindingAwareModelMap();

  @Test
  void shouldCreateControllerWithoutSpring() {
    String response = controller.showLeads(null, model);
    List<Lead> leads = (List<Lead>) model.getAttribute("leads");

    assertThat(response).isEqualTo("leads/list");
    assertThat(model.getAttribute("leads")).isInstanceOf(List.class);
    assertThat(leads).hasSize(2);
  }

  @Test
  void shouldUseInjectedService() {
    assertThat(controller).extracting("leadService").isSameAs(mockService);
  }
}
