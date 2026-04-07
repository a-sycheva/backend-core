package ru.mentee.power.crm.spring.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Deal;
import ru.mentee.power.crm.model.DealStatus;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.DealService;
import ru.mentee.power.crm.service.LeadService;

@WebMvcTest(DealController.class)
class DealControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private DealService dealService;
  @MockitoBean
  private LeadService leadService;

  @Test
  void shouldGetAllDealsWhenItCalled() throws Exception {
    Deal firstDeal = new Deal(UUID.randomUUID(), BigDecimal.valueOf(10_000));
    List<Deal> deals = List.of(firstDeal);
    when(dealService.getAllDeals()).thenReturn(deals);

    mockMvc.perform(get("/deals"))
         .andExpect(status().isOk())
         .andExpect(model().attribute("deals", deals))
         .andExpect(view().name("deals/list"));
  }

  @Test
  void shouldShowKanbanView() throws Exception {
    Map<DealStatus, List<Deal>> groupedDeals = Map.of(
        DealStatus.NEW, List.of(new Deal(UUID.randomUUID(), BigDecimal.valueOf(10_000)))
    );
    when(dealService.getDealsByStatusForKanban()).thenReturn(groupedDeals);

    mockMvc.perform(get("/deals/kanban"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("dealsByStatus"))
        .andExpect(view().name("deals/kanban"));
  }

  @Test
  void shouldShowConvertForm() throws Exception {
    UUID leadId = UUID.randomUUID();
    Lead lead = new Lead(leadId, "test@mail.ru",
        new Company("TestCorp", "TestIndustry"), LeadStatus.NEW);
    when(leadService.findById(leadId)).thenReturn(Optional.of(lead));

    mockMvc.perform(get("/deals/convert/{leadId}", leadId))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("lead"))
        .andExpect(view().name("deals/convert"));
  }

  @Test
  void shouldThrowExceptionWhenTryShowConvertFormWithNonExistingLead() throws Exception {
    UUID leadId = UUID.randomUUID();

    when(leadService.findById(leadId)).thenReturn(Optional.empty());

    mockMvc.perform(get("/deals/convert/{leadId}", leadId))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldConvertLeadToDeal() throws Exception {
    UUID leadId = UUID.randomUUID();
    BigDecimal amount = BigDecimal.valueOf(50000);

    mockMvc.perform(post("/deals/convert")
            .param("leadId", leadId.toString())
            .param("amount", amount.toString()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/deals"));

    verify(dealService).convertLeadToDeal(leadId, amount);
  }

  @Test
  void shouldTransitionDealStatus() throws Exception {
    UUID dealId = UUID.randomUUID();
    DealStatus newStatus = DealStatus.WON;

    mockMvc.perform(post("/deals/{dealId}/transition", dealId)
            .param("newStatus", newStatus.name()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/deals/kanban"));

    verify(dealService).transitionDealStatus(dealId, newStatus);
  }

}