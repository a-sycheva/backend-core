package ru.mentee.power.crm.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentee.power.crm.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {}
