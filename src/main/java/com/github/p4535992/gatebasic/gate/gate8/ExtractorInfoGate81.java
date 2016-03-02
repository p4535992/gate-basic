package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.gatebasic.object.MapAnnotation;
import com.github.p4535992.gatebasic.object.MapAnnotationSet;
import com.github.p4535992.gatebasic.object.MapDocument;
import javax.annotation.Nullable;
import gate.*;
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
import java.util.Collections;
import java.util.List;

/**
 * Created by 4535992 on 24/06/2015.
 * @author 4535992.
 * @version 2015-11-12.
 */
@SuppressWarnings("unused")
public class ExtractorInfoGate81 {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ExtractorInfoGate81.class);

    private Corpus corpus;
    private Controller controller;
    private MapDocument mapContentDocs;
    //private MapAnnotation mapAnnotation;
    //private MapAnnotationSet mapAnnotationSet;
    //private MapDocument mapDocs;


    private static ExtractorInfoGate81 instance = null;

    protected ExtractorInfoGate81(){
        mapContentDocs = new MapDocument();
        //if(mapContentDocs!=null)mapContentDocs.clear();
    }

    public static ExtractorInfoGate81 getInstance(){
        if(instance == null) {
            instance = new ExtractorInfoGate81();
        }

        return instance;
    }

    /** Tell GATE's spring.mvc.home.home.initializer.org.p4535992.mvc.webapp.controller about the corpus you want to run on.
     *  @param corpus corpus gate to set.
     *  @param controller corpus controller gate to set.
     */
    public void setCorpus(Corpus corpus, CorpusController controller) {
        this.corpus = corpus;
        this.controller = controller;
        controller.setCorpus(corpus);
    } // setCorpus

    public Corpus getCorpus() {
        return corpus;
    }

    public Controller getController() {
        return controller;
    }

    /**
     * Run GATE.
     * @param controller the {@link Controller} of the corpus controller gate to set.
     * @throws GateException gate exception.
     */
    public void execute(CorpusController controller) throws GateException {controller.execute();} // execute()

    /**
     * Run GATE.
     * @throws GateException gate exception.
     */
    public void execute() throws GateException {controller.execute();} // execute()

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
        }finally {
            exeTentative = 0;
        }
    }

    /**
     * Method to read all the processed result of GATE on the document referenced from the url.
     * @param url                the {@link URL} the url address to the documents you want to analize.
     * @param controller         the {@link CorpusController} with loaded a gapp file.
     * @param nameCorpus         the @Nullable {@link String} name of the corpus gate.
     * @param nameAnnotations    the {@link List} of {@link String} of annotation you want to get from the document.
     * @param nameAnnotationsSet the {@link List} of {@link String} of annotationSet you want to get from the document
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit       the {@link Boolean} is  true stop searching on other AnnotationSet hte same AnnotationType.
     * @return the {@link MapDocument} a map with all the string value you intend to extract from the document.
     */
    public MapDocument extractorGATE(
            URL url, CorpusController controller,@Nullable String nameCorpus,
            @Nullable List<String> nameAnnotationsSet,@Nullable List<String> nameAnnotations,boolean firstAndExit) {
        return extractorGATE(
                Collections.singletonList(url),controller,nameCorpus,nameAnnotationsSet,nameAnnotations,firstAndExit);
    }//extractorGATE

    /**
     * Method to read all the processed result of GATE on the document referenced from the url.
     * @param url the {@link URL} address to the documents you want to analyze.
     * @param docProcessor a {@link DocumentProcessor} with loaded a GAPP file using Spring framework.
     * @param nameCorpus the {@link String} (optional) name of the corpus gate.
     * @param nameAnnotations the {@link List} of the the {@link String}(optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet the {@link List} of the the {@link String}(optional) list of annotationSet you want to get from the document
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit the {@link Boolean} is true if stop searching on other AnnotationSet for the same AnnotationType.
     * @return the {@link MapDocument} a map with all the string value you intend to extract from the documents.t.
     */
    public MapDocument extractorGATE(
            URL url, DocumentProcessor docProcessor,@Nullable String nameCorpus,
            @Nullable List<String> nameAnnotationsSet,@Nullable List<String> nameAnnotations,boolean firstAndExit){
      return extractorGATE(
              Collections.singletonList(url),docProcessor,nameCorpus,nameAnnotationsSet,nameAnnotations,firstAndExit);
    }//extractorGATE

    /**
     * Method to read all the processed result of GATE on the document referenced from a list of urls.
     *
     * @param listUrl            the {@link List} of {@link URL} the url address to the documents you want to analize.
     * @param controller         the {@link CorpusController} with loaded a gapp file.
     * @param nameCorpus         the {@link String} name of the corpus gate.
     * @param nameAnnotations    the {@link List} of {@link String} of annotation you want to get from the document.
     * @param nameAnnotationsSet the {@link List} of {@link String} of annotationSet you want to get from the document
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit       the {@link Boolean} is  true stop searching on other AnnotationSet hte same AnnotationType.
     * @return the {@link MapDocument} a map with all the string value you intend to extract from the document.
     */
    public MapDocument extractorGATE(
            List<URL> listUrl, CorpusController controller,@Nullable String nameCorpus,@Nullable List<String> nameAnnotationsSet,
            @Nullable List<String> nameAnnotations,boolean firstAndExit) {
        return extractorGATEBase(new ArrayList<>(listUrl),controller,nameCorpus,null,nameAnnotationsSet,nameAnnotations,firstAndExit);
    }

    /**
     * Method to read all the processed result of GATE on the document referenced from a list of urls.
     * @param listUrl the [@link List] of {@link URL} address to the documents you want to analyze.
     * @param docProcessor a {@link DocumentProcessor} with loaded a GAPP file using Spring framework.
     * @param nameCorpus (optional) name of the corpus gate.
     * @param nameAnnotations (optional) list of annotation you want to get from the document.
     * @param nameAnnotationsSet (optional) list of annotationSet you want to get from the document
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit if true stop searching on other AnnotationSet for the same AnnotationType.
     * @return a map with all the string value you intend to extract from the documents.
     */
    public MapDocument extractorGATE(
            List<URL> listUrl, DocumentProcessor docProcessor, @Nullable String nameCorpus,
            @Nullable List<String> nameAnnotationsSet,@Nullable List<String> nameAnnotations,boolean firstAndExit) {
        return extractorGATEBase(
                new ArrayList<>(listUrl),docProcessor,nameCorpus,null,nameAnnotationsSet,nameAnnotations,firstAndExit);
    }

    /**
     * Method to read all the processed result of GATE on the document referenced from the url to a local file.
     * @param fileOrDirectory the {@link File} url address to a local file document you want to analyze.
     * @param controller the {@link CorpusController} with loaded a gapp file.
     * @param nameCorpus the {@link String} name of the corpus gate.
     * @param nameAnnotations {@link List} of {@link String} of annotation you want to get from the document.
     * @param nameAnnotationsSet {@link List} of {@link String} of annotationSet you want to get from the document
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit the {@link Boolean} is  true stop searching on other AnnotationSet hte same AnnotationType.
     * @return the {@link MapDocument} a map with all the string value you intend to extract from the document.
     */
    public MapDocument extractorGATE(
            File fileOrDirectory, CorpusController controller,@Nullable String nameCorpus,
            @Nullable List<String> nameAnnotationsSet,@Nullable  List<String> nameAnnotations,boolean firstAndExit) {
        List<URL> listUrl = prepareListURL(fileOrDirectory);
        return extractorGATEBase(new ArrayList<>(listUrl), controller, nameCorpus, null ,nameAnnotationsSet, nameAnnotations, firstAndExit);

    }

    /**
     * Method to read all the processed result of GATE on the document referenced from the url to a local file.
     * @param fileOrDirectory the {@link File} url address to a local file document you want to analize.
     * @param docProcessor the {@link DocumentProcessor}  with loaded a gapp file using Spring framework.
     * @param nameCorpus the {@link String} name of the corpus gate.
     * @param nameAnnotations {@link List} of {@link String} of annotation you want to get from the document.
     * @param nameAnnotationsSet {@link List} of {@link String} of annotationSet you want to get from the document
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit the {@link Boolean} is true stop searching on other AnnotationSet hte same AnnotationType.
     * @return the {@link MapDocument} a map with all the string value you intend to extract from the document.
     */
    public MapDocument extractorGATE(
            File fileOrDirectory, DocumentProcessor docProcessor,@Nullable String nameCorpus,
            @Nullable List<String> nameAnnotationsSet,@Nullable List<String> nameAnnotations,boolean firstAndExit){
        List<URL> listUrl = prepareListURL(fileOrDirectory);
        return extractorGATE(listUrl, docProcessor,nameCorpus,nameAnnotationsSet,nameAnnotations,firstAndExit);
    }

    /**
     * Method to read all the processed result of GATE on the document referenced from the url.
     * @param contentDocument the {@link String} content a local file document you want to analyze.
     * @param controller the {@link CorpusController} with loaded a gapp file.
     * @param nameCorpus the {@link String} name of the corpus gate.
     * @param nameAnnotations {@link List} of {@link String} of annotation you want to get from the document.
     * @param nameAnnotationsSet {@link List} of {@link String} of annotationSet you want to get from the document
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit the {@link Boolean} is  true stop searching on other AnnotationSet hte same AnnotationType.
     * @return the {@link MapDocument} a map with all the string value you intend to extract from the document.
     */
    public MapDocument extractorGATE(
            String contentDocument, CorpusController controller,@Nullable String nameCorpus,
            @Nullable List<String> nameAnnotationsSet,@Nullable List<String> nameAnnotations,boolean firstAndExit) {
        return extractorGATEBase(
                Collections.singletonList(contentDocument),controller,nameCorpus,null,nameAnnotationsSet,nameAnnotations,firstAndExit);
    }//extractorGATE

    /**
     * Method to read all the processed result of GATE on the document referenced from the url.
     * @param contentDocument the {@link String} content a local file document you want to analyze.
     * @param docProcessor the {@link DocumentProcessor}  with loaded a gapp file using Spring framework.
     * @param nameCorpus the {@link String} name of the corpus gate.
     * @param nameAnnotations {@link List} of {@link String} of annotation you want to get from the document.
     * @param nameAnnotationsSet {@link List} of {@link String} of annotationSet you want to get from the document
     *                           if param firstAndExit is true the list priority is given from the
     *                           index so the first element has more priority of the others element.
     * @param firstAndExit the {@link Boolean} is  true stop searching on other AnnotationSet hte same AnnotationType.
     * @return the {@link MapDocument} a map with all the string value you intend to extract from the document.
     */
    public MapDocument extractorGATE(
            String contentDocument, DocumentProcessor docProcessor,@Nullable String nameCorpus,
            @Nullable List<String> nameAnnotationsSet,@Nullable List<String> nameAnnotations,boolean firstAndExit){
        return extractorGATEBase(
                Collections.singletonList(contentDocument),docProcessor,nameCorpus,null,nameAnnotationsSet,nameAnnotations,firstAndExit);

    }//extractorGATE

    private MapDocument extractorGATEBase(List<Object> objectForCreateTheCorpus, Object processor,
                                          String nameCorpus, List<String> documents,
                                          List<String> nameAnnotationsSet, List<String> nameAnnotations , boolean firstAndExit) {
        GateCorpus8Kit gc8 = GateCorpus8Kit.getInstance();
        try {
            if (nameCorpus == null || nameCorpus.isEmpty()) {
                corpus = Factory.newCorpus("GeoDocuments Corpus");
            } else {
                corpus = Factory.newCorpus(nameCorpus);
            }
            logger.info("Execute of GATE in process for a list of  " + objectForCreateTheCorpus.size()
                    + " "+objectForCreateTheCorpus.get(0).getClass().getName()+"...");
            if (processor instanceof DocumentProcessor) {
                DocumentProcessor docProcessor = (DocumentProcessor) processor;
                for (Object obj : objectForCreateTheCorpus) {
                    if(obj != null) {
                        Document doc = gc8.createDoc(obj);
                        docProcessor.processDocument(doc);
                        corpus.add(doc);
                    }
                }
            } else if (processor instanceof CorpusController) {
                CorpusController corpusController = (CorpusController) processor;
                for (Object obj : objectForCreateTheCorpus) {
                    if (obj != null) {
                        Document doc = gc8.createDoc(obj);
                        corpus.add(doc);
                    }
                }
                setCorpus(corpus, corpusController);
                execute(corpusController);
            } else {
                logger.error("...the object :" + processor.getClass().getName() + " must be a CorpusController or a DocumentProcessor");
                return null;
            }
            logger.info("...GATE is been processed");
            //in the end...
            if (corpus == null) return null;
            else {
                logger.info("Work with the annotations...");
                return extractorGATE(corpus, nameAnnotationsSet,nameAnnotations, firstAndExit);
            }//else
        }//try
        catch (GateException | RuntimeException e) {
            logger.error(e.getMessage(), e);
        } finally {
            cleanup();
            corpus.clear();
        }
        return null;
    }

    /**
     * Method to Convert the Object Gate Support to a GeoDocument object.
     * @param support the {@link GateSupport2} Object.
     * @param nameAnnotation the {@link String} name of the annotation we want to extract.
     * @param index the {@link Integer} index of the Document Gate on the corpus because we can't know the exact name of
     *              the document given from the user.
     * @return the {@link String} of the content extract.
     */
    public String extractContentAnnotationFromGateSupport(GateSupport2 support,String nameAnnotation,Integer index){
        String content;
        try {
            //for(String nameAnnotation: anntotations ){
            //get list of all annotation set...
            List<MapAnnotationSet> list = new ArrayList<>(support.getDocument());
            if(!list.isEmpty() && list.get(index).size()>0) {
                try {
                    for (int j = 0; j < list.get(index).size(); j++) {
                        for(String sContent: support.getContent(index, j, nameAnnotation)) {
                            //content = support.getContent(index, j, nameAnnotation);
                            content = sContent;
                            if (content != null && !content.isEmpty()) {
                                return content;
                            }
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
     * Method to read all the processed result of GATE on a Corpus.
     * @param corpus the {@link Corpus} gate we want analyze.
     * @param nameAnnotations {@link List} of {@link String} of annotation you want to get from the document.
     * @param nameAnnotationsSet {@link List} of {@link String} of annotationSet you want to get from the document
     * @param firstAndExit the {@link Boolean} is true if stop searching on other AnnotationSet hte same AnnotationType.
     * @return the {@link MapDocument} a map with all the string value you intend to extract from the documents of the corpus.
     */
    public MapDocument extractorGATE(
            Corpus corpus,@Nullable List<String> nameAnnotationsSet,@Nullable List<String> nameAnnotations,boolean firstAndExit){
        //nameAnnotations.add("MyIndirizzo");
        //nameAnnotationsSet.add("MyAnnSet");
        //if(mapContentDocs!=null)mapContentDocs.clear();
        GateAnnotation81Kit ga81 = GateAnnotation81Kit.getInstance();
        //Set the global variable here...
        mapContentDocs = ga81.getAllAnnotationInfo(corpus,nameAnnotationsSet,nameAnnotations,firstAndExit);
        return mapContentDocs;
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
    private List<Path> getPathsFromDirectory(Path directory) {
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
