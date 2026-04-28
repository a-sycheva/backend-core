package ru.mentee.power.crm.service;

import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.mentee.power.crm.model.Invitee;
import ru.mentee.power.crm.repository.InviteeRepository;
import ru.mentee.power.crm.spring.dto.CreateInviteeRequest;
import ru.mentee.power.crm.spring.dto.InviteeResponse;
import ru.mentee.power.crm.spring.dto.UpdateInviteeStatusRequest;
import ru.mentee.power.crm.spring.exception.EmailAlreadyExistsException;
import ru.mentee.power.crm.spring.exception.EntityNotFoundException;
import ru.mentee.power.crm.spring.exception.InvalidStatusException;
import ru.mentee.power.crm.spring.mapper.InviteeMapper;

@Service
@AllArgsConstructor
public class InviteeService {
  private final InviteeRepository repository;
  private final InviteeMapper mapper;

  public Page<InviteeResponse> findAll(Pageable pageable) {
    return repository.findAll(pageable).map(mapper::toResponse);
  }

  public InviteeResponse findById(UUID id) {
    return repository
        .findById(id)
        .map(mapper::toResponse)
        .orElseThrow(() -> new EntityNotFoundException("Invitee", id.toString()));
  }

  public Optional<InviteeResponse> findByEmail(String email) {
    return repository.findByEmail(email).map(mapper::toResponse);
  }

  public InviteeResponse save(CreateInviteeRequest request) {
    if (repository.findByEmail(request.getEmail()).isPresent()) {
      throw new EmailAlreadyExistsException(request.getEmail());
    }
    Invitee invitee = mapper.toEntity(request);
    return mapper.toResponse(repository.save(invitee));
  }

  public void delete(UUID id) {
    if (!repository.existsById(id)) {
      throw new EntityNotFoundException("Invitee", id.toString());
    }
    repository.deleteById(id);
  }

  public InviteeResponse updateStatus(UUID id, UpdateInviteeStatusRequest request) {
    Invitee invitee =
        repository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Invitee", id.toString()));

    if (!request.getStatus().toString().equals("ACTIVE")
        && !request.getStatus().toString().equals("INACTIVE")) {
      throw new InvalidStatusException(request.getStatus().toString());
    }

    mapper.updateEntity(request, invitee);
    return mapper.toResponse(repository.save(invitee));
  }
}
