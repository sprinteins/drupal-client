package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.TargetId;
import com.sprinteins.drupalcli.models.TitleModel;
import com.sprinteins.drupalcli.models.TypeModel;

import java.util.List;

public class GetStartedParagraphModel extends ParagraphModel{


    public GetStartedParagraphModel(){
        super(new TypeModel(TargetId.GET_STARTED_ELEMENT));
    }
    
    public GetStartedParagraphModel(String title, DescriptionModel description) {
        this();
        getOrCreateFirstTitle().setValue(title);
        setDescription(List.of(description));
    }

    @Override
    @JsonProperty("field_p_getstarted_title")
    public List<TitleModel> getTitle() {
        return super.getTitle();
    }

    @Override
    @JsonProperty("field_p_getstarted_description")
    public List<DescriptionModel> getDescription() {
        return super.getDescription();
    }
}
