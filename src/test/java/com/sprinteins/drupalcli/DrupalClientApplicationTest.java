package com.sprinteins.drupalcli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DrupalClientApplicationTest {

    @Test
    public void test() throws Exception {
        int status = DrupalClientApplication.run(new String[0]);
        Assertions.assertEquals(0, status);
    }

    @Test
    public void testException() throws Exception {
        int status = DrupalClientApplication.run("ab c d".split("\\s"));
        Assertions.assertEquals(1, status);
    }
}
