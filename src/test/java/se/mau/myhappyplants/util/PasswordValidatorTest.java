package se.mau.myhappyplants.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Test
    @DisplayName("ACC.10F - Password should fail if under 12 characters")
    void testPasswordTooShort() {
        String tooShortPassword = "Short1!"; //Short pass but everything else is correct
        assertFalse(validator.isValid(tooShortPassword), "11 characters " +
                "should not be accepted");
    }

    @Test
    @DisplayName("ACC.10F - Password should fail if special character is missing")
    void testPasswordMissingSpecialCharacter() {
        String specialCharMissing = "NoSpecialChar2026"; //Missing special char but everything else is correct
        assertFalse(validator.isValid(specialCharMissing), "Missing special character" +
                "should be invalid");
    }

    @Test
    @DisplayName("ACC.10F - Password should fail if uppercase is missing")
    void testPasswordMissingUppercase() {
        String passMissingUppercase = "lowercase12345!"; //Missing uppercase but everything else is correct
        assertFalse(validator.isValid(passMissingUppercase), "Missing uppercase" +
                "should be invalid");
    }

    @Test
    @DisplayName("ACC.10F - Valid password should pass all rules")
    void testValidPassword() {
        String validPassword = "MyHappyPlantTest2026!"; //Has everything to be a valid password
        assertTrue(validator.isValid(validPassword), "A correct password " +
                "must return true");
    }
}
