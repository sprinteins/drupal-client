package com.sprinteins.drupalcli.getstartedparagraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.TargetId;
import com.sprinteins.drupalcli.models.TitleModel;
import com.sprinteins.drupalcli.models.TypeModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class GetStartedParagraphModel {

    private List<TypeModel> type = Collections
            .singletonList(new TypeModel(TargetId.GET_STARTED_ELEMENT));
    private List<DescriptionModel> description;
    private List<TitleModel> title;

    @JsonIgnore
    public DescriptionModel getOrCreateFirstDescription() {
        if (description == null) {
            description = new ArrayList<>();
        }
        if (description.isEmpty()) {
            description.add(new DescriptionModel());
        }
        return description.get(0);
    }

    @JsonIgnore
    public TitleModel getOrCreateFirstTitle() {
        if (title == null) {
            title = new ArrayList<>();
        }
        if (title.isEmpty()) {
            title.add(new TitleModel());
        }
        return title.get(0);
    }

    public List<TypeModel> getType() {
        return type;
    }

    public void setType(List<TypeModel> type) {
        this.type = type;
    }

    @JsonProperty("field_p_getstarted_description")
    public List<DescriptionModel> getDescription() {
        return description;
    }

    public void setDescription(List<DescriptionModel> description) {
        this.description = description;
    }

    @JsonProperty("field_p_getstarted_title")
    public List<TitleModel> getTitle() {
        return title;
    }

    public void setTitle(List<TitleModel> title) {
        this.title = title;
    }

}
