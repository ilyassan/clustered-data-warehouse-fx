package com.ilyassan.clustereddatawarehousefx.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CurrencyValidatorTest {

    private CurrencyValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new CurrencyValidator();
    }

    @Test
    void isValid_ValidCurrencyCode_USD() {
        assertTrue(validator.isValid("USD", context));
    }

    @Test
    void isValid_ValidCurrencyCode_EUR() {
        assertTrue(validator.isValid("EUR", context));
    }

    @Test
    void isValid_ValidCurrencyCode_GBP() {
        assertTrue(validator.isValid("GBP", context));
    }

    @Test
    void isValid_ValidCurrencyCode_JPY() {
        assertTrue(validator.isValid("JPY", context));
    }

    @Test
    void isValid_ValidCurrencyCode_Lowercase() {
        assertTrue(validator.isValid("usd", context));
    }

    @Test
    void isValid_ValidCurrencyCode_MixedCase() {
        assertTrue(validator.isValid("UsD", context));
    }

    @Test
    void isValid_InvalidCurrencyCode_ZZZ() {
        assertFalse(validator.isValid("ZZZ", context));
    }

    @Test
    void isValid_InvalidCurrencyCode_ABC() {
        assertFalse(validator.isValid("ABC", context));
    }

    @Test
    void isValid_Null() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void isValid_EmptyString() {
        assertFalse(validator.isValid("", context));
    }

    @Test
    void isValid_BlankString() {
        assertFalse(validator.isValid("   ", context));
    }

    @Test
    void isValid_TooShort() {
        assertFalse(validator.isValid("US", context));
    }

    @Test
    void isValid_TooLong() {
        assertFalse(validator.isValid("USDD", context));
    }
}
