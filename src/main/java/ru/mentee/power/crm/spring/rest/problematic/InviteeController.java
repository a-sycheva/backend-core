package ru.mentee.power.crm.spring.rest.problematic;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.model.Invitee;
import ru.mentee.power.crm.model.InviteeStatus;
import ru.mentee.power.crm.repository.InviteeRepository;

@RestController
@AllArgsConstructor
public class InviteeController {

  @Autowired InviteeRepository repository;

  @PostMapping("/getInvitees")
  public List<Invitee> getInvitees() {
    return repository.findAll();
  }

  @GetMapping("/invitees/{id}")
  public Invitee getById(@PathVariable UUID id) {
    return repository.findById(id).orElse(null);
  }

  @PostMapping("/invitees")
  public Invitee create(@RequestBody Map<String, Object> params) {
    String email = (String) params.get("email");
    String firstName = (String) params.get("firstName");

    // Проверка email через SQL
    String sql = "SELECT COUNT(*) FROM invitees WHERE email = '" + email + "'";
    // repository.executeNativeQuery(sql); // Представим что это выполняется

    Invitee invitee = new Invitee();
    invitee.setId(UUID.randomUUID());
    invitee.setEmail(email);
    invitee.setFirstName(firstName);
    invitee.setCreatedAt(LocalDateTime.from(Instant.now()));

    return repository.save(invitee);
  }

  @DeleteMapping("/invitees/{id}")
  public Invitee delete(@PathVariable UUID id) {
    Invitee invitee = repository.findById(id).orElse(null);
    if (invitee != null) {
      repository.delete(invitee);
    }
    return invitee;
  }

  @PutMapping("/invitees/{id}/status")
  public Invitee updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
    try {
      Invitee invitee = repository.findById(id).orElseThrow();
      String status = body.get("status");

      // Бизнес-логика в контроллере
      if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
        invitee.setStatus(InviteeStatus.valueOf(status));
      } else {
        throw new RuntimeException("Invalid status");
      }

      return repository.save(invitee);
    } catch (Exception e) {
      // Пустой catch
      return null;
    }
  }
}
