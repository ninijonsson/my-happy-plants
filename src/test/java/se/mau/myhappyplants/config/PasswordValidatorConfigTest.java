package se.mau.myhappyplants.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PasswordValidatorConfigTest {

    private final PasswordValidatorConfig validator = new PasswordValidatorConfig();

    @Test
    @DisplayName("ACC.10F - Password should fail if under 12 characters")
    void testPasswordTooShort() {
        String tooShortPassword = "Short1!"; //Short pass but everything else is correct
        assertEquals("Password must be at least 12 characters long.\n", validator.isValid(tooShortPassword));
    }

    @Test
    @DisplayName("ACC.10F - Password should fail if special character is missing")
    void testPasswordMissingSpecialCharacter() {
        String specialCharMissing = "NoSpecialChar2026"; //Missing special char but everything else is correct
        assertEquals("Password must contain at least one special character (!@#$%^&*()_+-=[]{}; etc.).\n", validator.isValid(specialCharMissing));
    }

    @Test
    @DisplayName("ACC.10F - Password should fail if uppercase is missing")
    void testPasswordMissingUppercase() {
        String passMissingUppercase = "lowercase12345!"; //Missing uppercase but everything else is correct
        assertEquals("Password must contain at least one uppercase letter (A-Z).\n", validator.isValid(passMissingUppercase));
    }

    @Test
    @DisplayName("ACC.10F - Valid password should pass all rules")
    void testValidPassword() {
        String validPassword = "MyHappyPlantTest2026!"; //Has everything to be a valid password
        assertEquals("OK", validator.isValid(validPassword));
    }
}
