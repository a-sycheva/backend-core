package ru.mentee.power.crm.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.dto.UpdateLeadRequest;

@Mapper
public interface LeadMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "company", ignore = true) // установить в сервисе
  Lead toEntity(CreateLeadRequest dto);

  @Mapping(target = "companyId", source = "company.id")
  @Mapping(target = "companyName", source = "company.name")
  LeadResponse toResponse(Lead entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "company", ignore = true) // установить в сервисе
  @Mapping(target = "version", ignore = true)
  void updateEntity(UpdateLeadRequest dto, @MappingTarget Lead entity);
}
