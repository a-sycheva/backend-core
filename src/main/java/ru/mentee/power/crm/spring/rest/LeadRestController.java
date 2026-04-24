package ru.mentee.power.crm.spring.rest;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.rest.generated.LeadManagementApi;

@RestController
@RequiredArgsConstructor
public class LeadRestController implements LeadManagementApi {
  private final LeadService leadService;
  private final LeadMapper leadMapper;

  @Override
  public ResponseEntity<List<LeadResponse>> getLeads() {
    List<LeadResponse> result = leadService.findAll().stream().map(leadMapper::toResponse).toList();
    return ResponseEntity.ok(result);
  }

  @Override
  public ResponseEntity<LeadResponse> getLeadById(UUID id) {
    return ResponseEntity.ok().body(leadService.getLeadById(id));
  }

  @Override
  public ResponseEntity<LeadResponse> createLead(CreateLeadRequest request) {
    Lead lead = leadMapper.toEntity(request);
    leadService.addLead(lead.getName(), lead.getEmail(), lead.getCompany(), lead.getStatus());
    LeadResponse response = leadMapper.toResponse(lead);
    URI uri = URI.create("/api/leads/" + lead.getId());

    return ResponseEntity.created(uri).body(response);
  }

  @Override
  public ResponseEntity<LeadResponse> updateLead(UUID id, UpdateLeadRequest request) {

    LeadResponse response = leadService.updateLead(id, request);
    return ResponseEntity.ok(response); // ← только 200 OK с телом
  }

  @Override
  public ResponseEntity<Void> deleteLead(UUID id) {
    leadService.deleteLead(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/test-500")
  public String test500() {
    throw new RuntimeException("Test internal server error");
  }
}
