package com.sprinteins.drupalcli.paragraph;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.models.FaqAnswerModel;
import com.sprinteins.drupalcli.models.FaqQuestionModel;
import com.sprinteins.drupalcli.models.TargetId;
import com.sprinteins.drupalcli.models.TypeModel;

public class FaqItemParagraphModel extends ParagraphModel{

  public FaqItemParagraphModel() {
    super(new TypeModel(TargetId.FAQ));
    //TODO Auto-generated constructor stub
  }

  @JsonProperty("field_faq_item_question")
  public List<FaqQuestionModel>getQuestion() {
    return super.getQuestion();
  }
  

  @JsonProperty("field_faq_item_answer")
  public List<FaqAnswerModel>getAnswer() {
    return super.getAnswer();
  }
}
