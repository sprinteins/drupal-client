package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprinteins.drupalcli.fields.UriValueModel;
import com.sprinteins.drupalcli.fieldtypes.DateValueModel;
import com.sprinteins.drupalcli.fieldtypes.LongValueModel;
import com.sprinteins.drupalcli.fieldtypes.StringValueModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileUploadModel {

    private List<LongValueModel> fid;
    private List<StringValueModel> uuid;
    private List<StringValueModel> langcode;
    private List<Object> uid;
    private List<StringValueModel> filename;
    private List<UriValueModel> uri;
    private List<StringValueModel> filemime;
    private List<LongValueModel> filesize;
    private List<StringValueModel> status;
    private List<DateValueModel> created;
    private List<DateValueModel> changed;

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
    public LongValueModel getOrCreateFirstFid() {
        if (fid == null) {
            fid = new ArrayList<>();
        }
        if (fid.isEmpty()) {
            fid.add(new LongValueModel());
        }
        return fid.get(0);
    }
    
    @JsonIgnore
    public UriValueModel getOrCreateFirstUri() {
        if (uri == null) {
            uri = new ArrayList<>();
        }
        if (uri.isEmpty()) {
            uri.add(new UriValueModel());
        }
        return uri.get(0);
    }
    
    public List<LongValueModel> getFid() {
        return Optional.ofNullable(fid).map(List::copyOf).orElse(null);
    }

    public void setFid(List<LongValueModel> fid) {
        this.fid = Optional.ofNullable(fid).map(List::copyOf).orElse(null);
    }

    public List<StringValueModel> getUuid() {
        return Optional.ofNullable(uuid).map(List::copyOf).orElse(null);
    }

    public void setUuid(List<StringValueModel> uuid) {
        this.uuid = Optional.ofNullable(uuid).map(List::copyOf).orElse(null);
    }

    public List<StringValueModel> getLangcode() {
        return Optional.ofNullable(langcode).map(List::copyOf).orElse(null);
    }

    public void setLangcode(List<StringValueModel> langcode) {
        this.langcode = Optional.ofNullable(langcode).map(List::copyOf).orElse(null);
    }

    public List<Object> getUid() {
        return Optional.ofNullable(uid).map(List::copyOf).orElse(null);
    }

    public void setUid(List<Object> uid) {
        this.uid = Optional.ofNullable(uid).map(List::copyOf).orElse(null);
    }

    public List<StringValueModel> getFilename() {
        return Optional.ofNullable(filename).map(List::copyOf).orElse(null);
    }

    public void setFilename(List<StringValueModel> filename) {
        this.filename = Optional.ofNullable(filename).map(List::copyOf).orElse(null);
    }

    public List<UriValueModel> getUri() {
        return Optional.ofNullable(uri).map(List::copyOf).orElse(null);
    }

    public void setUri(List<UriValueModel> uri) {
        this.uri = Optional.ofNullable(uri).map(List::copyOf).orElse(null);
    }

    public List<StringValueModel> getFilemime() {
        return Optional.ofNullable(filemime).map(List::copyOf).orElse(null);
    }

    public void setFilemime(List<StringValueModel> filemime) {
        this.filemime = Optional.ofNullable(filemime).map(List::copyOf).orElse(null);
    }

    public List<LongValueModel> getFilesize() {
        return Optional.ofNullable(filesize).map(List::copyOf).orElse(null);
    }

    public void setFilesize(List<LongValueModel> filesize) {
        this.filesize = Optional.ofNullable(filesize).map(List::copyOf).orElse(null);
    }

    public List<StringValueModel> getStatus() {
        return Optional.ofNullable(status).map(List::copyOf).orElse(null);
    }

    public void setStatus(List<StringValueModel> status) {
        this.status = Optional.ofNullable(status).map(List::copyOf).orElse(null);
    }

    public List<DateValueModel> getCreated() {
        return Optional.ofNullable(created).map(List::copyOf).orElse(null);
    }

    public void setCreated(List<DateValueModel> created) {
        this.created = Optional.ofNullable(created).map(List::copyOf).orElse(null);
    }

    public List<DateValueModel> getChanged() {
        return Optional.ofNullable(changed).map(List::copyOf).orElse(null);
    }

    public void setChanged(List<DateValueModel> changed) {
        this.changed = Optional.ofNullable(changed).map(List::copyOf).orElse(null);
    }

    @Override
    public String toString() {
        return "FileUploadModel{" +
                "fid=" + fid +
                ", uuid=" + uuid +
                ", langcode=" + langcode +
                ", uid=" + uid +
                ", filename=" + filename +
                ", uri=" + uri +
                ", filemime=" + filemime +
                ", filesize=" + filesize +
                ", status=" + status +
                ", created=" + created +
                ", changed=" + changed +
                '}';
    }
}
