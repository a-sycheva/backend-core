package ru.mentee.power.crm.spring.exception;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class EntityNotFoundException extends BusinessException {
  private final String entityType;
  private final String entityId;


  public EntityNotFoundException(String entityType, String entityId) {
    super("Entity " + entityType + " is not found with id: " + entityId);
    this.entityType = entityType;
    this.entityId = entityId;
  }

  public EntityNotFoundException (String message) {
    super(message);
    this.entityType = null;
    this.entityId = null;
  }
}
