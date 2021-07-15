package com.sprinteins.drupalcli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Options {

    public static final String API_PAGE_OPTIONS_KEY = "api-page";
    public static final String API_PAGE_DIRECTORY_OPTIONS_KEY = "api-page-directory";
    public static final String PORTAL_ENV_OPTIONS_KEY = "portal-environment";
    public static final String DISABLE_CHECKS_OPTIONS_KEY = "explicitly-disable-checks";

    private Long nodeID;
    private String apiDirectory;
    private String portalEnv;
    private List<String> disabledChecks;

    public Long getNodeID() { return nodeID; }
    public void setNodeID(Long nodeID) { this.nodeID = nodeID; }

    public String getApiDirectory() { return apiDirectory; }
    public void setApiDirectory(String apiDirectory) { this.apiDirectory = apiDirectory; }

    public String getPortalEnv() { return portalEnv; }
    public void setPortalEnv(String portalEnv) { this.portalEnv = portalEnv; }

    public List<String> getDisabledChecks() { return Optional.ofNullable(disabledChecks).map(List::copyOf).orElse(null); }
    public void setDisabledChecks(List<String> disabledChecks) { this.disabledChecks = Optional.ofNullable(disabledChecks).map(List::copyOf).orElse(null); }

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
                        List<String> disabledChecks = new ArrayList<String>();
                        if (this.disabledChecks != null) {
                            disabledChecks.addAll(this.disabledChecks);
                        }
                        Collections.addAll(disabledChecks, checks);
                        setDisabledChecks(disabledChecks);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
