package com.sprinteins.drupalcli.models;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum TargetId {

    GET_STARTED_ELEMENT, API_REFERENCE;

    @JsonValue
    public String toLowerCase() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

}
