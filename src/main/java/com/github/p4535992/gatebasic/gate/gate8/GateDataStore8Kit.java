
package com.github.p4535992.gatebasic.gate.gate8;

import gate.*;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class utility for work with datastore gate.
 * Class is Save the Corpus GATE Serial Lucene in a Datastore and all the methods required to perform
 * CRUD operations for both the body and for individual documents directly
 * From DataStore.
 * I WAS NOT BUILT IN THIS PROJECT BUT IT TAKES LITTLE invoke
 * @author 4535992
 * @version 2015-11-12.
 */
@SuppressWarnings("unused")
public class GateDataStore8Kit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GateDataStore8Kit.class);

    //the directory must EXIST and be EMPTY
    private String DS_DIR = null;//"/var/tmp/gate001"
    private String NAME_DATASTORE = null;
    private SerialDataStore sds = null;
    //private DataStore ds = null;

     private static GateDataStore8Kit instance = null;
  
     protected GateDataStore8Kit(){}

     public static GateDataStore8Kit getInstance(String DS_DIR, String NAME_DATASTORE){
         if(instance == null) {
            instance = new GateDataStore8Kit(DS_DIR,NAME_DATASTORE);
         }
        return instance;
     }

      protected GateDataStore8Kit(String DS_DIR, String NAME_DATASTORE) {

          setDataStore(DS_DIR,NAME_DATASTORE);

      }

      private void setDataStore(String DS_DIR,String NAME_DATASTORE){
          if(!DS_DIR.endsWith(File.separator))DS_DIR = DS_DIR + File.separator;
          if(NAME_DATASTORE.startsWith(File.separator)) NAME_DATASTORE = NAME_DATASTORE.substring(1,NAME_DATASTORE.length()-1);
          Path rep = Paths.get(DS_DIR + NAME_DATASTORE).toAbsolutePath();
          DS_DIR = rep.toString();
          //DS_DIR = "file:///"+rep.toString();
          logger.info("Datastore directory is located on :" + DS_DIR);
          this.DS_DIR = DS_DIR;
          this.NAME_DATASTORE = NAME_DATASTORE;
      }

      /**
       * Method to import a corpus to a datastore.
       * @param corp corpus gate to save on the datastore gate.
       */
      public void initDatastoreWithACorpus(Corpus corp) {
          //Inizializzazione di GATE avvenuta
          //gate.init() --- Gate.init()
          //Creazione e settaggio del Corpus avvenuta
          //gate.setCorpus(corpus)
          logger.info( "Datastore directory:" + DS_DIR + "\nNome DataStore:" + NAME_DATASTORE);
          try {
                //insert&open a new Serial Data Store
                //pass the datastore class and path as parameteres
                sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
                sds.setName(NAME_DATASTORE);
                logger.info( "Serial datastore created...");
                //insert test corpus
                // SecurityInfo is ingored for SerialDataStore - just pass null
                // a new persisent corpus is returned
                Corpus  persistCorp = (Corpus)sds.adopt(corp);
                sds.sync(persistCorp);
                logger.info("Corpus saved in datastore...");
                Object corpusID  = persistCorp.getLRPersistenceId();
                logger.info( corpusID.toString());
          }
          catch(PersistenceException e) {
                logger.error( "The datastore already exists:", e.getMessage(),e);
          }
      }

      /**
       * Method to change name to the corpus and sync it with the datastore.
       * @param persistCorp corpus gate to change the name.
       * @param nameCorpus new name corpus.
       * @throws PersistenceException error.
       * @throws SecurityException error.
       */
       public void changeNameCorpus(Corpus persistCorp,String nameCorpus) throws PersistenceException{
           persistCorp.setName(nameCorpus);
           persistCorp.sync();
           logger.info( "Change name of the Corpus:" + persistCorp.getName() + " on the datastoe with name " + nameCorpus);
       }
       /**
        * Method to Load corpus from datastore using its persistent ID.
        * @param corpusID string id of the corpus.
        * @return corpus gate.
        * @throws ResourceInstantiationException error.
        * @throws PersistenceException error.
        */
       public Corpus loadCorpusFromDataStoreById(Object corpusID) throws ResourceInstantiationException, PersistenceException {
           openDataStore(DS_DIR);
           FeatureMap corpFeatures = Factory.newFeatureMap();
           corpFeatures.put(DataStore.LR_ID_FEATURE_NAME, corpusID);
           corpFeatures.put(DataStore.DATASTORE_FEATURE_NAME, sds);
           //tell the context to load the Serial Corpus with the specified ID from the specified  datastore
           Corpus persistCorp = (Corpus)Factory.createResource("gate.corpora.SerialCorpusImpl", corpFeatures);
           logger.info( "Corpus " + persistCorp.getName() + " loaded on the datastore...");
           return persistCorp;

       }
       /**
        * Metho to delete corpus from datastore.
        * @param persistCorp corpus gate to delete.
        * @param corpusID id of the corpus.
        * @throws PersistenceException error.
        */
       public void deleteCorpusFromDataStoreById(Corpus persistCorp,Object corpusID) throws PersistenceException {
           sds.delete("gate.corpora.SerialCorpusImpl", corpusID);
           logger.info( "Corpus " + persistCorp.getName() + " deleted from the datastore " + sds.getName() + "!!!");
       }

       /**Close the DataStore.
        * @throws PersistenceException error.
        */
       public void closeDataStore() throws PersistenceException{
           sds.close();
           //sds = null;
           logger.info( "Datastore " + sds.getName() + " closed!!!");
       }

       /**
        * Delete the DataStore.
        * @throws PersistenceException error.
        */
       public void deleteDataStore() throws PersistenceException{
           sds.delete();
           logger.info( "Datastore " + sds.getName() + " deleted!!!");
       }

       /** open-reopen DataStore.
        * @param absolutePathDirectory string file to the folder datastore on local.
        */
       public void openDataStore(String absolutePathDirectory) {
           try{
              if((absolutePathDirectory!=null) || (sds==null)){
                  try {
                      sds = new SerialDataStore(absolutePathDirectory);
                      sds.setName(NAME_DATASTORE);
                  }catch(Exception e) {
                      //or
                      DataStore ds = Factory.openDataStore("gate.persist.SerialDataStore", "file://" + absolutePathDirectory);
                      sds = (SerialDataStore) ds;
                  }
              }
              sds.open();
              logger.info("Datastore " + sds.getName() + " opened");
           }catch(PersistenceException e){
              try {
                  sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
                  sds.setName(NAME_DATASTORE);
                  sds.open();
                  logger.info( "Datastore " + sds.getName() + " opened");
              } catch (PersistenceException ex) {
                  logger.error( e.getMessage(),e);
              }
          }
       }

    /**
     * Method to open the connection to the gate datastore.
     */
    public void openDataStore() {
         try{
             if((DS_DIR!=null) || (sds==null)){
                sds = new SerialDataStore(DS_DIR);
                sds.setName(NAME_DATASTORE);
             }
             logger.info( "Try to open the Datastore on directory:" + DS_DIR + "...");
             //sds =(SerialDataStore) Factory.openDataStore("gate.persist.SerialDataStore","file://"+DS_DIR);
             sds.open();
             logger.info( "...opened Datastore " + sds.getName() + "!!!");
        } catch(PersistenceException e){
              try {
                  sds  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore",DS_DIR);
                  sds.setName(NAME_DATASTORE);
                  sds.open();
                  logger.info( "Try to opne the Datastore directory:" + DS_DIR + "...");
              } catch (PersistenceException ex) {
                  logger.info( ex.getMessage(), ex);
              }

        }
    }

   /**
    * Method to Import/Save Document on the DataStore.
    * @param doc document to support.
    * @return document you have saved.
    * @throws PersistenceException error.
    * @throws SecurityException error.
    */
   public Document importDocumentToDataStore(Document doc) throws PersistenceException{
       //SecurityInfo is ingored for SerialDataStore - just pass null
       Document persistDoc = (Document)sds.adopt(doc);
       sds.sync(persistDoc);
       logger.info( "Document " + doc.getName() + " save to Datastore with the name " + persistDoc.getName());
       return persistDoc;
   }

   /**
    * Method to update/Change the name of the Document doc on the DataStore.
    * @param persistDoc document to update.
    * @param newName new name of the document.
    * @throws PersistenceException error.
    * @throws SecurityException  error.
    */
   public void updateDocumentNameOnTheDataStore(Document persistDoc,String newName) throws PersistenceException{
       String oldName = persistDoc.getName();
       persistDoc.setName(newName);
       persistDoc.sync();
       logger.info("Document: " + oldName + " on the Datastore has a new name: " + persistDoc.getName());
   }

   /**
    * Method to Load document from datastore.
    * @param docID id of the document.
    * @return document gate.
    * @throws ResourceInstantiationException error.
    */
   public Document loadDocumentFromDataStoreByID(Object docID) throws ResourceInstantiationException{
       //persistDoc = (Document)sds.adopt(doc,securityInfo);
       //sds.sync(persistDoc);
       FeatureMap docFeatures = Factory.newFeatureMap();
       docFeatures.put(DataStore.LR_ID_FEATURE_NAME, docID);
       docFeatures.put(DataStore.DATASTORE_FEATURE_NAME, sds);
       Document persistDoc = (Document)Factory.createResource("gate.corpora.DocumentImpl", docFeatures);
       logger.info( "Document" + persistDoc.getName() + " is loaded from the DataStore!!!");
       return persistDoc;
   }

   /**
    * Method to Delete document from the DataStore. *
    * @param persistDoc document to delete.
    * @throws PersistenceException error.
    */
   public void deleteDocumentFromDataStore(Document persistDoc) throws PersistenceException{
       Object docID  = persistDoc.getLRPersistenceId();
       sds.delete("gate.corpora.DocumentImpl", docID);
       logger.info("The document " + persistDoc.getName() + " is deleted from the DataStore!!!");
   }

   /**
    * Method to Load all the corpus on the DataStore.
    * @return list of corpus
    * @throws PersistenceException error.
    * @throws ResourceInstantiationException error.
    */
   public List<Corpus> loadAllCorpusOnTheDataStore() throws PersistenceException, ResourceInstantiationException{
       // list the corpora/documents in the DS
       openDataStore(DS_DIR);
       List<String> corpusIds = sds.getLrIds("gate.corpora.SerialCorpusImpl");
       List<Corpus> listCorpus = new ArrayList<>();
       //FeatureMap fm = Factory.newFeatureMap();
       //fm.put(DataStore.DATASTORE_FEATURE_NAME, sds);
       //fm.put(DataStore.LR_ID_FEATURE_NAME, corpusIds.get(0));
       //*********************************************************
       //corpusIds.set(0, corpusIds.get(0));
       //fm[DataStore.DATASTORE_FEATURE_NAME] = ds;
       //fm[DataStore.LR_ID_FEATURE_NAME] = corpusIds[0];
       //List docIds = ds.getLrIds("gate.corpora.DocumentImpl");
       for(int i =0; i < corpusIds.size(); i++){
           System.out.println("("+i+")"+"ID CORPUS:"+ corpusIds.get(i));
           FeatureMap fm = Factory.newFeatureMap();
           fm.put(DataStore.DATASTORE_FEATURE_NAME, sds);
           fm.put(DataStore.LR_ID_FEATURE_NAME, corpusIds.get(i));
           Corpus c = (Corpus)Factory.createResource("gate.corpora.SerialCorpusImpl", fm);
           logger.info( "(" + i + ")" + "CORPUS NAME:" + c.getName());
           logger.info( "(" + i + ")" + "ID NAME:" + c.getLRPersistenceId());
           listCorpus.add(c);
       }
       //Corpus c = (Corpus)Factory.toResource("gate.corpora.SerialCorpusImpl", fm);
       return listCorpus;
       // similarly for documents, just use gate.corpora.DocumentImpl as the class name
   }

   /**
    * Method to Copy all document on a corpus to another corpus.
    * @param nome_merge_corpus name of the merged corpus.
    * @param listCorpus list of corpus in the datastore.
    * @return corpus gate.
    * @throws PersistenceException error.
    * @throws ResourceInstantiationException error.
    */
   public Corpus mergeDocumentsOfMultipleCorpusWithChooseOfCorpusOnTheDataStore(
           String nome_merge_corpus,List<Corpus> listCorpus) throws PersistenceException, ResourceInstantiationException{
       Corpus finalCorpus = Factory.newCorpus(nome_merge_corpus);
       //listCorpus = loadAllCorpusOnTheDataStore();
       for(Corpus corpus: listCorpus){
           String name_corpus = corpus.getName();
           logger.info( name_corpus.toUpperCase());
           for(Document doc: corpus){
               String old_name_doc = doc.getName();
               String new_name_doc =name_corpus+"_"+old_name_doc;
               logger.info( "Document of Corpus:" + new_name_doc);
               doc.setName(new_name_doc);
               finalCorpus.add(doc);
           }
       }
       return finalCorpus;
   }

   /**
    * Method to Copy all document on a corpus to another corpus.
    * @param nome_merge_corpus name of the merged corpus.
    * @return corpus gate.
    * @throws PersistenceException error.
    * @throws ResourceInstantiationException error.
    */
   public Corpus mergeDocumentsOfMultipleCorpusWithoutChooseOfCorpusOnTheDataStore(String nome_merge_corpus)
           throws PersistenceException, ResourceInstantiationException{
       Corpus finalCorpus = Factory.newCorpus(nome_merge_corpus);
       List<Corpus> listCorpus = loadAllCorpusOnTheDataStore();
       for(Corpus corpus: listCorpus){
           String name_corpus = corpus.getName();
           logger.info( name_corpus.toUpperCase());
           for(Document doc: corpus){
               String old_name_doc = doc.getName();
               String new_name_doc =name_corpus+"_"+old_name_doc;
               logger.info( "Document of the Corpus:" + new_name_doc);
               doc.setName(new_name_doc);
               finalCorpus.add(doc);
           }
       }
       return finalCorpus;
   }

   public void saveACorpusOnTheDataStore(Corpus corp) throws PersistenceException, SecurityException{
       //insert test corpus
       Corpus persistCorp = (Corpus)sds.adopt(corp);
       sds.sync(persistCorp);
       logger.info( "corpus saved in datastore...");
       Object corpusID  = persistCorp.getLRPersistenceId();
       logger.info("ID del Corpus salvato nel datastore:" + corpusID.toString());
   }

    //SETTER AND GETTER
    public String getDS_DIR() {
        return DS_DIR;
    }

    public void setDS_DIR(String DS_DIR) {
        this.DS_DIR = DS_DIR;
    }

    public String getNAME_DATASTORE() {
        return NAME_DATASTORE;
    }

    public void setNAME_DATASTORE(String NAME_DATASTORE) {
        this.NAME_DATASTORE = NAME_DATASTORE;
    }

}
    

