package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fields.TypeModel;
import com.sprinteins.drupalcli.fieldtypes.FormattedTextModel;
import com.sprinteins.drupalcli.fieldtypes.StringValueModel;
import com.sprinteins.drupalcli.fieldtypes.TargetId;

import java.util.List;

public class GetStartedParagraphModel extends ParagraphModel{

    public static GetStartedParagraphModel create(String title, FormattedTextModel description) {
        GetStartedParagraphModel model = new GetStartedParagraphModel();
        model.getOrCreateFirstTitle().setValue(title);
        model.setDescription(List.of(description));
        return model;
    }

    public GetStartedParagraphModel(){
        super(new TypeModel(TargetId.GET_STARTED_ELEMENT));
    }
    
    @Override
    @JsonProperty("field_p_getstarted_title")
    public List<StringValueModel> getTitle() {
        return super.getTitle();
    }

    @Override
    @JsonProperty("field_p_getstarted_description")
    public List<FormattedTextModel> getDescription() {
        return super.getDescription();
    }
}
