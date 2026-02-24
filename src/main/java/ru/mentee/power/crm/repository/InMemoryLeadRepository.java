package ru.mentee.power.crm.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.Repository;

public class InMemoryLeadRepository implements Repository<Lead> {

  private final ArrayList<Lead> storage = new ArrayList<>();

  @Override
  public void add(Lead lead) {

    if (storage.contains(lead)) {
      throw new IllegalArgumentException("This lead already exists!");
    }
    storage.add(lead);
  }

  @Override
  public void remove(UUID id) {

    if (!storage.removeIf(lead -> lead.id().equals(id))) {
      throw new IllegalArgumentException("This lead does not exists!");
    }
  }

  @Override
  public Optional<Lead> findById(UUID id) {

    return storage.stream().filter(lead -> lead.id()
        .equals(id)).findFirst();
  }

  @Override
  public List<Lead> findAll() {

    List<Lead> leads = new ArrayList<>();

    for (Lead lead : storage) {

      if (lead != null) {
        leads.add(lead);
      }

    }
    return leads;
  }
}
