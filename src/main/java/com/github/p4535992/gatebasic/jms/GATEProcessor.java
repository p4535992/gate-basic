package com.github.p4535992.gatebasic.jms;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.util.DocumentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Map;

/**
 * Created by 4535992 on 17/04/2015.
 * USAGE:
 *   Document doc = ...;
 *   GATEProcessor proc = new GATEProcessor();
 *   proc.setDocumentProcessor(procDoc);
 *   proc.receive(doc);
 *
 * @author 4535992
 * @version 2015-06-25
 */
@SuppressWarnings("unused")
@Component
public class GATEProcessor {


    public GATEProcessor(){}

    @Autowired
    @Qualifier("documentProcessor")
    private DocumentProcessor documentProcessor;

    public DocumentProcessor getDocumentProcessor() {
        return documentProcessor;
    }

    public void setDocumentProcessor(DocumentProcessor documentProcessor) {
        this.documentProcessor = documentProcessor;
    }

    @Value("gate.queueName")
    private String queueName;

    @JmsListener(destination = "${gate.queueName}", concurrency = "${gate.numThreads}")
     public void receive(String message) {
        try {
            Document doc = Factory.newDocument(message);
            try {
                documentProcessor.processDocument(doc);
                // do whatever you need to do with the results
                Map<String, AnnotationSet> map = doc.getNamedAnnotationSets();
            } finally {
                Factory.deleteResource(doc);
            }
        } catch(Exception e) {
            // handle the exception somehow
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "${gate.queueName}", concurrency = "${gate.numThreads}")
    public void receive(Document doc) {
        try {
            try {
                documentProcessor.processDocument(doc);
                // do whatever you need to do with the results
                Map<String, AnnotationSet> map = doc.getNamedAnnotationSets();
            } finally {
                Factory.deleteResource(doc);
            }
        } catch(Exception e) {
            // handle the exception somehow
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "${gate.queueName}", concurrency = "${gate.numThreads}")
    public void receive(URL url) {
        try {
            Document doc = Factory.newDocument(url);
            try {
                documentProcessor.processDocument(doc);
                // do whatever you need to do with the results
                Map<String, AnnotationSet> map = doc.getNamedAnnotationSets();
            } finally {
                Factory.deleteResource(doc);
            }
        } catch(Exception e) {
            // handle the exception somehow
            e.printStackTrace();
        }
    }

}

