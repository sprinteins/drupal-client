package com.sprinteins.drupalcli.translations;

import java.util.List;
import java.util.Optional;

import com.sprinteins.drupalcli.fieldtypes.StringValueModel;

public class TranslationModel {
  
  private List<StringValueModel> title;

  public TranslationModel() {}

      public List<StringValueModel> getTitle() {
        return Optional.ofNullable(title).map(List::copyOf).orElse(null);
    }

}
