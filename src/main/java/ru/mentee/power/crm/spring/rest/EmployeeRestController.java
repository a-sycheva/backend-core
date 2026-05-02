package ru.mentee.power.crm.spring.rest;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.service.EmployeeService;
import ru.mentee.power.crm.spring.dto.CreateEmployeeRequest;
import ru.mentee.power.crm.spring.dto.EmployeeResponse;
import ru.mentee.power.crm.spring.dto.UpdateEmployeeSalaryRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
public class EmployeeRestController {
  private final EmployeeService service;

  @GetMapping
  public ResponseEntity<Page<EmployeeResponse>> getEmployees(Pageable pageable) {
    Page<EmployeeResponse> employees = service.findAll(pageable);
    return ResponseEntity.ok(employees);
  }

  @PostMapping
  public ResponseEntity<EmployeeResponse> addEmployees(
      @Valid @RequestBody CreateEmployeeRequest request) {
    EmployeeResponse created = service.addEmployee(request);
    URI location = URI.create("/api/employees/" + created.id());
    return ResponseEntity.created(location).body(created);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
    service.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<EmployeeResponse> updateSalary(
      @PathVariable UUID id, @Valid @RequestBody UpdateEmployeeSalaryRequest request) {
    EmployeeResponse updated = service.updateSalary(id, request);
    return ResponseEntity.ok(updated);
  }
}
