package com.github.p4535992.gatebasic.gate.gate8;

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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 24/06/2015.
 * @author 4535992.
 * @version 2015-11-12.
 * @deprecated use instead {@link ExtractorInfoGate81}
 */
@Deprecated
@SuppressWarnings("unused")
public class ExtractorInfoGate8 {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ExtractorInfoGate8.class);

    private Corpus corpus;
    private Map<String,Map<String,Map<String,String>>> mapContentDocs;
    private Map<String,String> mapAnnotation;
    private Map<String,Map<String,String>> mapAnnotationSet;
    private Map<String,Map<String,Map<String,String>>> mapDocs;


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
                logger.warn("No sentences or tokens to process in some gate documents");
            }
        }catch(OutOfMemoryError e){
            if(exeTentative < 3){
                exeTentative ++;
                execute(controller);
            }else{
                logger.warn("Exception in thread \"AWT-EventQueue-0\" ava.lang.OutOfMemoryError: Java heap space");
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
                if(nameCorpus == null || nameCorpus.isEmpty()) {
                    corpus = gc8.createCorpusByURL(url, "GeoDocuments Corpus");
                }else{
                    corpus = gc8.createCorpusByURL(url, nameCorpus);
                }
            }//if url!=null
            if(corpus == null){return null;}
            else{
                setCorpus(corpus, controller);
                logger.info("Execute of GATE in process for the url " + url + "...");
                execute(controller);
                logger.info("...GATE is been processed");
                logger.info("Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException e){
            logger.error(e.getMessage(), e);
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
            logger.info("Execute of GATE in process for the url " + url + "...");
            docProcessor.processDocument(doc);
            logger.info("...GATE is been processed");
            if(nameCorpus == null || nameCorpus.isEmpty()) {
                corpus = gc8.createCorpusByDocument(doc, "GeoDocuments Corpus");
            }else{
                corpus = gc8.createCorpusByDocument(doc, nameCorpus);
            }
            if(corpus == null){
                logger.warn("Can't work with the annotation because the Coprus object is NULL");
                return null;
            }
            else{
                logger.info("Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException e){
            logger.error(e.getMessage(), e);
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
            logger.info("Execute of GATE in process for a list of  " + listUrl.size() + " urls...");
            if (nameCorpus == null || nameCorpus.isEmpty()) {
                corpus = gc8.createCorpusByURL(listUrl, "GeoDocuments Corpus");
            } else {
                corpus = gc8.createCorpusByURL(listUrl, nameCorpus);
            }
            if(corpus == null){return null;}
            else{
                setCorpus(corpus, controller);
                execute(controller);
                logger.info("...GATE is been processed");
                logger.info("Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException e){
            logger.error(e.getMessage(), e);
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
            if (nameCorpus == null || nameCorpus.isEmpty()) {
                corpus = Factory.newCorpus("GeoDocuments Corpus");
            } else {
                corpus = Factory.newCorpus(nameCorpus);
            }
            for(URL url: listUrl) {
                try {
                    if (url != null) {
                        doc = gc8.createDoc(url);
                    }//if url!=null
                    logger.info("Execute of GATE in process for the url " + url + "...");
                    docProcessor.processDocument(doc);
                    logger.info("...GATE is been processed");
                    corpus.add(doc);
                } catch(GateException e){
                    logger.warn(e.getMessage(),e);
                }
            }
            if(corpus == null){return null;}
            else{
                logger.info("Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException e){
            logger.error(e.getMessage(), e);
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
        List<URL> listUrl = prepareListURL(fileOrDirectory);

        if(listUrl.size() > 1) {
            return extractorGATE(listUrl, controller, nameCorpus, nameAnnotations, nameAnnotationsSet, firstAndExit);
        }else{
            return extractorGATE(listUrl.get(0), controller, nameCorpus, nameAnnotations, nameAnnotationsSet, firstAndExit);
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
        List<URL> listUrl = prepareListURL(fileOrDirectory);

        if(listUrl.size() > 1) {
            return extractorGATE(listUrl, docProcessor,nameCorpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
        }else{
            return extractorGATE(listUrl.get(0), docProcessor,nameCorpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
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
                if(nameCorpus == null || nameCorpus.isEmpty()) {
                    corpus = gc8.createCorpusByString(contentDocument, "GeoDocuments Corpus");
                }else{
                    corpus = gc8.createCorpusByString(contentDocument, nameCorpus);
                }
            }//if url!=null
            if(corpus == null){return null;}
            else{
                setCorpus(corpus, controller);
                logger.info("Execute of GATE in process for a string content ...");
                execute(controller);
                logger.info("...GATE is been processed");
                logger.info("Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException e){
            logger.error(e.getMessage(), e);
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
            logger.info("Execute of GATE in process for a string content ...");
            docProcessor.processDocument(doc);
            logger.info("...GATE is been processed");
            if(nameCorpus == null || nameCorpus.isEmpty()) {
                corpus = gc8.createCorpusByDocument(doc, "GeoDocuments Corpus");
            }else{
                corpus = gc8.createCorpusByDocument(doc, nameCorpus);
            }
            if(corpus == null){return null;}
            else{
                logger.info("Work with the annotations...");
                return extractorGATE(corpus,nameAnnotations,nameAnnotationsSet,firstAndExit);
            }//else
        }//try
        catch(GateException|RuntimeException e){
            logger.error(e.getMessage(), e);
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
                            if (content != null && !content.isEmpty()) {
                                return content;
                            }
                        }
                    } catch (java.lang.IndexOutOfBoundsException e) {
                        logger.error(e.getMessage(), e);
                        return null;
                    }
                    logger.warn("We not find any content for the Annotation we inspect, " +
                            "usually this can't be happened, return a empty string");
                    return "";
                }//if !isEmpty
                else{
                    logger.warn("We try to extract content of annotation from a empty list of annotation return empty string");
                    return "";
                }
            //}//for each annotation
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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

    /**
     * Method to preapre a List of URL froma File or Directoryl
     * @param fileOrDirectory the {@link File} to inspect.
     * @return the list of {@link URL}.
     */
    private List<URL> prepareListURL(File fileOrDirectory){
        /*List<File> listFiles = new ArrayList<>();
        if(FileUtilities.isDirectoryExists(fileOrDirectory.getAbsolutePath())) {
            listFiles = FileUtilities.getFilesFromDirectory(fileOrDirectory);
        }else{
            listFiles.add(fileOrDirectory);
        }
        List<URL> listUrl = new ArrayList<>();
        for(File file: listFiles) {
            URL url = FileUtilities.toURL(file);
            if(url!=null)listUrl.add(url);
        }
        return listUrl;*/
        return prepareListURL(fileOrDirectory.toPath());
    }

    /**
     * Method to preapre a List of URL froma File or Directoryl
     * @param fileOrDirectory the {@link File} to inspect.
     * @return the list of {@link URL}.
     */
    private List<URL> prepareListURL(Path fileOrDirectory){
        List<Path> listFiles = new ArrayList<>();
        if(Files.exists(fileOrDirectory) && Files.isDirectory(fileOrDirectory)) {
            listFiles = getPathsFromDirectory(fileOrDirectory);
        }else{
            listFiles.add(fileOrDirectory);
        }
        List<URL> listUrl = new ArrayList<>();
        for(Path file: listFiles) {
            try {
                listUrl.add(file.toUri().toURL());
            } catch (MalformedURLException e) {
                logger.warn("Can't create a url for the file:"+file.toAbsolutePath().toString());
            }
        }
        return listUrl;
    }

    /**
     * Method to read all file ina directory/folder.
     *
     * @param directory the {@link File} directory/folder.
     * @return the  {@link List} of {@link File} in the directory.
     */
    public List<Path> getPathsFromDirectory(Path directory) {
        List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory.toUri()))) {
            for (Path path : directoryStream) {
                paths.add(path);
            }
        } catch (IOException e) {
            logger.error("Listing files in directory: {}", directory, e);
        }
        return paths;
    }

}
