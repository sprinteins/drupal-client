package com.sprinteins.drupalcli.fieldtypes;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum TextFormat {

    GITHUB_FLAVORED_MARKDOWN, BASIC_HTML, FULL_HTML;

    @JsonValue
    public String toLowerCase() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

}
