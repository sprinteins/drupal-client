package com.sprinteins.drupalcli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FrontMatterReaderTest {
    
    @Test
    void testName() throws Exception {
        String markdown = TestFiles.readAllBytesToString("markdown/markdown-with-frontmatter.markdown");
        var frontmatter = new FrontMatterReader().readFromFile(markdown);
        String string = frontmatter.get("title").get(0);
        assertEquals("Hey there", string);
    }

}
