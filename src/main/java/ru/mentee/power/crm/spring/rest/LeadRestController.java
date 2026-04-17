package ru.mentee.power.crm.spring.rest;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadRestController {
  private final LeadService leadService;

  @GetMapping
  public ResponseEntity<List<Lead>> getAllLeads() {
    List<Lead> result = leadService.findAll();
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Lead> getLeadById(@PathVariable UUID id) {
    return leadService.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
    Lead addedLead = leadService.addLead(lead.getName(), lead.getEmail(),
        lead.getCompany(), lead.getStatus());
    UUID id = addedLead.getId();

    return ResponseEntity.created(URI.create("/api/leads/" + id)).body(addedLead);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Lead> updateLead(
      @PathVariable UUID id,
      @RequestBody Lead lead) {

    return leadService.updateLead(id, lead)
        .map(updated -> ResponseEntity.ok(updated))
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
    if (leadService.deleteLead(id)) {
      return ResponseEntity.noContent().build();
    }
    else {
      return ResponseEntity.notFound().build();
    }
  }
}
