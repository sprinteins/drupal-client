package com.sprinteins.drupalcli.file;

import com.sprinteins.drupalcli.models.DateValueModel;
import com.sprinteins.drupalcli.models.LongValueModel;
import com.sprinteins.drupalcli.models.StringValueModel;
import com.sprinteins.drupalcli.models.UriValueModel;

import java.util.List;

public class ApiReferenceFileModel {

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

    public List<LongValueModel> getFid() {
        return fid;
    }

    public void setFid(List<LongValueModel> fid) {
        this.fid = fid;
    }

    public List<StringValueModel> getUuid() {
        return uuid;
    }

    public void setUuid(List<StringValueModel> uuid) {
        this.uuid = uuid;
    }

    public List<StringValueModel> getLangcode() {
        return langcode;
    }

    public void setLangcode(List<StringValueModel> langcode) {
        this.langcode = langcode;
    }

    public List<Object> getUid() {
        return uid;
    }

    public void setUid(List<Object> uid) {
        this.uid = uid;
    }

    public List<StringValueModel> getFilename() {
        return filename;
    }

    public void setFilename(List<StringValueModel> filename) {
        this.filename = filename;
    }

    public List<UriValueModel> getUri() {
        return uri;
    }

    public void setUri(List<UriValueModel> uri) {
        this.uri = uri;
    }

    public List<StringValueModel> getFilemime() {
        return filemime;
    }

    public void setFilemime(List<StringValueModel> filemime) {
        this.filemime = filemime;
    }

    public List<LongValueModel> getFilesize() {
        return filesize;
    }

    public void setFilesize(List<LongValueModel> filesize) {
        this.filesize = filesize;
    }

    public List<StringValueModel> getStatus() {
        return status;
    }

    public void setStatus(List<StringValueModel> status) {
        this.status = status;
    }

    public List<DateValueModel> getCreated() {
        return created;
    }

    public void setCreated(List<DateValueModel> created) {
        this.created = created;
    }

    public List<DateValueModel> getChanged() {
        return changed;
    }

    public void setChanged(List<DateValueModel> changed) {
        this.changed = changed;
    }

    @Override
    public String toString() {
        return "ApiReferenceFileModel{" +
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
