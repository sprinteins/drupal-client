package com.sprinteins.drupalcli.paragraph;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fields.FaqAnswerModel;
import com.sprinteins.drupalcli.fields.FaqQuestionModel;
import com.sprinteins.drupalcli.fieldtypes.TargetId;
import com.sprinteins.drupalcli.fields.TypeModel;

public class FaqItemParagraphModel extends ParagraphModel{
  private List<FaqQuestionModel> question;
  private List<FaqAnswerModel> answer;
  public FaqItemParagraphModel() {
    super(new TypeModel(TargetId.FAQ_ITEM));
    //TODO Auto-generated constructor stub
  }

  @JsonProperty("field_faq_item_question")
  public List<FaqQuestionModel> getQuestion() {
    return Optional.ofNullable(question).map(List::copyOf).orElse(null);
  }

  public void setQuestion(List<FaqQuestionModel> question) {
    this.question = Optional.ofNullable(question).map(List::copyOf).orElse(null);
  }

  @JsonProperty("field_faq_item_answer")
  public List<FaqAnswerModel> getAnswer() {
    return Optional.ofNullable(answer).map(List::copyOf).orElse(null);
  }

  public void setAnswer(List<FaqAnswerModel> answer) {
    this.answer = Optional.ofNullable(answer).map(List::copyOf).orElse(null);
  }

}
