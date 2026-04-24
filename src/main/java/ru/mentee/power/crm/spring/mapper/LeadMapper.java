package ru.mentee.power.crm.spring.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;

@Mapper
public interface LeadMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  Lead toEntity(CreateLeadRequest dto);

  @Mapping(target = "companyId", source = "company.id")
  @Mapping(target = "companyName", source = "company.name")
  LeadResponse toResponse(Lead entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "company", ignore = true)
  @Mapping(target = "version", ignore = true)
  void updateEntity(UpdateLeadRequest dto, @MappingTarget Lead entity);

  default OffsetDateTime map(LocalDateTime value) {
    return value != null ? value.atOffset(ZoneOffset.UTC) : null;
  }

  default LocalDateTime map(OffsetDateTime value) {
    return value != null ? value.toLocalDateTime() : null;
  }

  // Конвертация статусов (DTO → Entity)
  default ru.mentee.power.crm.model.LeadStatus map(
      ru.mentee.power.crm.spring.dto.generated.LeadStatus status) {
    return status == null ? null : ru.mentee.power.crm.model.LeadStatus.valueOf(status.name());
  }

  // Конвертация статусов (Entity → DTO)
  default ru.mentee.power.crm.spring.dto.generated.LeadStatus map(
      ru.mentee.power.crm.model.LeadStatus status) {
    return status == null
        ? null
        : ru.mentee.power.crm.spring.dto.generated.LeadStatus.valueOf(status.name());
  }
}
