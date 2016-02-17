package com.github.p4535992.gatebasic.gate;

import com.github.p4535992.gatebasic.gate.gate8.GateCorpus8Kit;
import com.github.p4535992.gatebasic.gate.jms.GATEProcessor;

import com.github.p4535992.gatebasic.util.BeansKit;
import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.util.DocumentProcessor;
import gate.util.GateException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4535992 on 24/06/2015.
 */
public class Test_intiGateSpring {
    /** Gate Document*/
    private static Document doc;
    /** Gate processor document */
    private static DocumentProcessor procDoc;

    private static Test_intiGateSpring instance = null;
    protected Test_intiGateSpring(){}

    public static Test_intiGateSpring getInstance(){
        if(instance == null) {
            instance = new Test_intiGateSpring();
        }
        return instance;
    }

    public void initGateWithSpring(String referencePathResourceFile) throws GateException, IOException {
        //File file = BeansKit.getResourceAsFile(referencePathResourceFile,Gate8Controller.class);
        //String path = FileUtil.convertFileToUri(file).toString();
        // load an application context from definitions in a file e.g. beans.xml
        ApplicationContext ctx = BeansKit.tryGetContextSpring(referencePathResourceFile,Test_intiGateSpring.class);
        //GATE provides a DocumentProcessor interface suitable for use with Spring pooling
        //procDoc = ctx.getBean("documentProcessor", DocumentProcessor.class);
        procDoc = BeansKit.getBeanFromContext("documentProcessor",DocumentProcessor.class,ctx);
    }

    /**
        If you need to duplicate other resources, use the two-argument
        Factory.duplicate, passing the ctx as the second parameter, to preserve object graph
        two calls to Factory.duplicate(r, ctx) for the same resource r in the same context ctx will return the same duplicate.
        calls to the single argument Factory.duplicate(r) or to the
        two-argument version with different contexts will return different duplicates.
        Can call the default duplicate algorithm (bypassing the CustomDuplication check) via Factory.defaultDuplicate
        it is safe to call defaultDuplicate(this, ctx), but calling duplicate(this, ctx) from within its own custom
        duplicate will cause infinite recursion!
     */
    public Resource duplicate(Factory.DuplicationContext ctx,List<ProcessingResource> prList)throws ResourceInstantiationException {
         // duplicate this controller in the default way - this handles subclasses nicely
         Controller c = (Controller) Factory.defaultDuplicate((Resource) this, ctx);
            // duplicate each of our PRs
             List<ProcessingResource> newPRs = new ArrayList<>();
            for(ProcessingResource pr : prList) {
                 newPRs.add((ProcessingResource)Factory.duplicate(pr, ctx));
            }
             // and set this duplicated list as the PRs of the copy
            c.setPRs(newPRs);
        return c;
    }

   /* private AtomicInteger totalCount;

    public AtomicInteger getTotalCount() {
         return totalCount;
    }

    @Sharable
    public void setTotalCount(AtomicInteger tc) {
         this.totalCount = tc;
    }

    public Resource init() throws ResourceInstantiationException {
         if(totalCount == null) {
             totalCount = new AtomicInteger(0);
         }
         return this;
     }

    public void reInit() throws ResourceInstantiationException {
        totalCount = null;
        super.reInit();
    }*/

    public static void main(String[] args) throws IOException, GateException {
        GateCorpus8Kit gateCorpus = GateCorpus8Kit.getInstance();
        Test_intiGateSpring gateController =  Test_intiGateSpring.getInstance();
        //Create the document from a url...
        //doc = Factory.newDocument(new URL("http://www.unifi.it"));
        gateController.initGateWithSpring("gate/gate-beans.xml");

        doc = gateCorpus.createDocByUrl(new URL("http://www.samsung.com/it/home"));
        // in worker threads. . .
        //procDoc.processDocument(doc);
        //Map<String,AnnotationSet> ass = doc.getNamedAnnotationSets();
        GATEProcessor proc = new GATEProcessor();
        proc.setDocumentProcessor(procDoc);
        proc.receive(doc);
    }

}
