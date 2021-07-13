package com.sprinteins.drupalcli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OptionsTest {

    @Test
    public void testApiPageID() throws Exception {
        String[] args = new String[]{"--api-page=94"};
        Options options = new Options(args);
        Assertions.assertEquals(94, options.getNodeID());
    }

    @Test
    public void testApiPageDirectory() throws Exception {
        String[] args = new String[]{"--api-page-directory=./api-page"};
        Options options = new Options(args);
        Assertions.assertEquals("./api-page", options.getApiDirectory());
    }

    @Test
    public void testPortalEnvironment() throws Exception {
        String[] args = new String[]{"--portal-environment=\"https://dhlapi-dev.metadeploy.com/\""};
        Options options = new Options(args);
        Assertions.assertEquals("\"https://dhlapi-dev.metadeploy.com/\"", options.getPortalEnv());
    }

    @Test
    public void testSingleExplicitlyDisableCheck() throws Exception {
        String[] args = new String[]{"--explicitly-disable-checks=LINTING"};
        Options options = new Options(args);
        Assertions.assertEquals("LINTING", options.getDisabledChecks().get(0));
    }

    @Test
    public void testMultipleExplicitlyDisableChecks() throws Exception {
        String[] args = new String[]{"--explicitly-disable-checks=LINTING,CONVERSION,SANITYCHECKS"};
        Options options = new Options(args);
        Assertions.assertEquals("LINTING", options.getDisabledChecks().get(0));
        Assertions.assertEquals("CONVERSION", options.getDisabledChecks().get(1));
        Assertions.assertEquals("SANITYCHECKS", options.getDisabledChecks().get(2));
    }

    @Test
    public void testFullOptionsWithSingleExplicitlyDisableCheck() throws Exception {
        String[] args = new String[]{"--api-page=94", "--api-page-directory=./api-page", "--portal-environment=\"https://dhlapi-dev.metadeploy.com/\"", "--explicitly-disable-checks=LINTING"};
        Options options = new Options(args);
        Assertions.assertEquals(94, options.getNodeID());
        Assertions.assertEquals("./api-page", options.getApiDirectory());
        Assertions.assertEquals("\"https://dhlapi-dev.metadeploy.com/\"", options.getPortalEnv());
        Assertions.assertEquals("LINTING", options.getDisabledChecks().get(0));    }

    @Test
    public void testFullOptionsWithMultipleExplicitlyDisableChecks() throws Exception {
        String[] args = new String[]{"--api-page=94", "--api-page-directory=./api-page", "--portal-environment=\"https://dhlapi-dev.metadeploy.com/\"", "--explicitly-disable-checks=LINTING,CONVERSION,SANITYCHECKS"};
        Options options = new Options(args);
        Assertions.assertEquals(94, options.getNodeID());
        Assertions.assertEquals("./api-page", options.getApiDirectory());
        Assertions.assertEquals("\"https://dhlapi-dev.metadeploy.com/\"", options.getPortalEnv());
        Assertions.assertEquals("LINTING", options.getDisabledChecks().get(0));
        Assertions.assertEquals("CONVERSION", options.getDisabledChecks().get(1));
        Assertions.assertEquals("SANITYCHECKS", options.getDisabledChecks().get(2));
    }
}


