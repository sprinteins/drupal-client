package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.models.*;

import java.util.List;

public class ReleaseNoteParagraphModel extends ParagraphModel{


    public ReleaseNoteParagraphModel(){
        super(new TypeModel(TargetId.RELEASE_NOTE));
    }

    @Override
    @JsonProperty("field_p_release_note_title")
    public List<TitleModel> getTitle() {
        return super.getTitle();
    }

    @Override
    @JsonProperty("field_p_release_note_description")
    public List<DescriptionModel> getDescription() {
        return super.getDescription();
    }

    @Override
    @JsonProperty("field_p_release_note_date")
    public List<DateValueModel> getDate() {
        return super.getDate();
    }

}
