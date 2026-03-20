package ru.mentee.power.crm.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ru.mentee.power.crm.repository.StatusRepository;

@Service
public class StatusService {
  private final StatusRepository statusRepository;

  public StatusService(StatusRepository statusRepository) {
    this.statusRepository = statusRepository;
  }

  public void addStatus(String status) {
    if (status == null || status.trim().isEmpty()) {
      throw new IllegalArgumentException("Status cannot be null or empty");
    }
    String upperCaseStatus = status.trim().toUpperCase();

    statusRepository.addStatus(upperCaseStatus);
  }

  public void removeStatus(String status) {
    if (status == null || status.trim().isEmpty()) {
      throw new IllegalArgumentException("Status cannot be null or empty");
    }

    String upperCaseStatus = status.trim().toUpperCase();
    statusRepository.removeStatus(upperCaseStatus);
  }

  public List<String> findAll() {
    return statusRepository.findAll();
  }

  public boolean statusExists(String status) {
    if (status == null || status.trim().isEmpty()) {
      return false;
    }
    return statusRepository.findAll().contains(status.trim());
  }

  public boolean containsStatus(String status) {
    return statusRepository.containsStatus(status);
  }
}