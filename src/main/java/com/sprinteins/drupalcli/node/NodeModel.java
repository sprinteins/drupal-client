package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.fields.*;
import com.sprinteins.drupalcli.fieldtypes.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NodeModel {

    private List<TypeModel> type = Collections
            .singletonList(new TypeModel(TargetId.API_REFERENCE));
    private List<GetStartedDocsElementModel> getStartedDocsElements;
    private List<AdditionalInformationElementModel> additionalInformationElements;
    private List<FaqItemsModel> faqItems;
    private List<ReleaseNoteElementModel> releaseNotesElement;
    private List<SourceFileModel> sourceFile;
    private List<DownloadsModel> downloadElements;
    private List<LongValueModel> nid;
    private List<StringValueModel> uuid;
    private List<LongValueModel> vid;
    private List<StringValueModel> langcode;
    private List<DateValueModel> revisionTimestamp;
    private List<UidTargetModel> revisionUid;
    private List<FormattedTextModel> listDescription;
    private List<StringValueModel> displayTitle;
    private List<StringValueModel> version;


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

    @JsonIgnore
    public DownloadsModel getOrCreateFirstDownloadElement() {
        if (downloadElements == null) {
            downloadElements = new ArrayList<>();
        }
        if (downloadElements.isEmpty()) {
            downloadElements.add(new DownloadsModel());
        }
        return downloadElements.get(0);
    }

    @JsonIgnore
    public FormattedTextModel getOrCreateFirstListDescription() {
        if (listDescription == null) {
            listDescription = new ArrayList<>();
        }
        if (listDescription.isEmpty()) {
            listDescription.add(new FormattedTextModel());
        }
        return listDescription.get(0);
    }

    @JsonIgnore
    public StringValueModel getOrCreateFirstDisplayTitle() {
        if (displayTitle == null) {
            displayTitle = new ArrayList<>();
        }
        if (displayTitle.isEmpty()) {
            displayTitle.add(new StringValueModel());
        }
        return displayTitle.get(0);
    }

    @JsonIgnore
    public LongValueModel getOrCreateFirstNid() {
        if (nid == null) {
            nid = new ArrayList<>();
        }
        if (nid.isEmpty()) {
            nid.add(new LongValueModel());
        }
        return nid.get(0);
    }

    @JsonIgnore
    public ReleaseNoteElementModel getOrCreateFirstReleaseNotesElement() {
        if (releaseNotesElement == null) {
            releaseNotesElement = new ArrayList<>();
        }
        if (releaseNotesElement.isEmpty()) {
            releaseNotesElement.add(new ReleaseNoteElementModel());
        }
        return releaseNotesElement.get(0);
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

    @JsonProperty("field_downloads")
    public List<DownloadsModel> getDownloadElements() {
        return Optional.ofNullable(downloadElements).map(List::copyOf).orElse(null);
    }
    public void setDownloadFile(List<SourceFileModel> downloadFile) {
        this.downloadElements = Optional.ofNullable(downloadElements).map(List::copyOf).orElse(null);
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
    public List<UidTargetModel> getRevisionUid() {
        return Optional.ofNullable(revisionUid).map(List::copyOf).orElse(null);
    }
    public void setRevisionUid(List<UidTargetModel> revisionUid) {
        this.revisionUid = Optional.ofNullable(revisionUid).map(List::copyOf).orElse(null);
    }

    @JsonProperty("field_get_started_docs_elements")
    public List<GetStartedDocsElementModel> getGetStartedDocsElements() {
        return Optional.ofNullable(getStartedDocsElements).map(List::copyOf).orElse(null);
    }
    public void setGetStartedDocsElements(List<GetStartedDocsElementModel> getStartedDocsElements) {
        this.getStartedDocsElements = Optional.ofNullable(getStartedDocsElements).map(List::copyOf).orElse(null);
    }

    @JsonProperty("field_additional_info_elements")
    public List<AdditionalInformationElementModel> getAdditionalInformationElements() {
        return Optional.ofNullable(additionalInformationElements).map(List::copyOf).orElse(null);
    }
    public void setAdditionalInformationElementsElements(List<AdditionalInformationElementModel> additionalInformationElements) {
        this.additionalInformationElements = Optional.ofNullable(additionalInformationElements).map(List::copyOf).orElse(null);
    }

    @JsonProperty("field_list_description")
    public List<FormattedTextModel> getListDescription() {
        return Optional.ofNullable(listDescription).map(List::copyOf).orElse(null);
    }

    public void setListDescription(List<FormattedTextModel> description) {
        this.listDescription = Optional.ofNullable(description).map(List::copyOf).orElse(null);
    }

    @JsonProperty("field_display_title")
    public List<StringValueModel> getDisplayTitle() {
        return Optional.ofNullable(displayTitle).map(List::copyOf).orElse(null);
    }

    public void setDisplayTitle(List<StringValueModel> title) {
        this.displayTitle = Optional.ofNullable(title).map(List::copyOf).orElse(null);
    }

    @JsonProperty("field_release_note_elements")
    public List<ReleaseNoteElementModel> getReleaseNotesElement() {
        return Optional.ofNullable(releaseNotesElement).map(List::copyOf).orElse(null);
    }
    public void setReleaseNotesElement(List<ReleaseNoteElementModel> releaseNotesElement) {
        this.releaseNotesElement = Optional.ofNullable(releaseNotesElement).map(List::copyOf).orElse(null);
    }

    @JsonProperty("field_content_paragraphs")
    public List<FaqItemsModel> getFaqItems() {
        return Optional.ofNullable(faqItems).map(List::copyOf).orElse(null);
    }
    public void setFaqItems(List<FaqItemsModel> faqItems) {
        this.faqItems = Optional.ofNullable(faqItems).map(List::copyOf).orElse(null);
    }

    @JsonProperty("field_version")
    public List<StringValueModel> getVersion() {
        return Optional.ofNullable(version).map(List::copyOf).orElse(null);
    }

    public void setVersion(List<StringValueModel> version) {
        this.version = Optional.ofNullable(version).map(List::copyOf).orElse(null);
    }

    @Override
    public String toString() {
        return "NodeModel{type='" + type + "', sourceFile='" + sourceFile + "}";
    }

}
