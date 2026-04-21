package ru.mentee.power.crm.spring.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.dto.UpdateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Validated
public class LeadRestController {
  private final LeadService leadService;
  private final LeadMapper leadMapper;

  @GetMapping
  public ResponseEntity<List<LeadResponse>> getAllLeads() {
    List<LeadResponse> result = leadService.findAll().stream().map(leadMapper::toResponse).toList();
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LeadResponse> getLeadById(
      @PathVariable @NotNull(message = "ID лида обязателен") UUID id) {
    return leadService
        .findById(id)
        .map(leadMapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody CreateLeadRequest request) {
    Lead lead = leadMapper.toEntity(request);
    leadService.addLead(lead.getName(), lead.getEmail(), lead.getCompany(), lead.getStatus());
    LeadResponse response = leadMapper.toResponse(lead);
    URI uri = URI.create("/api/leads/" + lead.getId());

    return ResponseEntity.created(uri).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<LeadResponse> updateLead(
      @PathVariable UUID id, @RequestBody UpdateLeadRequest request) {

    return leadService
        .findById(id)
        .map(
            lead -> {
              leadMapper.updateEntity(request, lead);
              return leadService.updateLead(id, lead);
            })
        .flatMap(optional -> optional)
        .map(leadMapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
    if (leadService.deleteLead(id)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
