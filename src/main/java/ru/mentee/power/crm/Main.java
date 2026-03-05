package ru.mentee.power.crm;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;

public class Main {
  static void main() throws Exception {
    LeadRepository leadRepository = new InMemoryLeadRepository();

    LeadService leadService = new LeadService(leadRepository);

    leadService.addLead("text1@example.ru", "FirstComp", LeadStatus.NEW);
    leadService.addLead("text2@example.ru", "SecondComp", LeadStatus.NEW);
    leadService.addLead("text3@example.ru", "ThirdComp", LeadStatus.NEW);
    leadService.addLead("text4@example.ru", "ForthComp", LeadStatus.NEW);
    leadService.addLead("text5@example.ru", "FivesComp", LeadStatus.NEW);
    leadService.addLead("<script>alert('XSS')</script>", "XssComp", LeadStatus.NEW);

    Tomcat tomcat = new Tomcat();
    tomcat.setPort(8080);

    Context context = tomcat.addContext("", new File(".").getAbsolutePath());
    context.getServletContext().setAttribute("LeadService", leadService);

    tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());
    context.addServletMappingDecoded("/leads", "LeadListServlet");

    tomcat.start();

    System.out.println("Tomcat started on port" + tomcat.getConnector().getLocalPort());
    System.out.println("Open http://" + tomcat.getHost().getName() + ":8080/leads in browser");

    tomcat.getServer().await();
  }
}
