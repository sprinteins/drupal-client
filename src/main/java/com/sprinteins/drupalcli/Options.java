package com.sprinteins.drupalcli;

import java.util.ArrayList;
import java.util.Collections;

public class Options {

    public static final String API_PAGE_OPTIONS_KEY = "api-page";
    public static final String API_PAGE_DIRECTORY_OPTIONS_KEY = "api-page-directory";
    public static final String PORTAL_ENV_OPTIONS_KEY = "portal-environment";
    public static final String DISABLE_CHECKS_OPTIONS_KEY = "explicitly-disable-checks";

    private Long nodeID;
    String apiDirectory;
    String portalEnv;
    ArrayList<String> disabledChecks;

    public Long getNodeID() { return nodeID; }
    public void setNodeID(Long nodeID) { this.nodeID = nodeID; }

    public String getApiDirectory() { return apiDirectory; }
    public void setApiDirectory(String apiDirectory) { this.apiDirectory = apiDirectory; }

    public String getPortalEnv() { return portalEnv; }
    public void setPortalEnv(String portalEnv) { this.portalEnv = portalEnv; }

    public ArrayList<String> getDisabledChecks() { return disabledChecks; }
    public void setDisabledChecks(ArrayList<String> disabledChecks) { this.disabledChecks = disabledChecks; }

    public Options(String[] args){
        for(String argument:args){
            String[] parts = argument.substring(2).split("=");
            switch (parts[0]) {
                case API_PAGE_OPTIONS_KEY:
                    setNodeID(Long.parseLong(parts[1]));
                    break;
                case API_PAGE_DIRECTORY_OPTIONS_KEY:
                    setApiDirectory(parts[1]);
                    break;
                case PORTAL_ENV_OPTIONS_KEY:
                    setPortalEnv(parts[1]);
                    break;
                case DISABLE_CHECKS_OPTIONS_KEY:
                    String[] checks = parts[1].split(",");
                    if(checks.length != 0){
                        setDisabledChecks(new ArrayList<String>());
                        Collections.addAll(disabledChecks, checks);
                    }
                    break;
            }
        }
    }
}
