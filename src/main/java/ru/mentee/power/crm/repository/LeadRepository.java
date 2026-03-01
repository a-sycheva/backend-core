package ru.mentee.power.crm.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ru.mentee.power.crm.model.Lead;

public interface LeadRepository<T> {

  Lead save(T lead);

  Optional<T> findById(UUID id);

  Optional<T> findByEmail(String email);

  List<T> findAll();

  void delete(UUID id);
}