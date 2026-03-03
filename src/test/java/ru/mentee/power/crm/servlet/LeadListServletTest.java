package ru.mentee.power.crm.servlet;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class LeadListServletTest {

  @Mock
  private HttpServletRequest mockReq;
  private HttpServletResponse mockResp;
  private ServletContext mockContext;

  @BeforeEach
  void setUp () {

  }

  @Test
  void shouldReturnHtmlTableWhenDoGetCalled() {

  }

  @Test
  void shouldSetContentTypeToHtmlWhenDoGetCalled() {

  }

}