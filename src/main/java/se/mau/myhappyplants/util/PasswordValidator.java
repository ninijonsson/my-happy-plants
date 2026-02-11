package se.mau.myhappyplants.util;

import org.springframework.stereotype.Component;

/**
 * Validator för lösenordsstyrka
 */

@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 12;
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String DIGIT_PATTERN = ".*[0-9].*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*";

    /**
     * Validera lösenordsstyrka
     * Krav: minst 12 tecken, en stor bokstav, en siffra, ett specialtecken
     *
     */

    public void validate(String password) {
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }

        if (password.length() < MIN_LENGTH) {
            throw new RuntimeException("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (!password.matches(UPPERCASE_PATTERN)) {
            throw new RuntimeException("Password must contain at least one uppercase letter");
        }

        if (!password.matches(DIGIT_PATTERN)) {
            throw new RuntimeException("Password must contain at least one number");
        }

        if (!password.matches(SPECIAL_CHAR_PATTERN)) {
            throw new RuntimeException("Password must contain at least one special character (!@#$%^&*()_+-=[]{}; etc.)");
        }
    }

    /**
     * Kolla om det är giltigt lösenord utan att kasta exception
     */
    public boolean isValid(String password) {
        try {
            validate(password);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
/**
 * Utility class for validating user passwords
 * based on security requirements.
 */

public class PasswordValidator {

    //public boolean isValid(String password)
    //TODO: Implement logic for requirement ACC.10F
    // 1. check if password length is >= 12
    // 2. check for at least one uppercase letter (A-Z)
    // 3. check for at least one digit (0-9)
    // 4. check for at least one special character (e.g !"@$#*€%)
}

//TODO Add helper methods for speicalCharacter if needed
//TODO Add helper methods for containsDigit if needed
