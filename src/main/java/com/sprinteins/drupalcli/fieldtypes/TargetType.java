package com.sprinteins.drupalcli.fieldtypes;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum TargetType {

    NODE_TYPE, USER, TAXONOMY_TERM, PARAGRAPH, USER_ROLE, FILE, PARAGRAPHS_TYPE;

    @JsonValue
    public String toLowerCase() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

}
