package com.sprinteins.drupalcli.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprinteins.drupalcli.models.TargetId;
import com.sprinteins.drupalcli.models.TypeModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

}
