package ru.mentee.power.crm.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class CustomerTest {

  @Test
  void shouldReuseContactWhenCreatingCustomer() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Address billingAddress = new Address("NY", "64 Main st.", "123456");

    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Customer customer = new Customer(UUID.randomUUID(), contact, billingAddress, "SILVER");

    assertThat(customer.contact().address()).isNotEqualTo(customer.billingAddress());
  }

  @Test
  void shouldDemonstrateContactReuseAcrossLeadAndCustomer() {

    Address address = new Address("NY", "123 Main st.", "123456");
    Address billingAddress = new Address("NY", "64 Main st.", "123456");

    Contact contact = new Contact("mail@example.ru", "+71234567890", address);

    Customer customer = new Customer(UUID.randomUUID(), contact, billingAddress, "SILVER");
    Lead lead = new Lead(UUID.randomUUID(), contact, "BigTech", "NEW");

    assertThat(lead.contact()).isEqualTo(customer.contact());
  }

}