package com.sprinteins.drupalcli.translations;

public class LanguageSelector {
  public String getLangCode(String languageString) {
    var searchTerm = "hreflang=\"";
    var positionStart = languageString.indexOf(searchTerm)+ searchTerm.length();
    var positionEnd = languageString.indexOf("\"",positionStart);

    return languageString.substring(positionStart,positionEnd);
  }
}
