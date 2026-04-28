package ru.mentee.power.crm.spring.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import ru.mentee.power.crm.model.InviteeStatus;

public record InviteeResponse(
    UUID id, String firstName, String email, InviteeStatus status, LocalDateTime createdAt) {}
