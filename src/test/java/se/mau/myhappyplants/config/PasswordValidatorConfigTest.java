package se.mau.myhappyplants.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("ACC.10F - Valid password should not be empty")
    void testPasswordEmpty() {
        String emptyPass = "";
        String result = validator.validate(emptyPass);
        assertTrue(result.contains("Password cannot be empty"));
    }

    @Test
    @DisplayName("ACC.10F - Valid password should not be empty")
    void testPasswordNull() {
        String nullPass = "";
        String result = validator.validate(nullPass);
        assertTrue(result.contains("Password cannot be empty"));
    }

    @Test
    @DisplayName("ACC.10F - Valid password should include at least 1 number")
    void testPasswordWithoutDigits() {
        String passWithNoDigit = "Password.aaa";
        assertEquals("Password must contain at least one digit (0-9).\n", validator.isValid(passWithNoDigit));
    }

    @Test
    @DisplayName("ACC.10F - Valid password should pass all rules")
    void testValidPassword() {
        String validPassword = "MyHappyPlantTest2026!"; //Has everything to be a valid password
        assertEquals("OK", validator.isValid(validPassword));
    }
}
