package com.sprinteins.drupalcli.models;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum ValueFormat {

    GITHUB_FLAVORED_MARKDOWN, BASIC_HTML;

    @JsonValue
    public String toLowerCase() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

}
