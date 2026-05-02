package ru.mentee.power.crm.spring.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.mentee.power.crm.model.Employee;
import ru.mentee.power.crm.spring.dto.CreateEmployeeRequest;
import ru.mentee.power.crm.spring.dto.EmployeeResponse;
import ru.mentee.power.crm.spring.dto.UpdateEmployeeSalaryRequest;

@Mapper
public interface EmployeeMapper {

  @Mapping(target = "id", ignore = true)
  Employee toEntity(CreateEmployeeRequest dto);

  EmployeeResponse toResponse(Employee entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "name", ignore = true)
  void updateEntity(UpdateEmployeeSalaryRequest dto, @MappingTarget Employee entity);
}
