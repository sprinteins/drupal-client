package com.sprinteins.drupalcli.paragraph;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.models.TargetId;
import com.sprinteins.drupalcli.models.TitleModel;
import com.sprinteins.drupalcli.models.TypeModel;

public class FaqItemsParagraphModel extends ParagraphModel {

  public static FaqItemsParagraphModel create(String title) {
    FaqItemsParagraphModel model = new FaqItemsParagraphModel();
    model.getOrCreateFirstTitle().setValue(title);
    return model;
  }

  public FaqItemsParagraphModel() {
    super(new TypeModel(TargetId.FAQ));
  }

  @Override
  @JsonProperty("field_faq_title")
  public List<TitleModel>getTitle() {
    return super.getTitle();
  }

}
