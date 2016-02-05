package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.util.bean.BeansKit;
import com.github.p4535992.util.file.FileUtilities;
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
    /** Gate Document */
    private Document gateDoc;
    /** Gate processor document */
    private DocumentProcessor procDoc;
    /** Base Directory for Gate Files*/
    private String baseDirectory;
    /** Check if the GATE API is already set for this Thread */
    private boolean gateAlreadySetted = false;

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
            logger.info("Initializing GATE...");
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
                FileUtilities.toFile(configFileSession);
                Gate.init();
            }catch(GateException ex){
                logger.error(e.getMessage(),e);
            }
        }
        logger.info("...GATE initialized");
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
        logger.info("Loading file .gapp/.xgapp...");
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
            logger.info("... file .gapp/.xgapp loaded!");
        }catch(GateException|IOException e){
            logger.warn(e.getMessage(), e);
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
            logger.warn(e.getMessage(), e);
        }
        return procDoc;
    }

    /**
     * Method to set the GATE Embedded API.
     * @param directoryFolderHome the root directory where all files of gate are stored eg:"gate_files".
     * @param directoryFolderPlugin  the root directory of all plugin of gate under the directoryFolderHome eg: "plugins".
     * @param configFileGate the path to the config file of gate under the directoryFolderHome eg:"gate.xml".
     * @param configFileUser the path to the config file user of gate under the directoryFolderHome eg:"user-gate.xml".
     * @param configFileSession the path to the config file session of gate under the directoryFolderHome eg:"gate.session".
     * @param gappFile the path to the gapp file user of gate under the directoryFolderHome eg:"custom/gapp/test.xgapp".
     * @return the GATE Controller.
     */
    public Controller setGate(String directoryFolderHome,String directoryFolderPlugin,
                              String configFileGate,String configFileUser,String configFileSession,String gappFile){
        if(controller==null) {
            if(gateAlreadySetted) {
                this.controller = setUpGateEmbedded(directoryFolderHome, directoryFolderPlugin,
                        configFileGate, configFileUser, configFileSession, gappFile);
                gateAlreadySetted = false;
                return controller;
            }else{
                logger.warn("The GATE embedded API is already set with Spring Framework and ProcessorDocument!!!");
                return null;
            }
        }else{
            logger.warn("The GATE embedded API is already set with Corpus Controller!!!");
            return controller;
        }
    }

    /**
     * Method to set the GATE Embedded API with Spring framework.
     * @param pathToTheGateContextFile path to the GATE Context File eg: "gate/gate-beans.xml".
     * @param beanNameOfTheProcessorDocument string name of the bean for the DocumentProcessor class on the
     *                                       gate context file eg:"documentProcessor".
     * @param thisClass class here you want invoke this method necessary for avoid exception with spring.
     * @return the GATE DocumentProcessor.
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
                return null;
            }
        }else {
            logger.warn("The GATE embedded API is already set with Spring Framework and ProcessorDocument!!!");
            return procDoc;
        }
    }

    //--------------------------------------------------------------------------------------------




}
