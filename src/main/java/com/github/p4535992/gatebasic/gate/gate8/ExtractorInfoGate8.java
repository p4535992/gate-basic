package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.util.file.FileUtilities;
import com.github.p4535992.util.string.StringUtilities;
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
 * @author 4535992.
 * @version 2015-11-12.
 */
@SuppressWarnings("unused")
public class ExtractorInfoGate8 {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ExtractorInfoGate8.class);

    private static String gm() {
        return Thread.currentThread().getStackTrace()[1].getMethodName()+":: ";
    }

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

    public Corpus getCorpus() {
        return corpus;
    }

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
                logger.warn(gm() + "No sentences or tokens to process in some gate documents");
            }
        }catch(OutOfMemoryError e){
            if(exeTentative < 3){
                exeTentative ++;
                execute(controller);
            }else{
                logger.warn(gm() + "Exception in thread \"AWT-EventQueue-0\" ava.lang.OutOfMemoryError: Java heap space");
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
     *                           if param firstAndExit is true the list priority is given from the
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
                if(StringUtilities.isNullOrEmpty(nameCorpus)) {
                    corpus = gc8.createCorpusByUrl(url, "GeoDocuments Corpus");
                }else{
                    corpus = gc8.createCorpusByUrl(url, nameCorpus);
                }
            }//if url!=null
            if(corpus == null){return null;}
            else{
                setCorpus(corpus, controller);
                logger.info(gm() + "Execute of GATE in process for the url " + url + "...");
                execute(controller);
                logger.info(gm() + "...GATE is been processed");
                logger.info(gm() + "Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException|IOException e){
            logger.error(gm() + e.getMessage(), e);
        }
        finally{
            cleanup();
            corpus.clear();
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
     *                           if param firstAndExit is true the list priority is given from the
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
            logger.info(gm() + "Execute of GATE in process for the url " + url + "...");
            docProcessor.processDocument(doc);
            logger.info(gm() + "...GATE is been processed");
            if(StringUtilities.isNullOrEmpty(nameCorpus)) {
                corpus = gc8.createCorpusByDocument(doc, "GeoDocuments Corpus");
            }else{
                corpus = gc8.createCorpusByDocument(doc, nameCorpus);
            }
            if(corpus == null){
                logger.warn("Can't work with the annotation because the Coprus object is NULL");
                return null;
            }
            else{
                logger.info(gm() + "Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException e){
            logger.error(gm() + e.getMessage(), e);
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
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the documents.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            List<URL> listUrl, CorpusController controller,String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit) {
        GateCorpus8Kit gc8 = GateCorpus8Kit.getInstance();
        try{
            logger.info(gm() + "Execute of GATE in process for a list of  " + listUrl.size() + " urls...");
            if (StringUtilities.isNullOrEmpty(nameCorpus)) {
                corpus = gc8.createCorpusByUrl(listUrl, "GeoDocuments Corpus");
            } else {
                corpus = gc8.createCorpusByUrl(listUrl, nameCorpus);
            }
            if(corpus == null){return null;}
            else{
                setCorpus(corpus, controller);
                execute(controller);
                logger.info(gm() + "...GATE is been processed");
                logger.info(gm() + "Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException|IOException e){
            logger.error(gm() + e.getMessage(), e);
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
     *                           if param firstAndExit is true the list priority is given from the
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
            if (StringUtilities.isNullOrEmpty(nameCorpus)) {
                corpus = Factory.newCorpus("GeoDocuments Corpus");
            } else {
                corpus = Factory.newCorpus(nameCorpus);
            }
            for(URL url: listUrl) {
                try {
                    if (url != null) {
                        doc = gc8.createDoc(url);
                    }//if url!=null
                    logger.info(gm() + "Execute of GATE in process for the url " + url + "...");
                    docProcessor.processDocument(doc);
                    logger.info(gm() + "...GATE is been processed");
                    corpus.add(doc);
                } catch(GateException e){
                    logger.warn(gm() + e.getMessage(),e);
                }
            }
            if(corpus == null){return null;}
            else{
                logger.info(gm() + "Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException e){
            logger.error(gm() + e.getMessage(), e);
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
        mapContentDocs.clear();
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
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the document.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            File fileOrDirectory, CorpusController controller,String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit) {
        if(FileUtilities.isDirectory(fileOrDirectory)){
            List<File> listFiles = FileUtilities.readDirectory(fileOrDirectory);
            List<URL> listUrl = new ArrayList<>();
            for(File file: listFiles) {
                try {
                    URL url = FileUtilities.toURL(file);
                    listUrl.add(url);
                } catch (MalformedURLException e) {
                    logger.warn(gm() + e.getMessage(), e);
                }
            }
            return extractorGATE(listUrl, controller,nameCorpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
        }else{
            URL url;
            try {
                url = FileUtilities.toURL(fileOrDirectory);
            } catch (MalformedURLException e) {
                logger.error(gm() + e.getMessage(), e);
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
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the document.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            File fileOrDirectory, DocumentProcessor docProcessor, String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit){
        if(FileUtilities.isDirectory(fileOrDirectory)){
            List<File> listFiles = FileUtilities.readDirectory(fileOrDirectory);
            List<URL> listUrl = new ArrayList<>();
            for(File file: listFiles) {
                try {
                    URL url = FileUtilities.toURL(file);
                    listUrl.add(url);
                } catch (MalformedURLException e) {
                    logger.warn(gm() + e.getMessage(), e);
                }

            }
            return extractorGATE(listUrl, docProcessor,nameCorpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
        }else{
            URL url;
            try {
                url = FileUtilities.toURL(fileOrDirectory);
            } catch (MalformedURLException e) {
                logger.error(gm() + e.getMessage(), e);
                return null;
            }
            return extractorGATE(url, docProcessor,nameCorpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
        }
    }

    /**
     * Method to read all the processed result of GATE on the document referenced from the url.
     * @param contentDocument the url address to the document you want to analize.
     * @param controller a corpus controller with loaded a gapp file.
     * @param nameCorpus (optional) name of the corpus gate.
     * @param nameAnnotations (optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet (optional) list of annotationSet you want to get from the document
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the document.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            String contentDocument, CorpusController controller,String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit) {
        GateCorpus8Kit gc8 = GateCorpus8Kit.getInstance();
        try{
            if(contentDocument!=null){
                if(StringUtilities.isNullOrEmpty(nameCorpus)) {
                    corpus = gc8.createCorpusByString(contentDocument, "GeoDocuments Corpus");
                }else{
                    corpus = gc8.createCorpusByString(contentDocument, nameCorpus);
                }
            }//if url!=null
            if(corpus == null){return null;}
            else{
                setCorpus(corpus, controller);
                logger.info(gm() + "Execute of GATE in process for a string content ...");
                execute(controller);
                logger.info(gm() + "...GATE is been processed");
                logger.info(gm() + "Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException|IOException e){
            logger.error(gm() + e.getMessage(), e);
        }
        finally{
            cleanup();
            corpus.clear();
        }
        return null;
    }//extractorGATE

    /**
     * Method to read all the processed result of GATE on the document referenced from the url.
     * @param contentDocument the string content of the url address to the document you want to analize.
     * @param docProcessor a document processor with loaded a gapp file using Spring framework.
     * @param nameCorpus (optional) name of the corpus gate.
     * @param nameAnnotations (optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet (optional) list of annotationSet you want to get from the document
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet hte same AnnotationType.
     * @return a map with all the string value you intend to extract from the document.
     */
    public Map<String,Map<String,Map<String,String>>> extractorGATE(
            String contentDocument, DocumentProcessor docProcessor, String nameCorpus,List<String> nameAnnotations,
            List<String> nameAnnotationsSet,boolean firstAndExit){
        GateCorpus8Kit gc8 = GateCorpus8Kit.getInstance();
        Document doc = new DocumentImpl();
        try{
            if(contentDocument!=null){
                doc = gc8.createDoc(contentDocument);
            }//if url!=null
            logger.info(gm() + "Execute of GATE in process for a string content ...");
            docProcessor.processDocument(doc);
            logger.info(gm() + "...GATE is been processed");
            if(StringUtilities.isNullOrEmpty(nameCorpus)) {
                corpus = gc8.createCorpusByDocument(doc, "GeoDocuments Corpus");
            }else{
                corpus = gc8.createCorpusByDocument(doc, nameCorpus);
            }
            if(corpus == null){return null;}
            else{
                logger.info(gm() +"Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException e){
            logger.error(gm() + e.getMessage(), e);
        }
        finally{
            cleanup();
        }
        return null;
    }//extractorGATE

    /**
     * Method to Convert the Object Gate Support to a GeoDocument object.
     * @param support the Gate Support Object.
     * @param nameAnnotation the string name of the annotation we want to extract.
     * @param index we use the index of the Document Gate on the corpus because we can't know the exact name of
     *              the document given from the user.
     * @return a GeoDocument Object.
     */
    public String extractContentAnnotationFromGateSupport(GateSupport support,String nameAnnotation,Integer index){
        String content;
        try {
            //for(String nameAnnotation: anntotations ){
                //get list of all annotation set...
                List<Map<String,Map<String,String>>> list = new ArrayList<>(support.getMapDocs().values());
                if(!list.isEmpty() && list.get(index).size()>0) {
                    try {
                        for (int j = 0; j < list.get(index).size(); j++) {
                            content = support.getContent(index, j, nameAnnotation);
                            if (!StringUtilities.isNullOrEmpty(content)) {
                                return content;
                            }
                        }
                    } catch (java.lang.IndexOutOfBoundsException e) {
                        logger.error(gm() + e.getMessage(), e);
                        return null;
                    }
                    logger.warn(gm() + "We not find any content for the Annotation we inspect, " +
                            "usually this can't be happened, return a empty string");
                    return "";
                }//if !isEmpty
                else{
                    logger.warn(gm() + "We try to extract content of annotation from a empty list of annotation return empty string");
                    return "";
                }
            //}//for each annotation
        } catch (Exception e) {
            logger.error(gm() + e.getMessage(), e);
            return null;
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

}
