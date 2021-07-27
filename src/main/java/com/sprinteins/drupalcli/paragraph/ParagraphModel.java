package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.models.DescriptionModel;
import com.sprinteins.drupalcli.models.TargetId;
import com.sprinteins.drupalcli.models.TitleModel;
import com.sprinteins.drupalcli.models.TypeModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ParagraphModel {
    private List<DescriptionModel> description;
    private List<TitleModel> title;
    private List<TypeModel> type;

    public ParagraphModel(TypeModel type){
        this.type = Collections.singletonList(type);
    }

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


    public List<DescriptionModel> getDescription() {
        return Optional.ofNullable(description).map(List::copyOf).orElse(null);
    }

    public void setDescription(List<DescriptionModel> description) {
        this.description = Optional.ofNullable(description).map(List::copyOf).orElse(null);
    }

    public List<TitleModel> getTitle() {
        return Optional.ofNullable(title).map(List::copyOf).orElse(null);
    }

    public void setTitle(List<TitleModel> title) {
        this.title = Optional.ofNullable(title).map(List::copyOf).orElse(null);
    }

    public List<TypeModel> getType() {
        return Optional.ofNullable(type).map(List::copyOf).orElse(null);
    }

    public void setType(List<TypeModel> type) {
        this.type = Optional.ofNullable(type).map(List::copyOf).orElse(null);
    }

}
