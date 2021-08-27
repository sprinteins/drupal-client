package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprinteins.drupalcli.models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ParagraphModel {
    private List<LongValueModel> id;
    private List<LongValueModel> revisionId;
    private List<StringValueModel> uuid;
    private List<DescriptionModel> description;
    private List<TitleModel> title;
    private List<TypeModel> type;
    private List<DateValueModel> date;

    public ParagraphModel(TypeModel type){
        this.type = Collections.singletonList(type);
    }

    @JsonIgnore
    public LongValueModel getOrCreateFirstId() {
        if (id == null) {
            id = new ArrayList<>();
        }
        if (id.isEmpty()) {
            id.add(new LongValueModel());
        }
        return id.get(0);
    }

    @JsonIgnore
    public LongValueModel getOrCreateFirstRevisionId() {
        if (revisionId == null) {
            revisionId = new ArrayList<>();
        }
        if (revisionId.isEmpty()) {
            revisionId.add(new LongValueModel());
        }
        return revisionId.get(0);
    }

    @JsonIgnore
    public StringValueModel getOrCreateFirstUuid() {
        if (uuid == null) {
            uuid = new ArrayList<>();
        }
        if (uuid.isEmpty()) {
            uuid.add(new StringValueModel());
        }
        return uuid.get(0);
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

    public List<LongValueModel> getId() {
        return Optional.ofNullable(id).map(List::copyOf).orElse(null);
    }

    public void setId(List<LongValueModel> id) {
        this.id = Optional.ofNullable(id).map(List::copyOf).orElse(null);
    }

    public List<LongValueModel> getRevisionId() {
        return Optional.ofNullable(revisionId).map(List::copyOf).orElse(null);
    }

    public void setRevisionId(List<LongValueModel> revisionId) {
        this.revisionId = Optional.ofNullable(revisionId).map(List::copyOf).orElse(null);
    }

    public List<StringValueModel> getUuid() {
        return Optional.ofNullable(uuid).map(List::copyOf).orElse(null);
    }

    public void setUuid(List<StringValueModel> uuid) {
        this.uuid = Optional.ofNullable(uuid).map(List::copyOf).orElse(null);
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

    public List<DateValueModel> getDate() {
        return Optional.ofNullable(date).map(List::copyOf).orElse(null);
    }

    public void setDate(List<DateValueModel> date) {
        this.date = Optional.ofNullable(date).map(List::copyOf).orElse(null);
    }

}
