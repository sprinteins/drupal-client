package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fields.TypeModel;
import com.sprinteins.drupalcli.fieldtypes.DateValueModel;
import com.sprinteins.drupalcli.fieldtypes.FormattedTextModel;
import com.sprinteins.drupalcli.fieldtypes.StringValueModel;
import com.sprinteins.drupalcli.fieldtypes.TargetId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReleaseNoteParagraphModel extends ParagraphModel{
    
    private List<DateValueModel> date;

    public ReleaseNoteParagraphModel(){
        super(new TypeModel(TargetId.RELEASE_NOTE));
    }
    
    @JsonIgnore
    public DateValueModel getOrCreateFirstDate() {
        if (date == null) {
            date = new ArrayList<>();
        }
        if (date.isEmpty()) {
            date.add(new DateValueModel());
        }
        return date.get(0);
    }

    @Override
    @JsonProperty("field_p_release_note_title")
    public List<StringValueModel> getTitle() {
        return super.getTitle();
    }

    @Override
    @JsonProperty("field_p_release_note_description")
    public List<FormattedTextModel> getDescription() {
        return super.getDescription();
    }

    @JsonProperty("field_p_release_note_date")
    public List<DateValueModel> getDate() {
        return Optional.ofNullable(date).map(List::copyOf).orElse(null);
    }

    public void setDate(List<DateValueModel> date) {
        this.date = Optional.ofNullable(date).map(List::copyOf).orElse(null);
    }

}
