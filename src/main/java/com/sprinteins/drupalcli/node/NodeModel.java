package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.TargetId;
import com.sprinteins.drupalcli.TypeModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NodeModel {

    private List<TypeModel> type = Collections
            .singletonList(new TypeModel(TargetId.API_REFERENCE));
    private List<SourceFileModel> sourceFile;

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
        return type;
    }

    public void setType(List<TypeModel> type) {
        this.type = type;
    }

    @JsonProperty("field_source_file")
    public List<SourceFileModel> getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(List<SourceFileModel> sourceFile) {
        this.sourceFile = sourceFile;
    }

}
