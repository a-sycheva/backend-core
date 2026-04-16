package ru.mentee.power.crm.spring.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mentee.power.crm.model.DealStatus;
import ru.mentee.power.crm.service.DealService;
import ru.mentee.power.crm.service.LeadService;

@Controller
@RequestMapping("/deals")
@RequiredArgsConstructor
public class DealController {
  private final DealService dealService;
  private final LeadService leadService;

  @GetMapping
  public String listDeals(Model model) {
    model.addAttribute("deals", dealService.getAllDeals());
    return "deals/list";
  }

  // показать доску канбан
  @GetMapping("/kanban")
  public String kanbanView(Model model) {
    model.addAttribute("dealsByStatus", dealService.getDealsByStatusForKanban());
    return "deals/kanban";
  }

  // выполнить переход статуса
  @PostMapping("/{dealId}/transition")
  public String transitionStatus(@PathVariable UUID dealId, @RequestParam DealStatus newStatus) {
    dealService.transitionDealStatus(dealId, newStatus);
    return "redirect:/deals/kanban";
  }
}
