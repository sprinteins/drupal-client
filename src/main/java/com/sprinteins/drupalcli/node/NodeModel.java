package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NodeModel {

    private List<TypeModel> type = Collections
            .singletonList(new TypeModel(TargetId.API_REFERENCE));
    private List<GetStartedDocsElementModel> getStartedDocsElement;
    private List<SourceFileModel> sourceFile;
    private List<LongValueModel> nid;
    private List<StringValueModel> uuid;
    private List<LongValueModel> vid;
    private List<StringValueModel> langcode;
    private List<DateValueModel> revisionTimestamp;
    private List<UidValueModel> revisionUid;


    @JsonIgnore
    public SourceFileModel getOrCreateFirstSourceFile() {
        if (sourceFile == null) {
            sourceFile = new ArrayList<>();
        }
        if (sourceFile.isEmpty()) {
            sourceFile.add(new SourceFileModel());
        }
        return sourceFile.get(0);
    }

    public List<TypeModel> getType() {
        return Optional.ofNullable(type).map(List::copyOf).orElse(null);
    }

    public void setType(List<TypeModel> type) {
        this.type = Optional.ofNullable(type).map(List::copyOf).orElse(null);
    }

    @JsonProperty("field_source_file")
    public List<SourceFileModel> getSourceFile() {
        return Optional.ofNullable(sourceFile).map(List::copyOf).orElse(null);
    }
    public void setSourceFile(List<SourceFileModel> sourceFile) {
        this.sourceFile = Optional.ofNullable(sourceFile).map(List::copyOf).orElse(null);
    }

    @JsonProperty("nid")
    public List<LongValueModel> getNid() {
        return Optional.ofNullable(nid).map(List::copyOf).orElse(null);
    }
    public void setNid(List<LongValueModel> nid) {
        this.nid = Optional.ofNullable(nid).map(List::copyOf).orElse(null);
    }

    @JsonProperty("uuid")
    public List<StringValueModel> getUuid() {
        return Optional.ofNullable(uuid).map(List::copyOf).orElse(null);
    }
    public void setUuid(List<StringValueModel> uuid) {
        this.uuid = Optional.ofNullable(uuid).map(List::copyOf).orElse(null);
    }

    @JsonProperty("vid")
    public List<LongValueModel> getVid() {
        return Optional.ofNullable(vid).map(List::copyOf).orElse(null);
    }
    public void setVid(List<LongValueModel> vid) {
        this.vid = Optional.ofNullable(vid).map(List::copyOf).orElse(null);
    }

    @JsonProperty("langcode")
    public List<StringValueModel> getLangcode() {
        return Optional.ofNullable(langcode).map(List::copyOf).orElse(null);
    }
    public void setLangcode(List<StringValueModel> langcode) {
        this.langcode = Optional.ofNullable(langcode).map(List::copyOf).orElse(null);
    }

    @JsonProperty("revision_timestamp")
    public List<DateValueModel> getRevisionTimestamp() {
        return Optional.ofNullable(revisionTimestamp).map(List::copyOf).orElse(null);
    }
    public void setRevisionTimestamp(List<DateValueModel> revisionTimestamp) {
        this.revisionTimestamp = Optional.ofNullable(revisionTimestamp).map(List::copyOf).orElse(null);
    }

    @JsonProperty("revision_uid")
    public List<UidValueModel> getRevisionUid() {
        return Optional.ofNullable(revisionUid).map(List::copyOf).orElse(null);
    }
    public void setRevisionUid(List<UidValueModel> revisionUid) {
        this.revisionUid = Optional.ofNullable(revisionUid).map(List::copyOf).orElse(null);
    }

    @JsonProperty("field_get_started_docs_elements")
    public List<GetStartedDocsElementModel> getGetStartedDocsElement() {
        return Optional.ofNullable(getStartedDocsElement).map(List::copyOf).orElse(null);
    }
    public void setGetStartedDocsElement(List<GetStartedDocsElementModel> getStartedDocsElement) {
        this.getStartedDocsElement = Optional.ofNullable(getStartedDocsElement).map(List::copyOf).orElse(null);
    }


}
