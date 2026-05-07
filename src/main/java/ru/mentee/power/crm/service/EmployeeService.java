package ru.mentee.power.crm.service;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Employee;
import ru.mentee.power.crm.repository.EmployeeRepository;
import ru.mentee.power.crm.spring.dto.CreateEmployeeRequest;
import ru.mentee.power.crm.spring.dto.EmployeeResponse;
import ru.mentee.power.crm.spring.dto.UpdateEmployeeSalaryRequest;
import ru.mentee.power.crm.spring.exception.EntityNotFoundException;
import ru.mentee.power.crm.spring.mapper.EmployeeMapper;

@Service
@AllArgsConstructor
public class EmployeeService {
  private final EmployeeRepository employeeRepository;
  private final EmployeeMapper employeeMapper;

  @Transactional(readOnly = true)
  public Page<EmployeeResponse> findAll(Pageable pageable) {
    return employeeRepository.findAll(pageable).map(employeeMapper::toResponse);
  }

  @Transactional(readOnly = true)
  public EmployeeResponse findById(UUID id) {
    Employee employee =
        employeeRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee", id.toString()));
    return employeeMapper.toResponse(employee);
  }

  public EmployeeResponse addEmployee(CreateEmployeeRequest request) {
    Employee employee = employeeMapper.toEntity(request);
    Employee saved = employeeRepository.save(employee);
    return employeeMapper.toResponse(saved);
  }

  @Transactional
  public EmployeeResponse updateSalary(UUID id, UpdateEmployeeSalaryRequest request) {
    Employee employee =
        employeeRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee", id.toString()));

    employeeMapper.updateEntity(request, employee);

    Employee saved = employeeRepository.save(employee);
    return employeeMapper.toResponse(saved);
  }

  public void deleteEmployee(UUID id) {
    if (!employeeRepository.existsById(id)) {
      throw new EntityNotFoundException("Employee", id.toString());
    }
    employeeRepository.deleteById(id);
  }
}
