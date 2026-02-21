package ru.mentee.power.crm.storage;

import java.util.Objects;

import ru.mentee.power.crm.domain.Lead;

public class LeadStorage {
  private Lead[] leads = new Lead[100];

  public boolean add (Lead lead) {

    for (int index = 0; index < leads.length; index++) {
      if (leads[index] != null
          && Objects.equals(leads[index].contact().email(), lead.contact().email())) {
        return  false;
      }
    }

    for (int index = 0; index < leads.length; index++) {
      if (leads[index] == null) {
        leads[index] = lead;
        return true;
      }
    }

    throw new IllegalStateException("Storage is full");
  }

  public Lead[] findAll() {

    int count = 0;

    for (int index = 0; index < leads.length; index++) {

      if (leads[index] != null) {
        count++;
      }
    }

    Lead[] result = new Lead[count];
    int resultIndex = 0;

    for (int index = 0; index < leads.length; index++) {

      if (leads[index] != null) {
        result[resultIndex] = leads[index];
        resultIndex++;
      }
    }

    return  result;
  }

  public int size() {

    int count = 0;

    for (int index = 0; index < leads.length; index++) {

      if (leads[index] != null) {
        count++;
      }
    }

    return  count;
  }
}
