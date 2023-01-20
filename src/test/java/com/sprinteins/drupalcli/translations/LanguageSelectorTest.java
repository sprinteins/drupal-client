package com.sprinteins.drupalcli.translations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LanguageSelectorTest {

  @Test
  void testLangCode() throws Exception {
    String languageString = "<a href=\"/api-reference/test-page\" hreflang=\"de\">DHL Pizza Ordering API</a>";
    String expected = "de";
    var langSelector = new LanguageSelector();
    var actual = langSelector.getLangCode(languageString);
    Assertions.assertEquals(expected, actual);
  }
}
