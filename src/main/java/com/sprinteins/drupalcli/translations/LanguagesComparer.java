package com.sprinteins.drupalcli.translations;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LanguagesComparer {

    private final List<TranslationModel> availableTranslations;
    private final Path directory;
    public LanguagesComparer(List<TranslationModel> translationModels, Path directoryPath) {
        availableTranslations = translationModels;
        directory = directoryPath;
    }

    public LanguageComparisonResult compareLanguages() {
        boolean isLanguageSetsMatch = compareLanguageSets();
        if (!isLanguageSetsMatch){
            return new LanguageComparisonResult(false, getMissingLanguages());
        }
        return new LanguageComparisonResult(true);
    }
    private ArrayList<String> getTranslationFolders() {
        ArrayList<String> localLangDirectories = new ArrayList<>();
        File rootDirectory = new File(directory.toString());
        String[] translationsDirectories = rootDirectory.list((current, name) -> new File(current, name).isDirectory());

        assert translationsDirectories != null;
        Collections.addAll(localLangDirectories, translationsDirectories);
        return localLangDirectories;
    }
    private boolean compareLanguageSets() {
        ArrayList<String> localDirectories = getTranslationFolders();
        for(TranslationModel translation:availableTranslations) {
            if(!localDirectories.contains(translation.getLangcode())) {
                return false;
            }
        }
        return true;
    }
    private String getMissingLanguages() {
        ArrayList<String> localDirectories = getTranslationFolders();
        ArrayList<String> missingLanguagesArray = new ArrayList<>();

        for(TranslationModel translation:availableTranslations) {
            if(!localDirectories.contains(translation.getLangcode())) {
                missingLanguagesArray.add(translation.getLangcode());
            }
        }

        StringBuilder missingLanguages = new StringBuilder();
        for(String lang:missingLanguagesArray) {
            missingLanguages.append(lang).append(", ");
        }
        missingLanguages = new StringBuilder(missingLanguages.substring(0, missingLanguages.length() - 2));

        return missingLanguages.toString();
    }
}
