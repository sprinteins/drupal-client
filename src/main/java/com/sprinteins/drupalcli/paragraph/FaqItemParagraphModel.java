package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fields.FaqAnswerModel;
import com.sprinteins.drupalcli.fields.FaqQuestionModel;
import com.sprinteins.drupalcli.fields.TypeModel;
import com.sprinteins.drupalcli.fieldtypes.TargetId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FaqItemParagraphModel extends ParagraphModel{
  private List<FaqQuestionModel> question;
  private List<FaqAnswerModel> answer;

  public FaqItemParagraphModel() {
    super(new TypeModel(TargetId.FAQ_ITEM));
  }

  public static FaqItemParagraphModel create(Long id) {
    FaqItemParagraphModel model = new FaqItemParagraphModel();
    model.getOrCreateFirstId().setValue(id);
    return model;
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

  @JsonIgnore
  public static FaqItemParagraphModel question(String question) {
      FaqItemParagraphModel model = new FaqItemParagraphModel();
      model.getOrCreateFirstQuestion().setValue(question);
      return model;
  }

  @JsonIgnore
  public FaqQuestionModel getOrCreateFirstQuestion() {
      if (question == null) {
          question = new ArrayList<>();
      }
      if (question.isEmpty()) {
          question.add(new FaqQuestionModel());
      }
      return question.get(0);
  }

  @JsonIgnore
  public static FaqItemParagraphModel answer(String answer, FaqItemParagraphModel... paragraphModel) {
      FaqItemParagraphModel model = new FaqItemParagraphModel();
      if (paragraphModel.length != 0 && paragraphModel[0] != null){
          model = paragraphModel[0];
      }
      model.getOrCreateFirstAnswer().setValue(answer);
      return model;
  }

  @JsonIgnore
  public FaqAnswerModel getOrCreateFirstAnswer() {
      if (answer == null) {
        answer = new ArrayList<>();
      }
      if (answer.isEmpty()) {
        answer.add(new FaqAnswerModel());
      }
      return answer.get(0);
  }

}
