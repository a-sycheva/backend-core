package ru.mentee.power.crm.spring.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class FieldInjectionProblemTest {

  @Test
  void fieldInjectionCausesNullPointerWithoutSpring() {
    DemoController controller = new DemoController(null);

    // controller.demo(); не содержит вызова у null
    // поэтому нет NPE

    assertThat(controller)
        .extracting("constructorService","fieldRepository", "setterService")
        .containsOnlyNulls();
  }
}
