package com.sprinteins.drupalcli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

public class DrupalClientApplicationTest {
    @Test
    public void test() throws Exception {
        int status = new CommandLine(new DrupalClientApplication()).execute();
        Assertions.assertEquals(0, status);
    }

    @Test
    public void testException() throws Exception {
        String[] args = {"ab", "c", "d"};
        int status = new CommandLine(new DrupalClientApplication()).execute(args);
        Assertions.assertEquals(2, status);
    }

//    @Test
//    public void testMissingEnv() throws Exception {
//        String[] args = {"update", "--api-page=3", "--api-page-directory=api-page"};
//        int status = new CommandLine(new DrupalClientApplication()).execute(args);
//        Assertions.assertEquals(2, status);
//    }
}
