package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.util.file.FileUtil;
import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringKit;
import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.corpora.DocumentImpl;
import gate.creole.ExecutionException;
import gate.util.DocumentProcessor;
import gate.util.GateException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 24/06/2015.
 * @author 4535992
 * @version 2015-06-25
 */
@SuppressWarnings("unused")
public class ExtractorInfoGate8 {

    private Corpus corpus;
    private Map<String,Map<String,Map<String,String>>> mapContentDocs;


    private static ExtractorInfoGate8 instance = null;
    protected ExtractorInfoGate8(){
        mapContentDocs = new HashMap<>();
    }

    public static ExtractorInfoGate8 getInstance(){
        if(instance == null) {
            instance = new ExtractorInfoGate8();
        }
        return instance;
    }

    /** Tell GATE's spring.mvc.home.home.initializer.org.p4535992.mvc.webapp.controller about the corpus you want to run on.
     *  @param corpus corpus gate to set.
     *  @param controller corpus controller gate to set.
     */
    public void setCorpus(Corpus corpus, CorpusController controller) {controller.setCorpus(corpus);} // setCorpus

    /**
     * Run GATE.
     * @param controller corpus controller gate to set.
     * @throws GateException gate exception.
     */
    public void execute(CorpusController controller) throws GateException {controller.execute();} // execute()

    /** Run GATE. */
    private int exeTentative = 0;
    public void executeWithTentatives(CorpusController controller) throws GateException {
        try{
            controller.execute(); // execute()
        }catch(ExecutionException ee){
            if(exeTentative < 3){
                exeTentative ++;
                execute(controller);
            }else{
                SystemLog.warning("No sentences or tokens to process in some gate documents");
            }
        }catch(OutOfMemoryError e){
            if(exeTentative < 3){
                exeTentative ++;
                execute(controller);
            }else{
                SystemLog.warning("Exception in thread \"AWT-EventQueue-0\" ava.lang.OutOfMemoryError: Java heap space");
            }
        }
    }

    /**
     * Method to read all the processed result of GATE on the document referenced from the url.
     * @param url the url address to the document you want to analize.
     * @param controller a corpus controller with loaded a gapp file.
     * @param nameCorpus (optional) name of the corpus gate.
     * @param nameAnnotations (optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet (optional) list of annotationSet you want to get from the document
     *                           if {@param firstAndExit} is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the document.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            URL url, CorpusController controller,String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit) {
        GateCorpus8Kit gc8 = GateCorpus8Kit.getInstance();
        try{
            if(url!=null){
                if(StringKit.isNullOrEmpty(nameCorpus)) {
                    corpus = gc8.createCorpusByUrl(url, "GeoDocuments Corpus");
                }else{
                    corpus = gc8.createCorpusByUrl(url, nameCorpus);
                }
            }//if url!=null
            if(corpus == null){return null;}
            else{
                setCorpus(corpus, controller);
                SystemLog.message("Execute of GATE in process for the url " + url + "...");
                execute(controller);
                SystemLog.message("...GATE is been processed");
                SystemLog.message("Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException|IOException ex){
            SystemLog.exception(ex);
        }
        finally{
            cleanup();
        }
        return null;
    }//extractorGATE

    /**
     * Method to read all the processed result of GATE on the document referenced from the url.
     * @param url the url address to the document you want to analize.
     * @param docProcessor a document processor with loaded a gapp file using Spring framework.
     * @param nameCorpus (optional) name of the corpus gate.
     * @param nameAnnotations (optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet (optional) list of annotationSet you want to get from the document
     *                           if {@param firstAndExit} is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the document.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            URL url, DocumentProcessor docProcessor, String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit){
        GateCorpus8Kit gc8 = GateCorpus8Kit.getInstance();
        Document doc = new DocumentImpl();
        try{
            if(url!=null){
                doc = gc8.createDoc(url);
            }//if url!=null
            SystemLog.message("Execute of GATE in process for the url " + url + "...");
            docProcessor.processDocument(doc);
            SystemLog.message("...GATE is been processed");
            if(StringKit.isNullOrEmpty(nameCorpus)) {
                corpus = gc8.createCorpusByDocument(doc, "GeoDocuments Corpus");
            }else{
                corpus = gc8.createCorpusByDocument(doc, nameCorpus);
            }
            if(corpus == null){return null;}
            else{
                SystemLog.message("Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException ex){
            SystemLog.exception(ex);
        }
        finally{
            cleanup();
        }
        return null;
    }//extractorGATE

    /**
     * Method to read all the processed result of GATE on the document referenced from a list of urls.
     * @param listUrl list of the url address to the documents you want to analize.
     * @param controller a corpus controller with loaded a gapp file.
     * @param nameCorpus (optional) name of the corpus gate.
     * @param nameAnnotations (optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet (optional) list of annotationSet you want to get from the document
     *                           if {@param firstAndExit} is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the documents.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            List<URL> listUrl, CorpusController controller,String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit) {
        GateCorpus8Kit gc8 = GateCorpus8Kit.getInstance();
        try{
            for(URL url : listUrl) {
                if (url != null) {
                    SystemLog.message("Execute of GATE in process for the url " + url + "...");
                    if (StringKit.isNullOrEmpty(nameCorpus)) {
                        corpus = gc8.createCorpusByUrl(url, "GeoDocuments Corpus");
                    } else {
                        corpus = gc8.createCorpusByUrl(url, nameCorpus);
                    }
                }//if url!=null
            }
            if(corpus == null){return null;}
            else{
                setCorpus(corpus, controller);

                execute(controller);
                SystemLog.message("...GATE is been processed");
                SystemLog.message("Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException|IOException ex){
            SystemLog.exception(ex);
        }
        finally{
            cleanup();
        }
        return null;
    }

    /**
     * Method to read all the processed result of GATE on the document referenced from a list of urls.
     * @param listUrl list of the url address to the documents you want to analize.
     * @param docProcessor a document processor with loaded a gapp file using Spring framework.
     * @param nameCorpus (optional) name of the corpus gate.
     * @param nameAnnotations (optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet (optional) list of annotationSet you want to get from the document
     *                           if {@param firstAndExit} is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the documents.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            List<URL> listUrl,DocumentProcessor docProcessor,String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit) {
        GateCorpus8Kit gc8 = GateCorpus8Kit.getInstance();
        Document doc = new DocumentImpl();
        try{

            if (StringKit.isNullOrEmpty(nameCorpus)) {
                corpus = Factory.newCorpus("GeoDocuments Corpus");
            } else {
                corpus = Factory.newCorpus(nameCorpus);
            }
            for(URL url: listUrl) {
                try {
                    if (url != null) {
                        doc = gc8.createDoc(url);
                    }//if url!=null
                    SystemLog.message("Execute of GATE in process for the url " + url + "...");
                    docProcessor.processDocument(doc);
                    SystemLog.message("...GATE is been processed");
                    corpus.add(doc);
                } catch(GateException ex){
                    SystemLog.warning(ex.getMessage());
                }
            }
            if(corpus == null){return null;}
            else{
                SystemLog.message("Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException ex){
            SystemLog.exception(ex);
        }
        finally{
            cleanup();
        }
        return null;
    }

    /**
     * Method to read all the processed result of GATE on a Corpus.
     * @param corpus corpus gate we want analize.
     * @param nameAnnotations (optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet (optional) list of annotationSet you want to get from the document
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the documents of the corpus.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            Corpus corpus,List<String> nameAnnotations,List<String> nameAnnotationsSet,boolean firstAndExit){
        //nameAnnotations.add("MyIndirizzo");
        //nameAnnotationsSet.add("MyAnnSet");
        GateAnnotation8Kit ga8 = GateAnnotation8Kit.getInstance();
        //Set the global variable here...
        mapContentDocs = ga8.getAllAnnotationInfo(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
        return mapContentDocs;
    }

    /**
     * Method to read all the processed result of GATE on the document referenced from the url to a local file.
     * @param fileOrDirectory the url address to a local file document you want to analize.
     * @param controller a corpus controller with loaded a gapp file.
     * @param nameCorpus (optional) name of the corpus gate.
     * @param nameAnnotations (optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet (optional) list of annotationSet you want to get from the document
     *                           if {@param firstAndExit} is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the document.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            File fileOrDirectory, CorpusController controller,String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit) {

        if(FileUtil.isDirectory(fileOrDirectory)){
            List<File> listFiles = FileUtil.readDirectory(fileOrDirectory);
            List<URL> listUrl = new ArrayList<>();
            for(File file: listFiles) {
                try {
                    URL url = FileUtil.convertFileToURL(file);
                    listUrl.add(url);
                } catch (MalformedURLException e) {
                    SystemLog.warning(e.getMessage());
                }
            }
            return extractorGATE(listUrl, controller,nameCorpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
        }else{
            URL url;
            try {
                url = FileUtil.convertFileToURL(fileOrDirectory);
            } catch (MalformedURLException e) {
                SystemLog.warning(e.getMessage());
                return null;
            }
            return extractorGATE(url, controller,nameCorpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
        }
    }

    /**
     * Method to read all the processed result of GATE on the document referenced from the url to a local file.
     * @param fileOrDirectory the url address to a local file document you want to analize.
     * @param docProcessor a document processor with loaded a gapp file using Spring framework.
     * @param nameCorpus (optional) name of the corpus gate.
     * @param nameAnnotations (optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet (optional) list of annotationSet you want to get from the document
     *                           if {@param firstAndExit} is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the document.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            File fileOrDirectory, DocumentProcessor docProcessor, String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit){
        if(FileUtil.isDirectory(fileOrDirectory)){
            List<File> listFiles = FileUtil.readDirectory(fileOrDirectory);
            List<URL> listUrl = new ArrayList<>();
            for(File file: listFiles) {
                try {
                    URL url = FileUtil.convertFileToURL(file);
                    listUrl.add(url);
                } catch (MalformedURLException e) {
                    SystemLog.warning(e.getMessage());
                }
            }
            return extractorGATE(listUrl, docProcessor,nameCorpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
        }else{
            URL url;
            try {
                url = FileUtil.convertFileToURL(fileOrDirectory);
            } catch (MalformedURLException e) {
                SystemLog.warning(e.getMessage());
                return null;
            }
            return extractorGATE(url, docProcessor,nameCorpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
        }
    }


    /**
     * Method for clean up the gate corpus.
     */
    public void cleanup() {
        if(corpus!=null) {
            Factory.deleteResource(corpus);
        }
    }

    /*
     * Method for convert a extrator gate information to a GeoDocument.
     * @param mapExtractor map of informations estract from gate.
     * @param url url of the web page.
     * @return a geodocument fulled with information.
     */
    /*public GeoDocument convertMapExtractorToGeoDocument(Map<String,Map<String,String>> mapExtractor,String url){
        GeoDocument geoDoc = new GeoDocument();
        try {
            geoDoc.setUrl(new URL(url));
            for(Map.Entry<String,Map<String,String>> entryAnnSet: mapExtractor.entrySet()){
                Map<String,String> entryAnn = entryAnnSet.getValue();
                for(Map.Entry<String,String> entry: entryAnn.entrySet()){
                    switch (entry.getKey()){
                        case "MyRegione":{
                            if(!StringKit.isNullOrEmpty(entry.getValue()) &&
                               StringKit.isNullOrEmpty(geoDoc.getRegione())) {
                                geoDoc.setRegione(entry.getValue());
                            }
                        }
                        case "MyPhone":
                            if(!StringKit.isNullOrEmpty(entry.getValue()) &&
                                    StringKit.isNullOrEmpty(geoDoc.getTelefono())) {
                                geoDoc.setTelefono(entry.getValue());
                            }
                        case "MyFax":
                            if(!StringKit.isNullOrEmpty(entry.getValue()) &&
                                    StringKit.isNullOrEmpty(geoDoc.getFax())) {
                                geoDoc.setFax(entry.getValue());
                            }
                        case "MyEmail":
                            if(!StringKit.isNullOrEmpty(entry.getValue()) &&
                                    StringKit.isNullOrEmpty(geoDoc.getEmail())) {
                                geoDoc.setEmail(entry.getValue());
                            }
                        case "MyPartitaIVA":
                            if(!StringKit.isNullOrEmpty(entry.getValue()) &&
                                    StringKit.isNullOrEmpty(geoDoc.getIva())) {
                                geoDoc.setIva(entry.getValue());
                            }
                        case "MyLocalita":
                            if(!StringKit.isNullOrEmpty(entry.getValue()) &&
                                    StringKit.isNullOrEmpty(geoDoc.getCity())) {
                                geoDoc.setCity(entry.getValue());
                            }
                        case "MyIndirizzo":
                            if(!StringKit.isNullOrEmpty(entry.getValue()) &&
                                    StringKit.isNullOrEmpty(geoDoc.getIndirizzo())) {
                                geoDoc.setIndirizzo(entry.getValue());
                            }
                        case "MyEdificio":
                            if(!StringKit.isNullOrEmpty(entry.getValue()) &&
                                    StringKit.isNullOrEmpty(geoDoc.getEdificio())) {
                                geoDoc.setEdificio(entry.getValue());
                            }
                    }//switch
                }
            }
        } catch (MalformedURLException e) {
            SystemLog.exception(e);
        }
        return geoDoc;
    }*/
}
