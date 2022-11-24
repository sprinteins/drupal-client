package com.sprinteins.drupalcli.paragraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.models.FaqItemModel;
import com.sprinteins.drupalcli.models.TargetId;
import com.sprinteins.drupalcli.models.TitleModel;
import com.sprinteins.drupalcli.models.TypeModel;

public class FaqItemsParagraphModel extends ParagraphModel {

  private List<FaqItemModel> faqItem;

  public static FaqItemsParagraphModel create(String title) {
    FaqItemsParagraphModel model = new FaqItemsParagraphModel();
    model.getOrCreateFirstTitle().setValue(title);
    return model;
  }

  public FaqItemsParagraphModel() {
    super(new TypeModel(TargetId.FAQ));
  }

  @JsonProperty("field_faq_item")
  public List<FaqItemModel> getFaqItem() {
      return Optional.ofNullable(faqItem).map(List::copyOf).orElse(null);
  }
  public void setFaqItem(List<FaqItemModel> faqItem) {
      this.faqItem = Optional.ofNullable(faqItem).map(List::copyOf).orElse(null);
  } 

  @Override
  @JsonProperty("field_faq_title")
  public List<TitleModel>getTitle() {
    return super.getTitle();
  }

}
