package ru.mentee.power.crm.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.model.Invitee;

public interface InviteeRepository extends JpaRepository<Invitee, UUID> {

  Optional<Invitee> findByEmail(String email);
}
