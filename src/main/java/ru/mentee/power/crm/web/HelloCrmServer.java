package ru.mentee.power.crm.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HelloCrmServer {

  private final HttpServer server;

  public HelloCrmServer(int port) throws IOException {
    this.server = HttpServer.create(new InetSocketAddress(port), 0);
  }

  public void start() {
    server.createContext("/hello", new HelloHandler());
    server.start();
    System.out.println("Server started on http://localhost:" + server.getAddress().getPort());
  }

  public void stop() {
    server.stop(0);
  }

  static class HelloHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

      System.out.println("---");
      System.out.println("New Request: " + new java.util.Date());
      System.out.println("Method: " + exchange.getRequestMethod());
      System.out.println("Path: " + exchange.getRequestURI().getPath());
      System.out.println("---");

      String response = "<!DOCTYPE html><html><body><h1>Hello CRM!</h1></body></html>";

      exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

      exchange.sendResponseHeaders(200, response.length());

      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.close();

      exchange.close();
    }
  }
}