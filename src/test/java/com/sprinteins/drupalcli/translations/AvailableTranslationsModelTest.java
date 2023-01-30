package com.sprinteins.drupalcli.translations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

public class AvailableTranslationsModelTest {

    @Test
    void testAvailableTranslationsModelValidateFunction() throws Exception {
        var list = new ArrayList<TranslationModel>();
        list.add(new TranslationModel("de"));
        list.add(new TranslationModel("en"));

				var model = new AvailableTranslationsModel(list);
				var actualTrue = model.validate("de");

        assertEquals(true, actualTrue);

				actualTrue = model.validate("en");

        assertEquals(true, actualTrue);

				var actualFalse = model.validate("GER");

        assertEquals(false, actualFalse);
    }

    @Test
    void testAvailableTranslationsModelPrintFunction() throws Exception {
        var list = new ArrayList<TranslationModel>();
        list.add(new TranslationModel("de"));
        list.add(new TranslationModel("en"));

				var model = new AvailableTranslationsModel(list);
				var actualString = model.printValues();

        assertEquals("- de\n- en\n", actualString);
    }

   
}
