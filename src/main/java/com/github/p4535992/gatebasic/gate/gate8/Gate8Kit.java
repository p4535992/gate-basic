package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.gatebasic.object.GateEmbedded;
import com.github.p4535992.gatebasic.util.BeansKit;
import gate.*;
import gate.creole.ANNIEConstants;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.gui.MainFrame;
import gate.persist.PersistenceException;
import gate.util.DocumentProcessor;
import gate.util.GateException;
import gate.util.LanguageAnalyserDocumentProcessor;
import gate.util.persistence.PersistenceManager;
import org.springframework.context.support.GenericApplicationContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by 4535992 on 17/04/2015.
 * @author 4535992
 * @version 2015-11-12
 */
@SuppressWarnings("unused")
public class Gate8Kit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Gate8Kit.class);

    /** The Corpus Pipeline application to contain ANNE,Lingpipe,Tools,ecc. */
    private boolean showGate;
    /** Gate Corpus Controller*/
    private Controller controller;
    /** Gapp File */
    private File gappFile;
    /* Gate Document */
    //private Document gateDoc;
    /** Gate processor document */
    private DocumentProcessor procDoc;
    /** Base Directory for Gate Files*/
    private String baseDirectory;
    /** Check if the GATE API is already set for this Thread */
    private boolean gateAlreadySetted = false;
    /* Gate Embedded Info */
    private GateEmbedded gateEmbedded;

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

    public Controller getCorpusController() {
        return controller;
    }

    public void setCorpusController(CorpusController controller) {
        this.controller = controller;
    }


    /**
     * Method for setup the GATE API in Embedded mode with manual configuration.
     * @param directoryFolderHome the {@link String} path to the directory folder where all GATE files are stored.
     * @param directoryFolderPlugin the {@link String} path to the directory folder where all PLUGIN GATE files are stored.
     * @param configFileGate the {@link String} path to the file configuration of gate eg gate.xml.
     * @param configFileUser the {@link String} path to the file configuration of user gate eg user.xml.
     * @param configFileSession the {@link String} path to the file configuration of where write and save the session of GATE eg gate.session
     *                          if null is stoed on the user folder on the system.
     * @param gappFile the {@link String} absolute path to the file gapp.
     * @return the gate {@link CorpusController} full setted.
     */
    public CorpusController setUpGateEmbedded(String directoryFolderHome,String directoryFolderPlugin,
                                              String configFileGate,String configFileUser,String configFileSession,String gappFile) {
        return setUpGateEmbedded(directoryFolderHome,directoryFolderPlugin,configFileGate,configFileUser,configFileSession,gappFile,false);
    }
    /**
     * Method for setup the GATE API in EMbedded mode with manual configuration.
     * @param directoryFolderHome the {@link String} path to the directory folder where all GATE files are stored.
     * @param directoryFolderPlugin the {@link String} path to the directory folder where all PLUGIN GATE files are stored.
     * @param configFileGate the {@link String} path to the file configuration of gate eg gate.xml.
     * @param configFileUser the {@link String} path to the file configuration of user gate eg user.xml.
     * @param configFileSession the {@link String} path to the file configuration of where write and save the session of GATE eg gate.session
     *                          if null is stoed on the user folder on the system.
     * @param gappFile the {@link String} absolute path to the file gapp.
     * @param useOnlyAbsoluteReference the {@link Boolean} if true you use absolute reference for set the element of gate.
     * @return the gate {@link CorpusController} full set.
     */
    public CorpusController setUpGateEmbedded(String directoryFolderHome,String directoryFolderPlugin,
                            String configFileGate,String configFileUser,String configFileSession,
                                              String gappFile,boolean useOnlyAbsoluteReference){
        //SET GATE EMBEDDED
        try {
            logger.info("Initializing GATE...");
            if (new File(directoryFolderHome).isAbsolute()) {
                /*if (directoryFolderHome.endsWith(File.separator))
                    directoryFolderHome = directoryFolderHome.substring(0, directoryFolderHome.length() - 1);*/
                if (!directoryFolderHome.endsWith(File.separator))
                    directoryFolderHome = directoryFolderHome + File.separator;
                baseDirectory = directoryFolderHome;
            } else {
                if (!directoryFolderHome.startsWith(File.separator))
                    directoryFolderHome = File.separator + directoryFolderHome;
               /* if (directoryFolderHome.endsWith(File.separator))
                    directoryFolderHome = directoryFolderHome.substring(0, directoryFolderHome.length() - 1);*/
                if (!directoryFolderHome.endsWith(File.separator))
                    directoryFolderHome = directoryFolderHome + File.separator;
                this.baseDirectory = System.getProperty("user.dir");
                directoryFolderHome = baseDirectory + directoryFolderHome;
                baseDirectory = directoryFolderHome;
            }
            logger.warn("The base directory you using is :" + baseDirectory);

          /*  Path path = Paths.get(directoryFolderHome);
            if (!Files.exists(path)|| Files.isDirectory(path))*/
            if (!new File(directoryFolderHome).exists())
                throw new IOException("The folder directoryFolderHome " + directoryFolderHome + " of GATE not exists!");
            Gate.setGateHome(new File(directoryFolderHome));
            logger.warn("The GATE_HOME you using is :" + directoryFolderHome);

            if (directoryFolderHome.endsWith(File.separator))
                directoryFolderHome = directoryFolderHome.substring(0, directoryFolderHome.length() - 1);

            setUpAndCopyFile(baseDirectory, directoryFolderPlugin, useOnlyAbsoluteReference);

            if (new File(directoryFolderPlugin).isAbsolute()) {
                setUpAndCopyFile(new File(directoryFolderPlugin).getParent(), new File(directoryFolderPlugin).getName(), useOnlyAbsoluteReference);
            }else{
                setUpAndCopyFile(baseDirectory, directoryFolderPlugin, useOnlyAbsoluteReference);
                if (!directoryFolderPlugin.startsWith(File.separator))
                    directoryFolderPlugin = File.separator + directoryFolderPlugin;
                if (directoryFolderPlugin.endsWith(File.separator))
                    directoryFolderPlugin = directoryFolderPlugin.substring(0, directoryFolderPlugin.length() - 1);

                directoryFolderPlugin = directoryFolderHome + directoryFolderPlugin;
            }
            if (!new File(directoryFolderPlugin).exists())
                throw new IOException("The folder directoryFolderPlugin " + directoryFolderPlugin + "of GATE not exists!");
            Gate.setPluginsHome(new File(directoryFolderPlugin));
            logger.warn("The GATE_PLUGIN_HOME you using is :" + directoryFolderPlugin);
            if (new File(configFileGate).isAbsolute()) {
                setUpAndCopyFile(new File(configFileGate).getParent(), new File(configFileGate).getName(), useOnlyAbsoluteReference);
            }else {
                setUpAndCopyFile(baseDirectory, configFileGate, useOnlyAbsoluteReference);
                if (!configFileGate.startsWith(File.separator)) configFileGate = File.separator + configFileGate;
                if (configFileGate.endsWith(File.separator))
                    configFileGate = configFileGate.substring(0, configFileGate.length() - 1);

                configFileGate = directoryFolderHome + configFileGate;
            }
            if (!new File(configFileGate).exists())
                throw new IOException("The configFileGate " + configFileGate + "of GATE not exists!");
            Gate.setSiteConfigFile(new File(configFileGate));
            logger.warn("The GATE_SITE_CONFIG_FILE you using is :" + configFileGate);
            if (new File(configFileUser).isAbsolute()) {
                setUpAndCopyFile(new File(configFileUser).getParent(), new File(configFileUser).getName(), useOnlyAbsoluteReference);
            }else {
                setUpAndCopyFile(baseDirectory, configFileUser, useOnlyAbsoluteReference);

                if (!configFileUser.startsWith(File.separator)) configFileUser = File.separator + configFileUser;
                if (configFileUser.endsWith(File.separator))
                    configFileUser = configFileUser.substring(0, configFileUser.length() - 1);

                configFileUser = directoryFolderHome + configFileUser;
            }
            if (!new File(configFileUser).exists())
                throw new IOException("The configFileUser " + configFileUser + " of GATE not exists!");
            Gate.setUserConfigFile(new File(configFileUser));
            logger.warn("The GATE_USER_CONFIG_FILE you using is :" + configFileGate);

            if (new File(configFileSession).isAbsolute()) {
                setUpAndCopyFile(new File(configFileSession).getParent(), new File(configFileSession).getName(), useOnlyAbsoluteReference);
            }else {
                setUpAndCopyFile(baseDirectory, configFileSession, useOnlyAbsoluteReference);

                if (!configFileSession.startsWith(File.separator))
                    configFileSession = File.separator + configFileSession;
                if (configFileSession.endsWith(File.separator))
                    configFileSession = configFileSession.substring(0, configFileSession.length() - 1);

                configFileSession = directoryFolderHome + configFileSession;
            }
            if (!new File(configFileSession).exists())
                throw new IOException("The configFileSession " + configFileSession + " of GATE not exists!");
            Gate.setUserSessionFile(new File(configFileSession));
            logger.warn("The GATE_USER_SESSION_FILE you using is :" + configFileGate);
        } catch(IllegalStateException e){
            logger.warn("Some configuration file of GATE is has already been set:"+e.getMessage(), e);
        }catch(IOException e) {
            logger.error("Failed the initialization of GATE:" +e.getMessage(),e);
        }
        //...TRY A SECOND TIME TO INITIALIZE GATE
        try {
            Gate.init();
        }catch(GateException e){
            //..Usuallly you got here for bad reading of the session file
            try {
                File config = new File(configFileSession);
                if(!(config.isFile() && config.exists())) {
                    try {
                        if(config.createNewFile()){
                            Gate.init();
                        }
                    } catch (IOException e1) {
                        throw new GateException("Can't set the configuration session file:"
                                +config.getAbsolutePath()+"->"+e1.getMessage(),e1);
                    }
                }else{
                    throw new GateException("Can't set the configuration session file:" +config.getAbsolutePath());
                }
            }catch(GateException ex){
                logger.error(ex.getMessage(),ex);
                return null;
            }
        }
        logger.info("...GATE initialized");
        gateAlreadySetted = true;
        gateEmbedded = new GateEmbedded(directoryFolderHome,directoryFolderPlugin,configFileGate,configFileUser,configFileSession,null);
        if(showGate) {
            //Work with graphic GATE interface
            MainFrame.getInstance().setVisible(true);
        }
        return (CorpusController) loadGapp(gappFile);
    }

    /**
     * Method for make a brute force copy of the necessary resources under the directory of GATE_HOME already setted.
     * @param gateHome the {@link String} path to the directory GATE_HOME
     * @param resourceGate the {@link String} path of the external resource to add to GATE_HOME.
     * @param useOnlyAbsoluteReference the {@link Boolean} if true the method work with the absolutes path of the files.
     * @return the {@link String} path to the resource under the GATE_HOME folder.
     */
    private String setUpAndCopyFile(String gateHome,String resourceGate,boolean useOnlyAbsoluteReference){
        if(new File(resourceGate).isAbsolute() && !useOnlyAbsoluteReference) {
            resourceGate = resourceGate.replace(gateHome,"");
        }else{
            if(!isFileOnDirectory(gateHome,resourceGate)){
                logger.warn("Force the copy of the folder directoryFolderPlugin " + resourceGate
                        + " to the directoryFolderHome GATE "+gateHome);
                if(!copyFileToDirectoryGATE(gateHome,resourceGate)) {
                    logger.error("Something wrong during the copy of the resource on the GATE directory");
                }
            }
        }
        return resourceGate;
    }

    /**
     * Method for load a gapp file and generate the controller for this session of gate.
     * @param base the {@link String} base directory/folder.
     * @param fileGapp the {@link String} filepath ot the gapp file.
     * @return the {@link Controller} of gate of the gapp file.
     */
    public Controller loadGapp(String base,String fileGapp){
        if(!base.startsWith(File.separator)) base = File.separator + base;
        if(!base.endsWith(File.separator)) base = base + File.separator;
        if(fileGapp.startsWith(File.separator)) fileGapp = fileGapp.substring(1,fileGapp.length());
        if(fileGapp.endsWith(File.separator)) fileGapp = fileGapp.substring(0,fileGapp.length()-1);
        return loadGapp(base + fileGapp);
    } // initAnnie()

    /**
     * Method for load a gapp file and generate the controller for this session of gate.
     * @param fileGapp the {@link String} filepath ot the gapp file.
     * @return the {@link Controller} of gate of the gapp file.
     */
    public Controller loadGapp(String fileGapp){
        return loadGapp(controller,fileGapp);
    }

    /**
     * Method for load a gapp file and generate the controller for this session of gate.
     * @param fileGapp the {@link File} filepath ot the gapp file.
     * @return the {@link Controller} of gate of the gapp file.
     */
    public Controller loadGapp(File fileGapp){
        return loadGapp(controller,fileGapp.getAbsolutePath());
    }

    /**
     * Method for load a gapp file and generate the controller for this session of gate.
     * @param fileGapp the {@link Path} filepath ot the gapp file.
     * @return the {@link Controller} of gate of the gapp file.
     */
    public Controller loadGapp(Path fileGapp){
        return loadGapp(controller,fileGapp.toAbsolutePath().toString());
    }

    /**
     * Method for load a gapp file and generate the controller for this session of gate.
     * @param controller the {@link Controller} of gate.
     * @param fileGapp the {@link File} filepath ot the gapp file.
     * @return the {@link Controller} of gate of the gapp file.
     */
    public Controller loadGapp(Controller controller, File fileGapp){
        return loadGapp(controller,fileGapp.getAbsolutePath());
    }

    /**
     * Method for load a gapp file and generate the controller for this session of gate.
     * @param controller the {@link Controller} of gate.
     * @param fileGapp the {@link Path} filepath ot the gapp file.
     * @return the {@link Controller} of gate of the gapp file.
     */
    public Controller loadGapp(Controller controller, Path fileGapp){
        return loadGapp(controller,fileGapp.toAbsolutePath().toString());
    }

    /**
     * Method for load a gapp file and generate the controller for this session of gate.
     * @param controller the {@link Controller} of gate.
     * @param fileGapp the {@link String} filepath ot the gapp file.
     * @return the {@link Controller} of gate of the gapp file.
     */
    public Controller loadGapp(Controller controller,String fileGapp){
        logger.info("Loading file .gapp/.xgapp from "+fileGapp+"...");
        Path path = Paths.get(fileGapp);
        if(Files.exists(path) && !Files.isDirectory(path)){
            logger.warn(".. can't laod the gapp file"+fileGapp+" not exists or is a directory");
            return null;
        }
        try {
            if(path.isAbsolute()) fileGapp = fileGapp.replace(Gate.getGateHome().getAbsolutePath(),"");
            if(!fileGapp.startsWith(File.separator)) fileGapp = File.separator + fileGapp;
            if(fileGapp.endsWith(File.separator)) fileGapp = fileGapp.substring(0,fileGapp.length()-1);
            if (new File(Gate.getGateHome() +  fileGapp).exists()) {
                controller = (Controller) PersistenceManager.loadObjectFromFile(
                        new File(Gate.getGateHome() + fileGapp));
            } else {
                throw new IOException("The gapp file not exists on "+fileGapp);
            }
            logger.info("... file .gapp/.xgapp loaded!");
        }catch(GateException|IOException e){
            logger.warn(e.getMessage(), e);
        }
        return controller;
    } // initAnnie()

    /**
     * Method for setup the GATE API in Embedded mode with spring configuration.
     * OLD_NAME: initGateWithSpring
     * @param referencePathResourceFile the {@link String} reference path to the resource file spring for gate context
     *                                  eg:"gate/gate_context.xml".
     * @param thisClass the {@link Class}reference to the invoke class.
     * @param idBeanDocumentProcessor the {@link String} name/id of the bean reference to DocumentProcessor on the gate context.
     * @return the DocumentProcessor Controller.
     */
    public DocumentProcessor setUpGateEmbeddedWithSpring(
            String referencePathResourceFile,Class<?> thisClass,String idBeanDocumentProcessor){
        GenericApplicationContext ctx;
        try {
            ctx = (GenericApplicationContext) BeansKit.tryGetContextSpring(referencePathResourceFile, thisClass);
            //GATE provides a DocumentProcessor interface suitable for use with Spring pooling
            //procDoc = BeansKit.getBeanFromContext("documentProcessor",DocumentProcessor.class,ctx);
            //procDoc = BeansKit.getBeanFromContext(idBeanDocumentProcessor,DocumentProcessor.class,ctx);
            try{
                procDoc = ctx.getBean(idBeanDocumentProcessor, DocumentProcessor.class);
            }catch(java.lang.IllegalStateException e){
                try {
                    ctx.refresh();
                    procDoc = ctx.getBean(DocumentProcessor.class);
                }catch(Exception e2){
                    logger.error(e2.getMessage(),e2);
                }
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return procDoc;
    }

    /**
     * Method for setup the GATE API in Embedded mode with spring configuration.
     * @param referencePathResourceFile the {@link String} reference path to the resource file spring for gate context
     *                                  eg:"gate/gate_context.xml".
     * @param idBeanDocumentProcessor the {@link String} name/id of the bean reference to DocumentProcessor on the gate context.
     * @return the DocumentProcessor Controller.
     */
    public DocumentProcessor setUpGateEmbeddedWithSpring(
            String referencePathResourceFile,String idBeanDocumentProcessor){
        return setUpGateEmbeddedWithSpring(referencePathResourceFile,Gate8Kit.class,idBeanDocumentProcessor);
    }

    /**
     * Method for setup the GATE API in Embedded mode with spring configuration.
     * @param referencePathResourceFile the {@link String} reference path to the resource file spring for gate context
     *                                  eg:"gate/gate_context.xml".
     * @return the GATE {@link DocumentProcessor}.
     */
    public DocumentProcessor setUpGateEmbeddedWithSpring(String referencePathResourceFile){
        return setUpGateEmbeddedWithSpring(referencePathResourceFile,Gate8Kit.class,"documentProcessor");
    }

    /**
     * Method to set the GATE Embedded API.
     * @param directoryFolderHome the {@link String} root directory where all files of gate are stored eg:"gate_files".
     * @param directoryFolderPlugin the {@link String} root directory of all plugin of gate under the directoryFolderHome eg: "plugins".
     * @param configFileGate the {@link String}path to the config file of gate under the directoryFolderHome eg:"gate.xml".
     * @param configFileUser the {@link String} path to the config file user of gate under the directoryFolderHome eg:"user-gate.xml".
     * @param configFileSession the {@link String} path to the config file session of gate under the directoryFolderHome eg:"gate.session".
     * @param gappFile the {@link String} path to the gapp file user of gate under the directoryFolderHome eg:"custom/gapp/test.xgapp".
     * @return the GATE {@link CorpusController}.
     */
    public CorpusController setGate(String directoryFolderHome,String directoryFolderPlugin,
                              String configFileGate,String configFileUser,String configFileSession,String gappFile){
        if(controller ==null) {
            if(gateAlreadySetted) {
                this.controller = setUpGateEmbedded(directoryFolderHome, directoryFolderPlugin,
                        configFileGate, configFileUser, configFileSession, gappFile);
                gateAlreadySetted = false;
                return (CorpusController) controller;
            }else{
                logger.warn("The GATE embedded API is already set with Spring Framework and ProcessorDocument!!!");
                return (CorpusController) controller;
            }
        }else{
            logger.warn("The GATE embedded API is already set with Corpus Controller!!!");
            return (CorpusController) controller;
        }
    }

    /**
     * Method to set the GATE Embedded API with Spring framework.
     * @param pathToTheGateContextFile the {@link String} path to the GATE Context File eg: "gate/gate-beans.xml".
     * @param beanNameOfTheProcessorDocument the {@link String} name of the bean for the DocumentProcessor class on the
     *                                       gate context file eg:"documentProcessor".
     * @param thisClass the {@link Class} here you want invoke this method necessary for avoid exception with spring.
     * @return the GATE the {@link DocumentProcessor}.
     */
    public DocumentProcessor setGateWithSpring(
            String pathToTheGateContextFile,String beanNameOfTheProcessorDocument,Class<?> thisClass){
        if(procDoc ==null) {
            if(gateAlreadySetted) {
                this.procDoc = setUpGateEmbeddedWithSpring(pathToTheGateContextFile, thisClass, beanNameOfTheProcessorDocument);
                gateAlreadySetted = false;
                return procDoc;
            }else{
                logger.warn("The GATE embedded API is already set with Corpus Controller!!!");
                return procDoc;
            }
        }else {
            logger.warn("The GATE embedded API is already set with Spring Framework and ProcessorDocument!!!");
            return procDoc;
        }
    }

    /**
     * Methods to setup a simple ANNIE controller with GATE
     * @return the {@link SerialAnalyserController} of GATE.
     */
    public SerialAnalyserController setGateWithANNIE(){
        // load ANNIE as an application from a gapp file
        Controller controller;
        try {
            if(gateAlreadySetted) {
                controller = (SerialAnalyserController)
                        PersistenceManager.loadObjectFromFile(new File(new File(
                                Gate.getPluginsHome(), ANNIEConstants.PLUGIN_DIR),
                                ANNIEConstants.DEFAULT_FILE));
                return (SerialAnalyserController) controller;
            }else{
                throw new PersistenceException("The GATE embedded API must be set before invoking this method use one of the setGate(...) methdos");
            }
        } catch (PersistenceException | IOException | ResourceInstantiationException e) {
           logger.error(e.getMessage(),e);
            return null;
        }
    }

    public synchronized Boolean cleanup(Corpus corpus) {
        return cleanup(controller,corpus);
    }

    public synchronized Boolean cleanup(Controller controller, Corpus corpus) {
        Factory.deleteResource(controller);
        if (corpus != null) {
            Factory.deleteResource(corpus);
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////
    //Method to manage the process on the current controller of GATE
    ////////////////////////////////////////////////////////////////////////
    private Map<String, BlockingQueue<DocumentProcessor>> poolMap = new HashMap<>();
    private long docsProcessedCount;
    private long docsFailedCount;

    public void addProcess(String processName, File gappFile, int poolSize) {
        Controller template = loadGapp(gappFile);
        if (poolSize > 0) {
            ArrayBlockingQueue<DocumentProcessor> pool = new ArrayBlockingQueue<>(poolSize);
            DocumentProcessor dp = new LanguageAnalyserDocumentProcessor();
            pool.add(dp);

            for (int i = 0; i < poolSize - 1; i++) {

                try {
                    CorpusController tmp = (CorpusController) Factory.duplicate(template);
                    DocumentProcessor dpTmp = new LanguageAnalyserDocumentProcessor();
                    pool.add(dpTmp);
                } catch (ResourceInstantiationException e) {
                    logger.error("Couldn't create controller for " + gappFile.getName(), e);
                }

            }
            poolMap.put(processName, pool);
        }
    }

    public Document setAndExecuteProcess(CorpusController controller,String name, gate.Document doc) {
        DocumentProcessor processor = null;
        try {
            processor = poolMap.get(name).take();
        } catch (InterruptedException e) {
            logger.error("Couldn't get a processor from the pool", e);
        }
        if (processor == null) {
            logger.error("Couldn't get a processor from the pool");
            return doc;
        }
        try {
            executeProcess(processor,controller,null,doc);
        } catch (Exception e) {
            docsFailedCount++;
            logger.error("Document failed to process" + doc.getName(), e);
        } finally {
            docsProcessedCount++;
            poolMap.get(name).add(processor);
        }

        return doc;
    }

    private void executeProcess(DocumentProcessor processor,CorpusController controller,Corpus corpus,Document doc) {
        if (corpus == null) {
            try {
                corpus = Factory.newCorpus("DP Corpus");
                if(corpus == null)
                    throw new ResourceInstantiationException("The Corpus is NULL");
            } catch (ResourceInstantiationException e) {
                logger.error("Couldn't create new corpus", e);
            }
        }
        try {
            corpus.add(doc);
            controller.setCorpus(corpus);
            try {
                controller.execute();
                processor.processDocument(doc);
            } catch (GateException e) {
                logger.error("Couldnt execute document processing", e);
            }
        } finally {
            controller.setCorpus(null);
            if (corpus != null) {
                corpus.clear();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////
    //Method for make a clean copy of the resources on the Gate Home setted.
    ////////////////////////////////////////////////////////////////////////

    private Boolean copyFileToDirectoryGATE(String srcFile, String destDir){
        return copyFileToDirectoryGATE(Paths.get(srcFile),Paths.get(destDir));
    }

    private Boolean copyFileToDirectoryGATE(Path srcFile, Path destDir){
        if(destDir == null) {
            throw new NullPointerException("Destination must not be null");
        } else if(Files.exists(destDir) && !Files.isDirectory(destDir)) {
            throw new IllegalArgumentException("Destination \'" + destDir + "\' is not a directory");
        } else {
            return copyFile(srcFile, Paths.get(destDir.toString(), srcFile.getFileName().toString()));
        }
    }

    private Boolean copyFile(Path srcFile, Path destFile) {
        try {
            if (srcFile == null)
                throw new NullPointerException("Source must not be null");
            else if (destFile == null)
                throw new NullPointerException("Destination must not be null");
            else if (!Files.exists(srcFile))
                throw new FileNotFoundException("Source \'" + srcFile + "\' does not exist");
            else if (Files.isDirectory(srcFile))
                throw new IOException("Source \'" + srcFile + "\' exists but is a directory");
            else if (getCanonicalPath(srcFile).equals(getCanonicalPath(destFile)))
                throw new IOException("Source \'" + srcFile + "\' and destination \'" + destFile + "\' are the same");
            else if (getParentFile(destFile) != null && !getParentFile(destFile).exists() && !getParentFile(destFile).mkdirs())
                throw new IOException("Destination \'" + destFile + "\' directory cannot be created");
            else if (Files.exists(destFile) && !canWrite(destFile))
                throw new IOException("Destination \'" + destFile + "\' exists but is read-only");
            else
                Files.copy(srcFile, destFile);
            return true;
        }catch(IOException e){
            logger.error("",e);
            return false;
        }
    }

    private String getCanonicalPath(Path path){
        try {
            return new URI(path.toUri().toString()).normalize().getPath();
        } catch (URISyntaxException e) {
            try {
                return path.toFile().getCanonicalPath();
            } catch (IOException e1) {
               return path.toUri().toString();
            }
        }
    }

    private File getParentFile(Path path){
        return path.getParent().toFile();
    }

    private Boolean canWrite(Path path){
        return Files.isWritable(path);
    }

    /**
     * Method to check if  a file is direct son of the parent directory.
     * @param directory the {@link File} directory.
     * @param child the {@link String} path of the son.
     * @return the {@link Boolean} is true if the file is founded and exists.
     */
    public static Boolean isFileOnDirectory(File directory,String child){
        return new File(directory,child).exists();
    }

    /**
     * Method to check if  a file is direct son of the parent directory.
     * @param directory the {@link String} directory.
     * @param child the {@link String} path of the son.
     * @return the {@link Boolean} is true if the file is founded and exists.
     */
    public static Boolean isFileOnDirectory(String directory,String child){
        return new File(directory,child).exists();
    }

    /**
     * Method to check if  a file is direct son of the parent directory.
     * @param directory the {@link Path} directory.
     * @param child the {@link String} path of the son.
     * @return the {@link Boolean} is true if the file is founded and exists.
     */
    public static Boolean isFileOnDirectory(Path directory,String child){
        return Files.exists(Paths.get(directory.toAbsolutePath().toString(),child));
    }



    //--------------------------------------------------------------------------------------------
    // SOME UTILITY FOR LOAD THE SPRING CONFIGURATION FILE FOR GATE
    //--------------------------------------------------------------------------------------------




}
