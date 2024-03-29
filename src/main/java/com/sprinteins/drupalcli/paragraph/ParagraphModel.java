package com.sprinteins.drupalcli.paragraph;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fields.TypeModel;
import com.sprinteins.drupalcli.fieldtypes.FormattedTextModel;
import com.sprinteins.drupalcli.fieldtypes.LongValueModel;
import com.sprinteins.drupalcli.fieldtypes.StringValueModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ParagraphModel {
    private List<LongValueModel> id;
    private List<LongValueModel> revisionId;
    private List<StringValueModel> uuid;
    private List<FormattedTextModel> description;
    private List<StringValueModel> title;
    private List<TypeModel> type;


    public ParagraphModel(TypeModel type){
        this.type = Collections.singletonList(type);
    }
    
    @JsonIgnore
    public long id() {
        return getOrCreateFirstId().getValue();
    }
    
    @JsonIgnore
    public String title() {
        return getOrCreateFirstTitle().getValue();
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
    public FormattedTextModel getOrCreateFirstDescription() {
        if (description == null) {
            description = new ArrayList<>();
        }
        if (description.isEmpty()) {
            description.add(new FormattedTextModel());
        }
        return description.get(0);
    }

    @JsonIgnore
    public StringValueModel getOrCreateFirstTitle() {
        if (title == null) {
            title = new ArrayList<>();
        }
        if (title.isEmpty()) {
            title.add(new StringValueModel());
        }
        return title.get(0);
    }

    public List<LongValueModel> getId() {
        return Optional.ofNullable(id).map(List::copyOf).orElse(null);
    }

    public void setId(List<LongValueModel> id) {
        this.id = Optional.ofNullable(id).map(List::copyOf).orElse(null);
    }

    @JsonProperty("revision_id")
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


    public List<FormattedTextModel> getDescription() {
        return Optional.ofNullable(description).map(List::copyOf).orElse(null);
    }

    public void setDescription(List<FormattedTextModel> description) {
        this.description = Optional.ofNullable(description).map(List::copyOf).orElse(null);
    }

    public List<StringValueModel> getTitle() {
        return Optional.ofNullable(title).map(List::copyOf).orElse(null);
    }

    public void setTitle(List<StringValueModel> title) {
        this.title = Optional.ofNullable(title).map(List::copyOf).orElse(null);
    }

    public List<TypeModel> getType() {
        return Optional.ofNullable(type).map(List::copyOf).orElse(null);
    }

    public void setType(List<TypeModel> type) {
        this.type = Optional.ofNullable(type).map(List::copyOf).orElse(null);
    }

}
