package com.sprinteins.drupalcli.file;

import com.sprinteins.drupalcli.models.*;

import java.util.List;

public class ImageModel {

    private LinksModel _links;
    private List<UriValueModel> uri;
    private StringValueModel filename;
    private StringValueModel filemime;
    private TypeModel type = new TypeModel(TargetId.IMAGE);
    private List<StringValueModel> data;


    public LinksModel get_links() { return _links; }
    public void setLinks(LinksModel links) { this._links = links; }

    public List<StringValueModel> getData() { return data; }
    public void setData(List<StringValueModel> data) { this.data = data; }

    public List<UriValueModel> getUri() { return uri; }
    public void setUri(List<UriValueModel> uri) { this.uri = uri; }

    public StringValueModel getFilename() { return filename; }
    public void setFilename(StringValueModel filename) { this.filename = filename; }

    public StringValueModel getFilemime() { return filemime; }
    public void setFilemime(StringValueModel filemime) { this.filemime = filemime; }

    public TypeModel getType() { return type; }
    public void setType(TypeModel type) { this.type = type; }
}