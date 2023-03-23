package com.sprinteins.drupalcli.translations;

import java.util.ArrayList;
import java.util.List;

public class AvailableTranslationsModel {

  private List<TranslationModel> translationsList = new ArrayList<TranslationModel>();

  public AvailableTranslationsModel(List<TranslationModel> translations) {
    this.translationsList = translations;
  }

  public boolean validate(String langcodeInput){
    for(TranslationModel translation: translationsList) {
      if(translation.getLangcode().equals(langcodeInput)) return true;
    }
    return false;
  } 

  public String printValues(){
    StringBuilder availableLangcodes = new StringBuilder();
    for(TranslationModel translation: translationsList) {
      availableLangcodes.append("- ").append(translation.getLangcode()).append("\n");
    }
    return availableLangcodes.toString();
  } 
}
