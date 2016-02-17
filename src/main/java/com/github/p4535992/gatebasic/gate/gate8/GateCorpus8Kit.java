package com.github.p4535992.gatebasic.gate.gate8;


import com.sun.istack.internal.Nullable;
import gate.*;
import gate.corpora.DocumentImpl;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class utility for work with Document and Corpus of the GATE API.
 * @author 4535992 Elaborato Sistemi Distribuiti
 * @version 2015-11-12
 */
@SuppressWarnings("unused")
public class GateCorpus8Kit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GateCorpus8Kit.class);

    private Document doc;
    public Corpus getCorpus() {
        return corpus;
    }
    public void setCorpus(Corpus corpus) {
        this.corpus = corpus;
    }

    public Corpus corpus;
    public DataStore datastore;


    private static GateCorpus8Kit instance = null;
    protected GateCorpus8Kit(){}

    public static GateCorpus8Kit getInstance(){
        if(instance == null) {
            instance = new GateCorpus8Kit();
        }
        return instance;
    }

    /**
     * Crea un Corpus di Documenti Gate.
     * @param  url url to the web document.
     * @param  nomeCorpus corpus gate to set.
     * @return corpus gate fulled.
     * @throws  IOException error.
     * @throws  ResourceInstantiationException error.
     */
    public Corpus createCorpusByURL(URL url,String nomeCorpus) throws IOException, ResourceInstantiationException{
        corpus = Factory.newCorpus(nomeCorpus);
        doc = createDoc(url);
        if(doc != null) {
            corpus.add(doc);//add a document to the corpus
        }
        logger.info("Loaded a corpus of: " + corpus.size() + " files");
        return corpus;
    } // createCorpus
    
   /**
    * Crea un Corpus di Documenti Gate
    * @param  listUrl list of url to set of web document.
    * @param  nomeCorpus name of the corpus gate.
    * @return corpus gate fulled.
    * @throws  IOException error.
    * @throws  ResourceInstantiationException error.
    */  
    public Corpus createCorpusByURL(List<URL> listUrl,String nomeCorpus) throws IOException, ResourceInstantiationException{
        corpus = Factory.newCorpus(nomeCorpus);
        Integer indice = 0;
        for (URL url : listUrl) {
            doc = createDoc(url, indice);
            if (doc != null) {
                corpus.add(doc);//add a document to the corpus
                indice++;
            }
        } // for each corpus
        logger.info("The Corpus has:" + indice + " document create from urls address.");
        return corpus;
    } // createCorpus


    /**
     * Method for create a Corpus Gate from a single file or directory/folder.
     * @param fileOrDirectory file or directory to put in the corpus.
     * @param nameCorpus name of hte Corpus gate.
     * @return corpus gate.
     * @throws ResourceInstantiationException throw if any error is occurred.
     * @throws IOException throw if any error is occurred.
     */
    public Corpus createCorpusByFile(File fileOrDirectory,String nameCorpus) throws ResourceInstantiationException, IOException {
        if(fileOrDirectory.isDirectory() && fileOrDirectory.isDirectory()){
            List<File> listFiles = getFilesFromDirectory(fileOrDirectory);
            return createCorpusByFile(listFiles,nameCorpus);
        }else {
            corpus = Factory.newCorpus(nameCorpus);
            doc = createDoc(fileOrDirectory.toURI().toURL());
            if (doc != null) {
                corpus.add(doc);//add a document to the corpus
            }
            return corpus;
        }
    }

    /**
     * Method for create a Corpus Gate from a list of files
     * @param listFiles list of files you want put in a gate corpus.
     * @param nameCorpus string name of Corpus Gate.
     * @return corpus of gate fill with the document.
     * @throws ResourceInstantiationException throw if any error is occurred.
     * @throws IOException throw if any error is occurred.
     */
    public Corpus createCorpusByFile(List<File> listFiles,String nameCorpus)throws ResourceInstantiationException, IOException {
        corpus = Factory.newCorpus(nameCorpus);
        Integer indice = 0;
        for(File file : listFiles) {
            doc = createDoc(file.toURI().toURL(),indice);
            //Document doc = Factory.newDocument(docFile.toURL(), "utf-8");
            if (doc != null) {
                corpus.add(doc);//add a document to the corpus
                indice++;
            }
        }
        return corpus;
    }

    /**
     * Method for create a Corpus Gate from a Document Gate.
     * @param doc document Gate to add to the Corpus.
     * @param nomeCorpus string name of Corpus Gate.
     * @return corpus of gate fill with the document.
     * @throws ResourceInstantiationException throw if any error is occurred.
     */
    public Corpus createCorpusByDocument(Document doc,String nomeCorpus)
            throws ResourceInstantiationException{
        corpus = Factory.newCorpus(nomeCorpus);
        corpus.add(doc);//add a document to the corpus
        logger.info("Loaded a corpus of: " + corpus.size() + " files");
        return corpus;
    } // createCorpus

    /**
     * Method for create a Corpus Gate from a list of Document Gate.
     * @param listDoc list of Document Gate toadd to the Corpus.
     * @param nomeCorpus string name of the Coprus Gate.
     * @return corpus of gate fill with the documents.
     * @throws ResourceInstantiationException throw if any error is occurred.
     */
    public Corpus createCorpusByDocument(List<Document> listDoc,String nomeCorpus)
            throws ResourceInstantiationException{
        corpus = Factory.newCorpus(nomeCorpus);
        for(Document document: listDoc) {
            corpus.add(document);//add a document to the corpus
        }
        logger.info("Loaded a corpus of: " + corpus.size() + " files");
        return corpus;
    } // createCorpus

    /**
     * Crea un Corpus di Documenti Gate.
     * @param  content string of the web document.
     * @param  nomeCorpus corpus gate to set.
     * @return corpus gate fulled.
     * @throws  IOException error.
     * @throws  ResourceInstantiationException error.
     */
    public Corpus createCorpusByString(String content,String nomeCorpus) throws IOException, ResourceInstantiationException{
        corpus = Factory.newCorpus(nomeCorpus);
        doc = createDoc(content);
        if(doc != null) {
            corpus.add(doc);//add a document to the corpus
        }
        logger.info("Loaded a corpus of: " + corpus.size() + " files");
        return corpus;
    } // createCorpus



    /**
     * Metodo che salva un Corpus nel datastore.
     * @param corpus corpus to save.
     * @param datastore datastore gate.
     * @throws IOException error.
     * @throws PersistenceException  error.
     */
    public void saveCorpusOnDataStoreForOutOfMemory(Corpus corpus,GateDataStore8Kit datastore)
            throws IOException, PersistenceException{
        datastore.openDataStore();
        datastore.initDatastoreWithACorpus(corpus);
        datastore.saveACorpusOnTheDataStore(corpus);
        datastore.closeDataStore();
    }

    /**
     * Method for load a Corpus Gate on a Gate DataStore.
     * @param corpus corpus gate to save.
     * @param datastore datastore gate where save the corpus.
     * @throws gate.persist.PersistenceException throw if any error is occurred.
     * @throws gate.creole.ResourceInstantiationException throw if any error is occurred.
     */
    public void loadCorpusOnADataStore(Corpus corpus,GateDataStore8Kit datastore)
            throws PersistenceException, ResourceInstantiationException {
        try {
            datastore.openDataStore();
            datastore.initDatastoreWithACorpus(corpus);
            datastore.saveACorpusOnTheDataStore(corpus);
            //datastore.closeDataStore();
        } catch (PersistenceException e) {
            datastore.openDataStore();
            datastore.saveACorpusOnTheDataStore(corpus);
            datastore.closeDataStore();
        } finally {
            //code for merge all the corpus in the datstore in a unique corpus.
            datastore.openDataStore();
            List<Corpus> listCorpus = datastore.loadAllCorpusOnTheDataStore();
            if (listCorpus.size() > 1) {
                String nome_corpus_2 = "GeoDocuments_Corpus_" + "[]";
                corpus = datastore.mergeDocumentsOfMultipleCorpusWithChooseOfCorpusOnTheDataStore(nome_corpus_2, listCorpus);
                datastore.saveACorpusOnTheDataStore(corpus);
            }
            datastore.closeDataStore();
        }
    }//loadCorpusOnADataStore

    /**
     * Method for create gate document from a url object.
     * @param url url to a web page or file.
     * @param i integer for index the document
     * @return document gate.
     */
    public Document createDoc(URL url,Integer i) {
        return createDoc(url,null,null,null,"doc_" + i + "_" + url);
    }

    /*
     * Method for create gate document from a url object.
     * @param url url to a web page or file.
     * @return document gate.
     */
    /*private Document createDocWithFeature(
            URL url,@Nullable Boolean preserveOriginalContent,@Nullable Boolean collectRepositioningInfo,
            @Nullable Charset encoding, @Nullable String resourceName) {
        try {
            if(preserveOriginalContent ==null) preserveOriginalContent =true;
            if(collectRepositioningInfo == null) collectRepositioningInfo = true;
            if(encoding == null) encoding =StandardCharsets.UTF_8;
            if(resourceName == null) resourceName = "doc_" + url.toString();
            Date date = new Date();
            //document features insert by me
            FeatureMap feats = Factory.newFeatureMap();
            feats.put("date", date);
            //document features insert with GATE
            FeatureMap params = Factory.newFeatureMap();
            params.put("sourceUrl", url);
            params.put("preserveOriginalContent", preserveOriginalContent);
            params.put("collectRepositioningInfo", collectRepositioningInfo);
            params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, encoding.displayName());
            params.put(Document.DOCUMENT_URL_PARAMETER_NAME, url);
            try {
                doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, feats, resourceName);
            }catch(gate.creole.ResourceInstantiationException e){
                try {
                    doc = Factory.newDocument(url, "UTF-8");
                    doc.setSourceUrl(url);
                    doc.setPreserveOriginalContent(true);
                    doc.setCollectRepositioningInfo(true);
                    doc.setFeatures(feats);
                }catch(gate.creole.ResourceInstantiationException e1){
                    throw new NullPointerException(e1.getMessage());
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.warn("Document " + url + " not exists anymore or is unreachable:"+e.getMessage(),e);
            doc = null;
        }
        catch (NullPointerException e){
            logger.error(e.getMessage(),e);
            doc = null;
        }
        return doc;
    }*/

    public FeatureMap createFeatureMap(){
        return Factory.newFeatureMap();
    }

    /**
     * Method for create gate document from a url object.
     * @param message url to a web page or file.
     * @return document gate.
     */
    public Document createDoc(String message){
        return createDoc(message,null,null,null,null);
    }

    /**
     * Method for create gate document from a url object.
     * @param message string to a web page or file.
     * @return document gate.
     */
    public Document createDoc(String message,
                              @Nullable Boolean preserveOriginalContent,@Nullable Boolean collectRepositioningInfo,
                              @Nullable Charset encoding, @Nullable String resourceName){
        try {
            Date date = new Date();
            if(preserveOriginalContent ==null) preserveOriginalContent =true;
            if(collectRepositioningInfo == null) collectRepositioningInfo = true;
            if(resourceName == null) resourceName = "doc_" + date.toString();
            FeatureMap feats = Factory.newFeatureMap();
            feats.put("date", date);
            feats.put(Document.DOCUMENT_STRING_CONTENT_PARAMETER_NAME, message);
            doc = new DocumentImpl();
            //doc = Factory.newDocument(message);
            doc.setPreserveOriginalContent(preserveOriginalContent);
            doc.setCollectRepositioningInfo(collectRepositioningInfo);
            doc.setName(resourceName);
            doc.setFeatures(feats);
            return (Document) doc.init();
        }catch(ResourceInstantiationException e){
            logger.error("Can't create the Gate Document",e);
            return null;
        }
    }

    /**
     * Method for create gate document from a url object.
     * @param url url to a web page or file.
     * @return document gate.
     */
    public Document createDoc(URL url){
        return createDoc(url,null,null,null,null);
    }

    /**
     * Method to create a gate document from a string.
     * @param url the {@link URL} message.
     * @return the {@link Document} gate.
     */
    public Document createDoc(URL url,
                              @Nullable Boolean preserveOriginalContent,@Nullable Boolean collectRepositioningInfo,
                              @Nullable Charset encoding, @Nullable String resourceName){
        try {
            if(preserveOriginalContent ==null) preserveOriginalContent =true;
            if(collectRepositioningInfo == null) collectRepositioningInfo = true;
            if(encoding == null) encoding =StandardCharsets.UTF_8;
            if(resourceName == null) resourceName = "doc_" + url.toString();
            Date date = new Date();
            FeatureMap feats = Factory.newFeatureMap();
            feats.put("date", date);
            feats.put(Document.DOCUMENT_URL_PARAMETER_NAME, url);
            feats.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, encoding);
            doc = new DocumentImpl();
            //doc = Factory.newDocument(url,encoding.displayName());
            doc.setSourceUrl(url);
            doc.setPreserveOriginalContent(preserveOriginalContent);
            doc.setCollectRepositioningInfo(collectRepositioningInfo);
            doc.setName(resourceName);
            doc.setFeatures(feats);
            return (Document) doc.init();
        }catch(ResourceInstantiationException e){
            logger.error("Can't create the Gate Document",e);
            return null;
        }
        //return createDocWithFeature(url,preserveOriginalContent,collectRepositioningInfo,encoding,resourceName);
    }

    /**
     * Method to create a gate document from a string.
     * @param message the {@link File} to convert to a document.
     * @return the {@link Document} gate.
     */
    public Document createDoc(File message){
        try {
            return createDoc(message.toURI().toURL());
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to create a gate document from a string.
     * @param path the {@link Path} to convert to a document.
     * @return the {@link Document} gate.
     */
    public Document createDoc(Path path){
        try {
            return createDoc(path.toUri().toURL());
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to create a gate document from a string.
     * @param uri the {@link URI} to convert to a document.
     * @return the {@link Document} gate.
     */
    public Document createDoc(URI uri){
        try {
            return createDoc(uri.toURL());
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * Method to create a gate document from a string.
     * @param obj the {@link Object} to convert to a document.
     * @return the {@link Document} gate.
     */
    public Document createDoc(Object obj){
        if(obj != null) {
            if (obj instanceof String) {
                String contentDocument = String.valueOf(obj);
                return createDoc(contentDocument);
            } else if (obj instanceof URL) {
                URL url = (URL) obj;
                return createDoc(url);
            } else if(obj instanceof URI){
                URI uri = (URI) obj;
                return createDoc(uri);
            }else if(obj instanceof File){
                File file = (File) obj;
                return createDoc(file);
            }else if(obj instanceof Path){
                Path path = (Path) obj;
                return createDoc(path);
            }else{
                logger.error("Can't create a valid document gate for a "+obj.getClass().getName()+" object.");
                return null;
            }
        }else{
            logger.error("Can't create a valid document gate for a NULL object.");
            return null;
        }
    }

    /**
     * Method to convert a gate Document to a XML file.
     * @param fileXml string path the xml file.
     * @param doc gate document.
     * @throws IOException throws if any error is occurred.
     */
    public void writeXMLFromDocument(File fileXml,Document doc) throws IOException {
        String docXMLString;
        docXMLString = doc.toXml();
        String outputFileName = doc.getName() + ".out.xml";
        File outputFile = new File(fileXml.getParentFile(), outputFileName);
        FileOutputStream fos = new FileOutputStream(outputFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        OutputStreamWriter out;
        out = new OutputStreamWriter(bos, "utf-8");
        out.write(docXMLString);
        out.close();
    }

    /**
     * Method to read all file ina directory/folder.
     *
     * @param directory the {@link File} directory/folder.
     * @return the  {@link List} of {@link File} in the directory.
     */
    private List<File> getFilesFromDirectory(File directory) {
        List<File> paths = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory.toURI()))) {
            for (Path path : directoryStream) {
                paths.add(path.toFile());
            }
        } catch (IOException e) {
            logger.error("Listing files in directory: {}", directory, e);
        }
        return paths;
    }

    private static List<String> read(Path fileInput, Charset encodingInput) {
        if(fileInput == null){
            logger.warn("The fileInput we try to read is a NULL object.");
            return new ArrayList<>();
        }
        if (encodingInput == null) encodingInput = StandardCharsets.UTF_8;
        List<String> collection = new ArrayList<>();
        try {
            try (Stream<String> stream = Files.lines(fileInput,encodingInput)) {
                collection.addAll(stream.collect(Collectors.toList()));
            }
            return collection;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private static String readAll(Path fileInput, Charset encodingInput) {
        List<String> list = read(fileInput,encodingInput);
        StringBuilder sb = new StringBuilder();
        for(String s: list){
            sb.append(s).append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    private static List<String> read(File fileInput) {
        return read(fileInput.toPath(),StandardCharsets.UTF_8);
    }

    private static List<String> read(Path fileInput) {
        return read(fileInput,StandardCharsets.UTF_8);
    }

    private static String readAll(File fileInput) {
        return readAll(fileInput.toPath(),StandardCharsets.UTF_8);
    }

    private static String readAll(Path fileInput) {
        return readAll(fileInput,StandardCharsets.UTF_8);
    }


  
}//class pipeline
