package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.util.bean.BeansKit;
import com.github.p4535992.util.file.impl.FileUtil;
import com.github.p4535992.util.log.SystemLog;
import gate.*;
import gate.corpora.RepositioningInfo;
import gate.gui.MainFrame;
import gate.util.DocumentProcessor;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by 4535992 on 17/04/2015.
 * @author 4535992
 * @version 2015-07-02
 */
@SuppressWarnings("unused")
public class Gate8Kit {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Gate8Kit.class);

    /** The Corpus Pipeline application to contain ANNE,Lingpipe,Tools,ecc. */
    private boolean showGate;
    /** Gate Corpus Controller*/
    private Controller controller;
    /** Gapp File */
    private File gappFile;
    /** Gate Document */
    private Document gateDoc;
    /** Gate processor document */
    private DocumentProcessor procDoc;
    /** Base Directory for Gate Files*/
    private String baseDirectory;

    private static Gate8Kit instance = null;
    protected Gate8Kit(){
        this.showGate = false;
        this.baseDirectory = System.getProperty("user.dir");
    }

    public static Gate8Kit getInstance(){
        if(instance == null) {
            instance = new Gate8Kit();
        }
        return instance;
    }

    /* Getter and Setter*/
    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public  boolean isShowGate() {
        return showGate;
    }

    public  void setShowGate(boolean showGate){
        this.showGate = showGate;
    }

    public  File getGappFile() {
        return gappFile;
    }

    public  void setGappFile(File gappFile) throws Exception {
       this.gappFile = gappFile;
    }

    public DocumentProcessor getProcDoc() {
        return procDoc;
    }

    public void setProcDoc(DocumentProcessor procDoc) {
        this.procDoc = procDoc;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Method for setup the GATE API in EMbedded mode with manual configuration.
     * @param directoryFolderHome path to the directory folder where all GATE files are stored.
     * @param directoryFolderPlugin path to the directory folder where all PLUGIN GATE files are stored.
     * @param configFileGate path to the file configuration of gate eg gate.xml.
     * @param configFileUser path to the file configuration of user gate eg user.xml.
     * @param configFileSession path to the file configuration of where write and save the session of GATE eg gate.session
     *                          if null is stoed on the user folder on the system.
     * @param gappFile sting absolute path to the file gapp.
     * @return the gate controller full setted.
     */
    public Controller setUpGateEmbedded(String directoryFolderHome,String directoryFolderPlugin,
                            String configFileGate,String configFileUser,String configFileSession,String gappFile){
        //SET GATE EMBEDDED
        try {
            SystemLog.message("Initializing GATE...");
            if (!directoryFolderHome.startsWith(File.separator))
                directoryFolderHome = File.separator + directoryFolderHome;
            if (directoryFolderHome.endsWith(File.separator))
                directoryFolderHome = directoryFolderHome.substring(0, directoryFolderHome.length() - 1);

            directoryFolderHome = baseDirectory + directoryFolderHome;
            if (!new File(directoryFolderHome).exists())
                throw new IOException("The folder directoryFolderHome " + directoryFolderHome + " of GATE not exists!");
            Gate.setGateHome(new File(directoryFolderHome));

            if (!directoryFolderPlugin.startsWith(File.separator))
                directoryFolderPlugin = File.separator + directoryFolderPlugin;
            if (directoryFolderPlugin.endsWith(File.separator))
                directoryFolderPlugin = directoryFolderPlugin.substring(0, directoryFolderPlugin.length() - 1);

            directoryFolderPlugin = directoryFolderHome + directoryFolderPlugin;
            if (!new File(directoryFolderPlugin).exists())
                throw new IOException("The folder directoryFolderPlugin " + directoryFolderPlugin + "of GATE not exists!");
            Gate.setPluginsHome(new File(directoryFolderPlugin));

            if (!configFileGate.startsWith(File.separator)) configFileGate = File.separator + configFileGate;
            if (configFileGate.endsWith(File.separator))
                configFileGate = configFileGate.substring(0, configFileGate.length() - 1);

            configFileGate = directoryFolderHome + configFileGate;
            if (!new File(configFileGate).exists())
                throw new IOException("The configFileGate " + configFileGate + "of GATE not exists!");
            Gate.setSiteConfigFile(new File(configFileGate));

            if (!configFileUser.startsWith(File.separator)) configFileUser = File.separator + configFileUser;
            if (configFileUser.endsWith(File.separator))
                configFileUser = configFileUser.substring(0, configFileUser.length() - 1);

            configFileUser = directoryFolderHome + configFileUser;
            if (!new File(configFileUser).exists())
                throw new IOException("The configFileUser " + configFileUser + " of GATE not exists!");
            Gate.setUserConfigFile(new File(configFileUser));

            if (!configFileSession.startsWith(File.separator)) configFileSession = File.separator + configFileSession;
            if (configFileSession.endsWith(File.separator))
                configFileSession = configFileSession.substring(0, configFileSession.length() - 1);

            configFileSession = directoryFolderHome + configFileSession;
            if (!new File(configFileSession).exists())
                throw new IOException("The configFileSession " + configFileSession + " of GATE not exists!");
            Gate.setUserSessionFile(new File(configFileSession));
        } catch(IllegalStateException e){
            SystemLog.warning("Some configuration file of GATE is has already been set");
            SystemLog.warning(e.getMessage());
        }catch(IOException e1) {
            SystemLog.error(e1.getMessage());
            SystemLog.abort(0, "Failed the initialization of GATE");
        }
        //...TRY A SECOND TIME TO INITIALIZE GATE
        try {
            Gate.init();
        }catch(GateException e){
            //..Usuallly you got here for bad reading of the session file
            try {
                FileUtil.createFile(configFileSession);
                Gate.init();
            }catch(GateException|IOException ex){
                SystemLog.exception(e);
            }
        }
        SystemLog.message("...GATE initialized");
        if(showGate) {
            //Work with graphic GATE interface
            MainFrame.getInstance().setVisible(true);
        }
        return loadGapp(gappFile);
    }

    /**
     * Method for load a gapp file and generate the controller for this session of gate.
     * @param base string base directory/folder.
     * @param fileGapp string filepath ot the gapp file.
     * @return corpus controller of the gapp file.
     */
    public CorpusController loadGapp(String base,String fileGapp){
        if(!base.startsWith(File.separator)) base = File.separator + base;
        if(!base.endsWith(File.separator)) base = base + File.separator;
        if(fileGapp.startsWith(File.separator)) fileGapp = fileGapp.substring(1,fileGapp.length());
        if(fileGapp.endsWith(File.separator)) fileGapp = fileGapp.substring(0,fileGapp.length()-1);
        loadGapp(base + fileGapp);
        return (CorpusController)controller;
    } // initAnnie()

    /**
     * Method for load a gapp file and generate the controller for this session of gate.
     * @param fileGapp string filepath ot the gapp file.
     * @return corpus controller of the gapp file.
     */
    public CorpusController loadGapp(String fileGapp){
        SystemLog.message("Loading file .gapp/.xgapp...");
        try {
            if(!fileGapp.startsWith(File.separator)) fileGapp = File.separator + fileGapp;
            if(fileGapp.endsWith(File.separator)) fileGapp = fileGapp.substring(0,fileGapp.length()-1);
            //File gapp = new File(home.home, "custom/gapp/geoLocationPipelineFast.xgapp");
            if (new File(Gate.getGateHome() +  fileGapp).exists()) {
                controller = (CorpusController) PersistenceManager.loadObjectFromFile(
                        new File(Gate.getGateHome() + fileGapp));
            } else {
                throw new IOException("The gapp file not exists");
            }
            //CorpusController  con = (CorpusController) PersistenceManager.loadObjectFromFile(gapp);
            SystemLog.message("... file .gapp/.xgapp loaded!");
        }catch(GateException|IOException e){
            SystemLog.exception(e);
        }
        return (CorpusController) controller;
    } // initAnnie()

    /**
     * Method for setup the GATE API in EMbedded mode with spring configuration.
     * @param referencePathResourceFile string reference path to the resource file spring for gate context
     *                                  eg:"gate/gate_context.xml".
     * @param thisClass the reference to the invoke class.
     * @param idBeanDocumentProcessor string name/id of the bean refrence to DocumentProcessor on the gate context.
     * @return the DocumentProcessor Controller.
     */
    public DocumentProcessor setUpGateEmbeddedWithSpring(
            String referencePathResourceFile,Class<?> thisClass,String idBeanDocumentProcessor){
        ApplicationContext ctx;
        try {
            ctx = BeansKit.tryGetContextSpring(referencePathResourceFile, thisClass);
            //GATE provides a DocumentProcessor interface suitable for use with Spring pooling
            //procDoc = BeansKit.getBeanFromContext("documentProcessor",DocumentProcessor.class,ctx);
            procDoc = BeansKit.getBeanFromContext(idBeanDocumentProcessor,DocumentProcessor.class,ctx);
        } catch (IOException e) {
            SystemLog.exception(e);
        }
        return procDoc;
    }

    /**
     * Class for sort the annotation in a list.
     */
    public class SortedAnnotationList extends Vector<Annotation> {
        private static final long serialVersionUID = 15L;
        public SortedAnnotationList() {
            super();
        } // SortedAnnotationList

        public boolean addSortedExclusive(Annotation annot) {
            Annotation currAnot;
            // overlapping check
            /*for (int i=0; i<size(); ++i) {
                currAnot = (Annotation) get(i);
                if(annot.overlaps(currAnot)) {
                    return false;
                } // if
            } // for*/
            for (Object o : this) {
                currAnot = (Annotation) o;
                if (annot.overlaps(currAnot)) return false;
                // if
            } // for
            long annotStart = annot.getStartNode().getOffset();
            long currStart;
            // insert
            for (int i=0; i < size(); ++i) {
                currAnot = get(i);
                currStart = currAnot.getStartNode().getOffset();
                if(annotStart < currStart) {
                    super.insertElementAt(annot, i);
                    SystemLog.message("Insert start: " + annotStart + " at position: " + i + " size=" + size());
                    SystemLog.message("Current start: " + currStart);
                    return true;
                } // if
            } // for

            int size = size();
            super.insertElementAt(annot, size);
            SystemLog.message("Insert start: " + annotStart + " at size position: " + size);
            return true;
        } // addSorted
    } // SortedAnnotationList


    /**
     * Method to convert every gate document in a coprus in xml files with all the annotations you gate in them
     * @param corpus corpus  of gate.
     * @param addAnnotTypesRequired list of annotation.
     * @param directory file directory where store the html and xml file of the gate documents.
     * @throws IOException error.
     */
    public void createXMLFileForEachDoc(Corpus corpus,List<String> addAnnotTypesRequired,File directory) throws IOException{
        // for each document, get an XML document with the person,location,MyGeo names added
        String pathDir = FileUtil.path(directory);
        Iterator<Document> iter = corpus.iterator();
        int count = 0;
        String startTagPart_1 = "<span GateID=\"";
        String startTagPart_2 = "\" title=\"";
        String startTagPart_3 = "\" style=\"background:Red;\">";
        String endTag = "</span>";
        while(iter.hasNext()) {
            Document doc = iter.next();
            AnnotationSet defaultAnnotSet = doc.getAnnotations();
            Set<String> annotTypesRequired = new HashSet<>();
            for (String s : addAnnotTypesRequired) {
                annotTypesRequired.add(s);
                System.out.println(s);
            }
            // annotTypesRequired.add("Person");
            // annotTypesRequired.add("Location");
            Set<Annotation> newSetAnnotation = new HashSet<>(defaultAnnotSet.get(annotTypesRequired));
            FeatureMap features = doc.getFeatures();
            String originalContent = (String)features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
            RepositioningInfo info = (RepositioningInfo)features.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);
            ++count;
            ///GENERAZIONE DEI DOCUMENTI
            String nameGateDocument0 = doc.getName();
            String fileName0 = "("+count+")"+nameGateDocument0+".html";
            FileUtil.createFile(directory);//create file if not exists...
            System.out.println("File write to the path : '"+ directory.getAbsolutePath()+"'");
            //after the controller is execute....
            if(originalContent != null && info != null) {
                System.out.println("OrigContent and reposInfo existing. Generate file...");
                Iterator<Annotation> it = newSetAnnotation.iterator();
                Annotation currAnnot;
                SortedAnnotationList sortedAnnotations = new SortedAnnotationList();
                while(it.hasNext()) {
                    currAnnot = it.next();
                    sortedAnnotations.addSortedExclusive(currAnnot);
                } // while
                StringBuilder editableContent = new StringBuilder(originalContent);
                long insertPositionEnd;
                long insertPositionStart;
                // insert anotation tags backward
                System.out.println("Unsorted annotations count: "+newSetAnnotation.size());
                System.out.println("Sorted annotations count: "+sortedAnnotations.size());
                if(newSetAnnotation.size()>0 && sortedAnnotations.size()>0){
                    for(int i=sortedAnnotations.size()-1; i>=0; --i) {
                        currAnnot = sortedAnnotations.get(i);
                        insertPositionStart =
                                currAnnot.getStartNode().getOffset();
                        insertPositionStart = info.getOriginalPos(insertPositionStart);
                        insertPositionEnd = currAnnot.getEndNode().getOffset();
                        insertPositionEnd = info.getOriginalPos(insertPositionEnd, true);
                        if(insertPositionEnd != -1 && insertPositionStart != -1) {
                            editableContent.insert((int)insertPositionEnd, endTag);
                            editableContent.insert((int)insertPositionStart, startTagPart_3);
                            editableContent.insert((int)insertPositionStart,
                                    currAnnot.getType());
                            editableContent.insert((int)insertPositionStart, startTagPart_2);
                            editableContent.insert((int)insertPositionStart,
                                    currAnnot.getId().toString());
                            editableContent.insert((int)insertPositionStart, startTagPart_1);
                        } // if
                    } // for
                }// if size
                try (FileWriter writer = new FileWriter(directory)) {
                    writer.write(editableContent.toString());
                }
            } // if - should generate
            else if (originalContent != null) {
                System.out.println("OrigContent existing. Generate file...");

                Iterator<Annotation> it = newSetAnnotation.iterator();
                Annotation currAnnot;
                SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

                while(it.hasNext()) {
                    currAnnot = it.next();
                    sortedAnnotations.addSortedExclusive(currAnnot);
                } // while

                StringBuilder editableContent = new StringBuilder(originalContent);
                long insertPositionEnd;
                long insertPositionStart;
                // insert anotation tags backward
                SystemLog.message("Unsorted annotations count: " + newSetAnnotation.size());
                SystemLog.message("Sorted annotations count: " + sortedAnnotations.size());
                if(newSetAnnotation.size()>0 && sortedAnnotations.size()>0){
                    for(int i=sortedAnnotations.size()-1; i>=0; --i) {
                        currAnnot = sortedAnnotations.get(i);
                        insertPositionStart = currAnnot.getStartNode().getOffset();
                        insertPositionEnd = currAnnot.getEndNode().getOffset();
                        if(insertPositionEnd != -1 && insertPositionStart != -1) {
                            editableContent.insert((int)insertPositionEnd, endTag);
                            editableContent.insert((int)insertPositionStart, startTagPart_3);
                            editableContent.insert((int)insertPositionStart,
                                    currAnnot.getType());
                            editableContent.insert((int)insertPositionStart, startTagPart_2);
                            editableContent.insert((int) insertPositionStart,
                                    currAnnot.getId().toString());
                            editableContent.insert((int)insertPositionStart, startTagPart_1);
                        } // if
                    } // for
                }//if size
                try (FileWriter writer = new FileWriter(directory)) {
                    writer.write(editableContent.toString());
                }
            }
            else {
                SystemLog.message("Content : " + doc.getContent().toString());
                SystemLog.message("Repositioning: " + info);
            }

            String xmlDocument = doc.toXml(newSetAnnotation, false);
            String fileName = "("+count+")"+doc.getName()+".xml";
            //File dir2 = new File ("gate_files");
            File actualFile = new File (fileName);
            try (FileWriter writer2 = new FileWriter(actualFile)) {
                writer2.write(xmlDocument);
            }
        }
    }


}
