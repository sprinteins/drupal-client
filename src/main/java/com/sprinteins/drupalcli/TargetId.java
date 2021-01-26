package com.sprinteins.drupalcli;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum TargetId {

	GET_STARTED_ELEMENT;

    @JsonValue
    public String toLowerCase() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

}
