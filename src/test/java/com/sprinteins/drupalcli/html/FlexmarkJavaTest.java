package com.sprinteins.drupalcli.html;

import com.sprinteins.drupalcli.TestFiles;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.junit.jupiter.api.Test;

public class FlexmarkJavaTest {
    
    @Test
    void testName() throws Exception {
        String html = TestFiles.readAllBytesToString("html/overview.html");
        String markdown = FlexmarkHtmlConverter.builder().build().convert(html);
        System.out.println(markdown);
    }

}
