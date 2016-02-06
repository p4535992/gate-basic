package com.github.p4535992.gatebasic.gate.servlet.deprecated;

import com.github.p4535992.gatebasic.gate.gate8.GateCorpus8Kit;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.creole.ExecutionException;
import org.springframework.web.HttpRequestHandler;
import javax.annotation.PreDestroy;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple HttpRequestHandler that uses a GATE application to process
 * some text as a GATE document and render the document's features as an
 * HTML table.
 */
@SuppressWarnings("unused")
public class MyGateHandler implements HttpRequestHandler {
  
  private static List<URL> listaUrl= new ArrayList<>();

  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(MyGateHandler.class);

  private static GateCorpus8Kit create = GateCorpus8Kit.getInstance();

  /**Atomic counter that we use to obtain a unique ID for each handler instance.*/
  private static AtomicInteger nextId = new AtomicInteger(1);
  /**The ID of this handler instance.*/
  private int handlerId;
  /**The application that will be run. */
  private CorpusController application;
  /**A corpus that will be used to hold the document being processed.*/
  private Corpus corpus;
  /**
   * Method to Set the application that will be run over the documents.
   * @param application corpus controller gate.
   */
  public void setApplication(CorpusController application) {this.application = application; }

  /*
   * Create the corpus. The PostConstruct annotation means that this
   * method will be called by spring once the handler object has been
   * constructed and its properties (i.e. the application) have been
   * set.
   * @throws Exception error.
   */
  /*@PostConstruct
  public void init() throws Exception {
    IWebsiteDao websiteDao = new WebsiteDaoImpl();
    websiteDao.setTableSelect("website");
    websiteDao.setDriverManager(
            "com.sql.jdbc.Driver",
            "jdbc:sql",
            "localhost",
            "3306",
            "siimobility",
            "siimobility",
            "urldb");

    listaUrl = (ArrayList<URL>) websiteDao.selectAllUrl("url",8, 0);
    for(int i = 0; i < listaUrl.size(); i++) {System.out.println("url["+i+"]:"+listaUrl.get(i));}
    //init GATE
     try {
       // insert a GATE corpus and add a document for each command-line argument
       corpus = Factory.newCorpus("MainPipeline corpus");
       corpus = create.createCorpusByListOfUrls(listaUrl, corpus.getName());
       System.err.println("Fine caricamento della PIPELINE");
       //spring.mvc.home.home.initializer.org.p4535992.mvc.webapp.controller.setCorpus(corpus2);
       application.setCorpus(corpus);//set corpus
       System.err.println("...START");
       application.execute();
       //spring.mvc.home.home.initializer.org.p4535992.mvc.webapp.controller.execute();//execute the corpus fatto da spring
     }catch (Exception e){
       e.printStackTrace();
     }
  }*/

  /*
   * Clean-up method. The PreDestroy annotation means that Spring will
   * call the method when the object is no longer required.
   */
  @PreDestroy
  public void cleanup() {
    logger.info("cleanup() for GateHandler " + handlerId);
    Factory.deleteResource(corpus);
    Factory.deleteResource(application);
  }

  /**
   * Handle a request.
   * @param request request http.
   * @param response response htttp.
   * @throws ServletException error.
   * @throws IOException error.
   */
  public void handleRequest(HttpServletRequest request,
          HttpServletResponse response) throws ServletException, IOException {
    logger.info("Handler " + handlerId + " handling request");
//    // we take the text to annotate from a form field
//    String text = request.getParameter("text");
//    // the form also allows you to provide a mime type
//    String mime = request.getParameter("mime");
    
    // delay parameter to fake a long-running process
    int delay = 0;
    String delayParam = request.getParameter("delay");
    if(delayParam != null) {
      try {
        delay = Integer.parseInt(delayParam);
      }
      catch(NumberFormatException e) {
        logger.warn("Failed to parse delay value " + delayParam + ", ignored", e);
      }
    }

      Document doc = corpus.get(0);
    try {
      application.execute();
      // fake a long-running application by sleeping for delay seconds
      try {
        Thread.sleep(delay * 1000);
      }
      catch(InterruptedException e) {
        // re-interrupt self
        Thread.currentThread().interrupt();
      }
      logger.info("Application completed");
      successMessage(doc, response);
    }
    catch(ExecutionException e) {
      failureMessage("Error occurred which executing GATE application", e,response);
    }
    finally {
      // remember to do the clean-up tasks in a finally
      corpus.clear();
       logger.info("Deleting corpus");
      logger.info("Deleting document");
      Factory.deleteResource(doc);
    }
  }
  
  
  /**
   * Render the document's features in an HTML table.
   * @param doc document gate.
   * @param response response http.
   * @throws IOException error.
   */
  private void successMessage(Document doc, HttpServletResponse response)
          throws IOException {
    response.setContentType("text/html");
    PrintWriter w = response.getWriter();
    w.println("<html>");
    w.println("<head>");
    w.println("<title>Results - GATE handler " + handlerId + "</title>");
    w.println("</head>");
    w.println("<body>");
    w.println("<h1>Document features: GATE handler " + handlerId + "</h1>");
    w.println("<table>");
    w.println("<tr><td><b>Name</b></td><td><b>Value</b></td></tr>");
    for(Map.Entry<Object, Object> entry : doc.getFeatures().entrySet()) {
      w.println("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue()
              + "</td></tr>");
    }
    w.println("</table>");
    w.println("</body>");
    w.println("</html>");
  }

  /**
   * Simple error handler - you would obviously use something more
   * sophisticated in a real application.
   * @param message string messsage.
   * @param e exception to print. 
   * @param response response http.
   * @throws IOException error.
   */
  private void failureMessage(String message, Exception e,
          HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    PrintWriter w = response.getWriter();
    w.println("<html>");
    w.println("<head>");
    w.println("<title>Error - GATE handler " + handlerId + "</title>");
    w.println("</head>");
    w.println("<body>");
    w.println("<h1>Error in GATE handler " + handlerId + "</h1>");
    w.println("<p>" + message + "</p>");
    if(e != null) {
      w.println("<pre>");
      e.printStackTrace(w);
      w.println("</pre>");
    }
    w.println("</body>");
    w.println("</html>");
  }
  
  
  
}