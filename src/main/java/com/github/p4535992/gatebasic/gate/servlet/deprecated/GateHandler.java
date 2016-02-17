package com.github.p4535992.gatebasic.gate.servlet.deprecated;

import gate.*;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import org.springframework.web.HttpRequestHandler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple HttpRequestHandler that uses a GATE application to process
 * some text as a GATE document and render the document's features as an
 * HTML table.
 */
@SuppressWarnings("unused")
public class GateHandler implements HttpRequestHandler {

  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(GateHandler.class);

  /**
   * Atomic counter that we use to obtain a unique ID for each handler
   * instance.
   */
  private static AtomicInteger nextId = new AtomicInteger(1);

  /**
   * The ID of this handler instance.
   */
  private int handlerId;

  /**
   * The application that will be run.
   */
  private CorpusController application;

  /**
   * A corpus that will be used to hold the document being processed.
   */
  private Corpus corpus;

  /**
   * Set the application that will be run over the documents.
   * @param application corpus controller of gate.
   */
  public void setApplication(CorpusController application) {
    this.application = application;
  }

  /**
   * Create the corpus. The PostConstruct annotation means that this
   * method will be called by spring once the handler object has been
   * constructed and its properties (i.e. the application) have been
   * set.
   * @throws Exception error.
   */
  @PostConstruct
  public void init() throws Exception {
    handlerId = nextId.getAndIncrement();
    logger.info("init() for GateHandler " + handlerId);
    // insert a corpus and give it to the spring.mvc.home.home.initializer.org.p4535992.mvc.webapp.controller
    corpus = Factory.newCorpus("org.p4535992.mvc.webapp corpus");
    application.setCorpus(corpus);
  }

  /**
   * Clean-up method. The PreDestroy annotation means that Spring will
   * call the method when the object is no longer required.
   * @throws Exception error.
   */
  @PreDestroy
  public void cleanup() throws Exception {
    logger.info("cleanup() for GateHandler " + handlerId);
    Factory.deleteResource(corpus);
    Factory.deleteResource(application);
  }

  /**
   * Handle a request.
   */
  public void handleRequest(HttpServletRequest request,
          HttpServletResponse response) throws ServletException, IOException {
    logger.info("Handler " + handlerId + " handling request");
    // we take the text to annotate from a form field
    String text = request.getParameter("text");
    // the form also allows you to provide a mime type
    String mime = request.getParameter("mime");
    
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

    Document doc;
    try {
      logger.debug("Creating document");
      doc = (Document)Factory.createResource("gate.corpora.DocumentImpl",
                  Utils.featureMap("stringContent", text, "mimeType", mime));
    }
    catch(ResourceInstantiationException e) {
      failureMessage("Could not insert GATE document for input text", e,
              response);
      return;
    }
    try {
      corpus.add(doc);
      logger.info("Executing application");
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
      logger.info("Deleting document");
      Factory.deleteResource(doc);
    }
  }

  /**
   * Render the document's features in an HTML table.
   */
  private void successMessage(Document doc, HttpServletResponse response)throws IOException {
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
   * Simple org.p4535992.mvc.error handler - you would obviously use something more
   * sophisticated in a real application.
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

