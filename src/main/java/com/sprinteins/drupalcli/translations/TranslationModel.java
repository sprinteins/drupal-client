package com.sprinteins.drupalcli.translations;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TranslationModel {
  
  private String langcode;

  public TranslationModel() {}

  @JsonProperty("langcode")
  public String getLangcode() {
    return langcode;
  }

  @JsonProperty("langcode")
  public void setLangcode(String langcode) {
    this.langcode = langcode;
  }

}
