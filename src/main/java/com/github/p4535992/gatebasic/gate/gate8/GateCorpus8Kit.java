package com.github.p4535992.gatebasic.gate.gate8;
import com.github.p4535992.util.file.impl.FileUtilities;
import com.github.p4535992.util.log.SystemLog;
import gate.*;
import gate.corpora.DocumentImpl;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.List;
/**
 * Class utility for work with Document and Corpus of the GATE API.
 * @author 4535992 Elaborato Sistemi Distribuiti
 * @version 2015-06-25
 */
@SuppressWarnings("unused")
public class GateCorpus8Kit {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger( GateCorpus8Kit.class);
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
    public Corpus createCorpusByUrl(URL url,String nomeCorpus) throws IOException, ResourceInstantiationException{
        corpus = Factory.newCorpus(nomeCorpus);
        doc = createDocByUrl(url);
        if(doc != null) {
            corpus.add(doc);//add a document to the corpus
        }
        SystemLog.message("Loaded a corpus of: "+corpus.size()+" files");
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
    public Corpus createCorpusByUrl(List<URL> listUrl,String nomeCorpus) throws IOException, ResourceInstantiationException{
        corpus = Factory.newCorpus(nomeCorpus);
        Integer indice = 0;
        for (URL url : listUrl) {
            doc = createDocByUrl(url, indice);
            if (doc != null) {
                corpus.add(doc);//add a document to the corpus
                indice++;
            }
        } // for each corpus
        SystemLog.message("Contenuto del Corpus costituito da:" + indice + " indirizzi url.");
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
        if(FileUtilities.isDirectory(fileOrDirectory)){
            List<File> listFiles = FileUtilities.readDirectory(fileOrDirectory);
            return createCorpusByFile(listFiles,nameCorpus);
        }else {
            corpus = Factory.newCorpus(nameCorpus);
            doc = createDocByUrl(FileUtilities.convertFileToUri(fileOrDirectory.getAbsolutePath()).toURL());
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
            doc = createDocByUrl(FileUtilities.convertFileToUri(file.getAbsolutePath()).toURL(),indice);
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
        SystemLog.message("Loaded a corpus of: "+corpus.size()+" files");
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
        SystemLog.message("Loaded a corpus of: "+corpus.size()+" files");
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
        SystemLog.message("Loaded a corpus of: "+corpus.size()+" files");
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
    public Document createDocByUrl(URL url,Integer i) {
        doc = new DocumentImpl();
        try {
            //document features insert with GATE
            FeatureMap params = Factory.newFeatureMap();
            params.put("sourceUrl", url);
            params.put("preserveOriginalContent", true);
            params.put("collectRepositioningInfo", true);
            params.put(Document.DOCUMENT_ENCODING_PARAMETER_NAME, "UTF-8");
            //document features insert by me
            FeatureMap feats = Factory.newFeatureMap();
            feats.put("date", new Date());
            try {
                doc = (Document) Factory.createResource("gate.corpora.DocumentImpl", params, feats, "doc_" + i + "_" + url);
            }catch(gate.creole.ResourceInstantiationException e){
                try {
                    doc = Factory.newDocument(url, "UTF-8");
                }catch(gate.creole.ResourceInstantiationException e1){
                    throw new NullPointerException(e1.getMessage());
                }
            }
            //doc = Factory.newDocument(url, "utf-8");          
        } catch (ArrayIndexOutOfBoundsException ax) {
            SystemLog.warning("Document " + url + " not exists anymore or is unreachable.");
        }
        catch (NullPointerException ne){
            SystemLog.exception(ne);
            doc = null;
        }
        return doc;
    }

    /**
     * Method for create gate document from a url object.
     * @param url url to a web page or file.
     * @return document gate.
     */
    public Document createDocByUrl(URL url) {
        return createDocByUrl(url, 0);
    }

    /**
     * Method to create a gate document from a string.
     * @param message string message.
     * @return gate document.
     */
    public Document createDoc(String message){
        doc = new DocumentImpl();
        try {
            doc = Factory.newDocument(message);
        } catch (ResourceInstantiationException e) {
            SystemLog.exception(e);
        }
        return doc;
    }

    /**
     * Method to create a gate document from a URL.
     * @param url  URL address.
     * @return gate document.
     */
    public Document createDoc(URL url){
        doc = new DocumentImpl();
        try {
            doc = Factory.newDocument(url);
        } catch (ResourceInstantiationException e) {
            SystemLog.exception(e);
        }
        return doc;
    }

    /**
     * Method to create a gate document from a URL.
     * @param url URL address.
     * @param encoding string encoding format.
     * @return gate document.
     */
    public Document createDoc(URL url,String encoding){
        doc = new DocumentImpl();
        try {
            doc = Factory.newDocument(url,encoding);
        } catch (ResourceInstantiationException e) {
           SystemLog.exception(e);
        }
        return doc;
    }


    /**
     * Method to convert a gate Document to a XML file.
     * @param fileXml string path the xml file.
     * @param doc gate document.
     * @throws IOException throws if any error is occurred.
     */
    public void wrtieXMLFromDocuemnt(File fileXml,Document doc) throws IOException {
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
  
}//class pipeline
