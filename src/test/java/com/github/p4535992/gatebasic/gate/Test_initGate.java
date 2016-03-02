package com.github.p4535992.gatebasic.gate;

import com.github.p4535992.gatebasic.gate.gate8.*;
import com.github.p4535992.gatebasic.object.MapAnnotation;
import com.github.p4535992.gatebasic.object.MapAnnotationSet;
import com.github.p4535992.gatebasic.object.MapDocument;
import gate.CorpusController;
import gate.Document;
import gate.util.DocumentProcessor;
import gate.util.GateException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 4535992 on 12/02/2016.
 * @author 4535992.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath*:datasource-test.xml", "classpath*:applicationContext.xml"})
public class Test_initGate {

    /**
     * Method Set up and use GATE embedded .
     * @version 1.6.9
     * @throws MalformedURLException
     */
    @Test
    public void setUpGateEmbedded() throws MalformedURLException {
        //Class for process a document with GATE and get a result with only the st String value
        //name Document -> name AnnotationSet -> name Annotation -> string content.
        ExtractorInfoGate81 eig8 = ExtractorInfoGate81.getInstance();
        //create a list of annotation (you know they exists on the gate document,otherwise you get null result).....
        List<String> listAnn =  new ArrayList<>(Arrays.asList("MyRegione","MyPhone","MyFax","MyEmail","MyPartitaIVA",
                "MyLocalita","MyIndirizzo","MyEdificio","MyProvincia"));
        //create a list of annotationSet (you know they exists on the gate document,otherwise you get null result).....
        List<String> listAnnSet = new ArrayList<>(Arrays.asList("MyFOOTER","MyHEAD","MySpecialID","MyAnnSet"));
        //Extract all the information given from a URL in a specific object,
        //there are method for works with java.io.File File or Directory
        //Store the result on of the extraction on a GateSupport Object
        Gate8Kit gate8Kit = Gate8Kit.getInstance();
        CorpusController controller = gate8Kit.setUpGateEmbedded(System.getProperty("user.dir")+ File.separator+"gate_files",
                "plugins", "gate.xml", "user-gate.xml", "gate.session",
                "custom/gapp/geoLocationPipeline06102014v7_fastMode.xgapp");
        GateSupport2 support = GateSupport2.getInstance(
                eig8.extractorGATE(
                        new URL("http://www.unifi.it"),controller,"corpus_test_1",listAnnSet,listAnn,true)
        );
        //Now you can get the content from a specific document, specific AnnotationSet, specific Annotation.
        String content0 = support.getSingleContent("http://www.unifi.it", "MyAnnSet", "MyIndirizzo"); // "P.azza Guido Monaco"
        String content1 = support.getSingleContent(0,"MyAnnSet", "MyIndirizzo"); // "P.azza Guido Monaco"
        String content2 = support.getSingleContent(0,0,"MyIndirizzo"); // "P.azza Guido Monaco"
        String content3 = support.getSingleContent(0,0,0); // "P.azza Guido Monaco"
        //GeoDocument geoDoc = web.convertGateSupportToGeoDocument(support,new URL("http://www.unifi.it"),0);
        String s = "";
    }

   /* @Test
    public void setUpBeansKitWithClassPath() throws IOException {
        //File file = BeansKit.getResourceAsFile(referencePathResourceFile,Gate8Controller.class);
        //String path = FileUtil.convertFileToUri(file).toString();
        // load an application context from definitions in a file e.g. beans.xml
        ApplicationContext ctx = BeansKit.tryGetContextSpring("gate/gate-beans.xml",Test_initGate.class);
        //GATE provides a DocumentProcessor interface suitable for use with Spring pooling
        DocumentProcessor procDoc = BeansKit.getBeanFromContext("documentProcessor",DocumentProcessor.class,ctx);
        String ss="";
    }*/

   /* @Test
    public void setUpBeansKitWithFile() throws IOException {
        // load an application context from definitions in a file e.g. beans.xml
        ApplicationContext ctx = BeansKit.tryGetContextSpring("gate/gate-beans-file.xml",Test_initGate.class);
        //GATE provides a DocumentProcessor interface suitable for use with Spring pooling
        //procDoc = ctx.getBean("documentProcessor", DocumentProcessor.class);
        DocumentProcessor procDoc = BeansKit.getBeanFromContext("documentProcessor",DocumentProcessor.class,ctx);
        String ss="";
    }*/

    @Test
    public void setUpGateEmbeddedWithAnnotationSpring() throws IOException, GateException {
        //Class for process a document with GATE and get a result with only the st String value
        //name Document -> name AnnotationSet -> name Annotation -> string content.
        ExtractorInfoGate81 eig8 = ExtractorInfoGate81.getInstance();
        //create a list of annotation (you know they exists on the gate document,otherwise you get null result).....
        List<String> listAnn =  new ArrayList<>(Arrays.asList("MyRegione","MyPhone","MyFax","MyEmail","MyPartitaIVA",
                "MyLocalita","MyIndirizzo","MyEdificio","MyProvincia"));
        //create a list of annotationSet (you know they exists on the gate document,otherwise you get null result).....
        List<String> listAnnSet = new ArrayList<>(Arrays.asList("MyFOOTER","MyHEAD","MySpecialID","MyAnnSet"));
        //Extract all the information given from a URL in a specific object,
        //there are method for works with java.io.File File or Directory
        //Store the result on of the extraction on a GateSupport Object
        Gate8Kit gate8Kit = Gate8Kit.getInstance();
        DocumentProcessor procDoc = gate8Kit.setUpGateEmbeddedWithSpring("gate/gate-beans.xml");
        //Document doc = gateCorpus.createDocByUrl(new URL("http://www.samsung.com/it/home"));
        GateSupport2 support = GateSupport2.getInstance(
                eig8.extractorGATE(
                        new URL("http://www.unifi.it"),procDoc,"corpus_test_1",listAnnSet,listAnn,true)
        );
        //Now you can get the content from a specific document, specific AnnotationSet, specific Annotation.
        //or loop by using a index int.
        String content0 = support.getSingleContent("http://www.unifi.it", "MyAnnSet", "MyIndirizzo"); // "P.azza Guido Monaco"
        String content1 = support.getSingleContent(0,"MyAnnSet", "MyIndirizzo"); // "P.azza Guido Monaco"
        String content2 = support.getSingleContent(0,0,"MyIndirizzo"); // "P.azza Guido Monaco"
        String content3 = support.getSingleContent(0,0,0); // "+39 055 27571"
        //Other function with some wrapped object of GATE API.

        //Get Documents on the First Corpus saved on the GateSupport wrapped object
        List<MapDocument> mapDocument = support.getCorpus(0);
        List<String> contents = null;
        for(MapDocument doc: mapDocument) {
            //Get AnnotationSet on the first Document of the Corpus saved on the GateSupport wrapped object
            List<MapAnnotationSet> mapAnnotationSets = support.getDocument(0);
            for(MapAnnotationSet annSet: mapAnnotationSets){
                //Get Annotation from AnnotationSet on the first Document of the Corpus saved on the GateSupport wrapped object
                List<MapAnnotation> mapAnnotations = support.getAnnotationSet(0);
                for(MapAnnotation ann: mapAnnotations){
                    //Get List of Content Annotation from AnnotationSet on the
                    // first Document of the Corpus saved on the GateSupport wrapped object
                    contents = support.getContent(0,0,0);
                    break;
                }
                break;
            }
            break;
        }
        String content4 = contents.get(0); // "+39 055 27571"
    }

    @Test
    public void setUpWithGateAPI() throws MalformedURLException, GateException {
        Gate8Kit gate8Kit = Gate8Kit.getInstance();
        DocumentProcessor procDoc = gate8Kit.setUpGateEmbeddedWithSpring("gate/gate-beans.xml");
        Document doc = GateCorpus8Kit.getInstance().createDoc(new URL("http://www.unifi.it"));
        procDoc.processDocument(doc);

    }

    /*
    @Test
    public void setUpGateEmbeddedWithGateProcessorAndJMS() throws MalformedURLException {
        GateCorpus8Kit gateCorpus = GateCorpus8Kit.getInstance();
        Gate8Kit g8 = Gate8Kit.getInstance();
        //Create the document from a url...
        //doc = Factory.newDocument(new URL("http://www.unifi.it"));
        DocumentProcessor procDoc = g8.setUpGateEmbeddedWithSpring("gate/gate-beans.xml",Test_initGate.class,"documentProcessor");
        Document doc = gateCorpus.createDocByUrl(new URL("http://www.samsung.com/it/home"));
        // in worker threads. . .
        //procDoc.processDocument(doc);
        //Map<String,AnnotationSet> ass = doc.getNamedAnnotationSets();
        GATEProcessor proc = new GATEProcessor();
        proc.setDocumentProcessor(procDoc);
        proc.receive(doc);
    }
    */
}
