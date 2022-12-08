package com.sprinteins.drupalcli.models;

import com.sprinteins.drupalcli.fieldtypes.DateValueModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class DateValueModelTest {

    private static Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testDateValidationForCorrectFormat() throws Exception {
        DateValueModel date = new DateValueModel();
        date.setValue("2022-12-14");

        Set<ConstraintViolation<DateValueModel>> constraintViolations =
                validator.validate( date );

        Assertions.assertEquals( 0, constraintViolations.size() );
    }

    @Test
    void testDateValidationForWrongFormat() throws Exception {
        DateValueModel date = new DateValueModel();
        date.setValue("2022-13-14");

        Set<ConstraintViolation<DateValueModel>> constraintViolations =
                validator.validate( date );

        Assertions.assertEquals( 1, constraintViolations.size() );
        Assertions.assertEquals(
                "Value for date is not valid, please use YYYY-MM-DD\n",
                constraintViolations.iterator().next().getMessage()
        );
    }
}
