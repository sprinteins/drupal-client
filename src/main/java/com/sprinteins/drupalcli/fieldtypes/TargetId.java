package com.sprinteins.drupalcli.fieldtypes;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum TargetId {

    GET_STARTED_ELEMENT, API_REFERENCE, IMAGE, RELEASE_NOTE, ADDITIONAL_INFO_ELEMENT, FAQ_ITEM, FAQ_ITEMS, DOWNLOADS;

    @JsonValue
    public String toLowerCase() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

}
