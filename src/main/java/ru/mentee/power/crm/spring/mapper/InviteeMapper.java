package ru.mentee.power.crm.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mentee.power.crm.model.Invitee;
import ru.mentee.power.crm.spring.dto.CreateInviteeRequest;
import ru.mentee.power.crm.spring.dto.InviteeResponse;
import ru.mentee.power.crm.spring.dto.UpdateInviteeStatusRequest;

@Mapper
public interface InviteeMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  Invitee toEntity(CreateInviteeRequest dto);

  InviteeResponse toResponse(Invitee entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "firstName", ignore = true)
  @Mapping(target = "email", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  void updateEntity(UpdateInviteeStatusRequest dto, @MappingTarget Invitee entity);
}
