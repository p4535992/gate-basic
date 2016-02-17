package com.github.p4535992.gatebasic.object;

/**
 * Created by 4535992 on 28/01/2016.
 * @author 4535992.
 */
public class GateEmbedded {

    private String directoryFolderHome;
    private String directoryFolderPlugin;
    private String configFileGate;
    private String configFileUser;
    private String configFileSession;
    private String gappFile;

    public GateEmbedded(){}

    public GateEmbedded(String directoryFolderHome, String directoryFolderPlugin, String configFileGate, String configFileUser, String configFileSession, String gappFile) {
        this.directoryFolderHome = directoryFolderHome;
        this.directoryFolderPlugin = directoryFolderPlugin;
        this.configFileGate = configFileGate;
        this.configFileUser = configFileUser;
        this.configFileSession = configFileSession;
        this.gappFile = gappFile;
    }

    public String getDirectoryFolderHome() {
        return directoryFolderHome;
    }

    public void setDirectoryFolderHome(String directoryFolderHome) {
        this.directoryFolderHome = directoryFolderHome;
    }

    public String getDirectoryFolderPlugin() {
        return directoryFolderPlugin;
    }

    public void setDirectoryFolderPlugin(String directoryFolderPlugin) {
        this.directoryFolderPlugin = directoryFolderPlugin;
    }

    public String getConfigFileGate() {
        return configFileGate;
    }

    public void setConfigFileGate(String configFileGate) {
        this.configFileGate = configFileGate;
    }

    public String getConfigFileUser() {
        return configFileUser;
    }

    public void setConfigFileUser(String configFileUser) {
        this.configFileUser = configFileUser;
    }

    public String getConfigFileSession() {
        return configFileSession;
    }

    public void setConfigFileSession(String configFileSession) {
        this.configFileSession = configFileSession;
    }

    public String getGappFile() {
        return gappFile;
    }

    public void setGappFile(String gappFile) {
        this.gappFile = gappFile;
    }

    @Override
    public String toString() {
        return "GateEmbedded{" +
                "directoryFolderHome='" + directoryFolderHome + '\'' +
                ", directoryFolderPlugin='" + directoryFolderPlugin + '\'' +
                ", configFileGate='" + configFileGate + '\'' +
                ", configFileUser='" + configFileUser + '\'' +
                ", configFileSession='" + configFileSession + '\'' +
                ", gappFile='" + gappFile + '\'' +
                '}';
    }
}
