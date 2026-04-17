package ru.mentee.power.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "ru.mentee.power.crm")
@EnableFeignClients
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
