package ru.mentee.power.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.mentee.power.crm")
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
