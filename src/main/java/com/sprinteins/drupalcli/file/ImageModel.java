package com.sprinteins.drupalcli.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.models.*;

import java.util.List;
import java.util.Optional;

public class ImageModel {

    private LinksModel links;
    private List<UriValueModel> uri;
    private StringValueModel filename;
    private StringValueModel filemime;
    private TypeModel type = new TypeModel(TargetId.IMAGE);
    private List<StringValueModel> data;

    @JsonProperty("_links")
    public LinksModel getLinks() { return links; }
    public void setLinks(LinksModel links) { this.links = links; }
    
    public List<StringValueModel> getData() { return Optional.ofNullable(data).map(List::copyOf).orElse(null); }
    public void setData(List<StringValueModel> data) { this.data = Optional.ofNullable(data).map(List::copyOf).orElse(null); }

    public List<UriValueModel> getUri() { return Optional.ofNullable(uri).map(List::copyOf).orElse(null); }
    public void setUri(List<UriValueModel> uri) { this.uri = Optional.ofNullable(uri).map(List::copyOf).orElse(null); }

    public StringValueModel getFilename() { return filename; }
    public void setFilename(StringValueModel filename) { this.filename = filename; }

    public StringValueModel getFilemime() { return filemime; }
    public void setFilemime(StringValueModel filemime) { this.filemime = filemime; }

    public TypeModel getType() { return type; }
    public void setType(TypeModel type) { this.type = type; }
}