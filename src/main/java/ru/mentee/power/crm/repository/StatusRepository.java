package ru.mentee.power.crm.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class StatusRepository {
  HashSet<String> statuces =  new HashSet<>();

  public void addStatus(String status) {
    statuces.add(status);
  }

  public void removeStatus(String status) {
    statuces.remove(status);
  }

  public List<String> findAll() {
    return new ArrayList<String>(statuces);
  }

  public boolean containsStatus(String status) {
    return statuces.contains(status);
  }
}