package se.mau.myhappyplants.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidatorTest {

    @Disabled("Disabled until PasswordValidator logic has been imlpemented ACC.10F")
    @Test
    @DisplayName("ACC.10F - Password strength validation")
    void testPasswordStrengthRules() {
        //Contains upper case, special character but too short (not 12 chars)
        String tooShort = "Short1!";

        //Long enough, has upper case but no special character
        String noSpecial = "Password12345";

        //Valid password
        String validPassword = "MyHappyPlantTest2026!";

//        assertAll("Password rules validation",
//                () -> assertFalse(PasswordValidator.isValid(tooShort), "Too short"),
//                () -> assertFalse(PasswordValidator.isValid(noSpecial), "Missing special character"),
//                () -> assertTrue(PasswordValidator.isValid(valid), "Valid password should pass")
//        );
    }
}
