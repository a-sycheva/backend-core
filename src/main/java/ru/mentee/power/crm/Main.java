package ru.mentee.power.crm;

import ru.mentee.power.crm.web.HelloCrmServer;

public class Main {
  static void main() throws Exception {
    int port = 8080;
    HelloCrmServer server = new HelloCrmServer(port);

    server.start();
    Thread.currentThread().join();
    Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
  }
}
