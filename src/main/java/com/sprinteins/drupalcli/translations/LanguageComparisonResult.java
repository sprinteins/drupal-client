package com.sprinteins.drupalcli.translations;

import java.util.Objects;

public final class LanguageComparisonResult {
    private final boolean result;
    private final String missingTranslations;

    LanguageComparisonResult(boolean result, String missingTranslations) {
        this.result = result;
        this.missingTranslations = missingTranslations;
    }

    public boolean result() {
        return result;
    }

    public String missingTranslations() {
        return missingTranslations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (LanguageComparisonResult) obj;
        return this.result == that.result &&
                Objects.equals(this.missingTranslations, that.missingTranslations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, missingTranslations);
    }

    @Override
    public String toString() {
        return "LanguageComparisonRecord[" +
                "result=" + result + ", " +
                "missingTranslations=" + missingTranslations + ']';
    }

    public LanguageComparisonResult(boolean result) {
        this(result, "");
    }
}
