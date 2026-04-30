package ru.mentee.power.crm.spring.rest.fixed;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.service.InviteeService;
import ru.mentee.power.crm.spring.dto.CreateInviteeRequest;
import ru.mentee.power.crm.spring.dto.InviteeResponse;
import ru.mentee.power.crm.spring.dto.UpdateInviteeStatusRequest;

@RestController
@AllArgsConstructor
@RequestMapping("/fixed")
public class InviteeControllerFixed {
  private final InviteeService service;

  @GetMapping("/invitees")
  public ResponseEntity<Page<InviteeResponse>> getInvitees(Pageable pageable) {
    Page<InviteeResponse> result = service.findAll(pageable);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/invitees/{id}")
  public ResponseEntity<InviteeResponse> getById(@PathVariable UUID id) {
    return ResponseEntity.ok().body(service.findById(id));
  }

  @PostMapping("/invitees")
  public ResponseEntity<InviteeResponse> create(@Valid @RequestBody CreateInviteeRequest request) {
    InviteeResponse created = service.save(request);
    URI location = URI.create("/api/invitees/" + created.id());
    return ResponseEntity.created(location).body(created);
  }

  @DeleteMapping("/invitees/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/invitees/{id}/status")
  public ResponseEntity<InviteeResponse> updateStatus(
      @PathVariable UUID id, @Valid @RequestBody UpdateInviteeStatusRequest request) {
    InviteeResponse updated = service.updateStatus(id, request);
    return ResponseEntity.ok().body(updated);
  }
}
