package se.mau.myhappyplants.config;

import org.springframework.stereotype.Component;

/**
 * Validator för lösenordsstyrka
 */

@Component
public class PasswordValidatorConfig {

    private static final int MIN_LENGTH = 12;
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String DIGIT_PATTERN = ".*[0-9].*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*";

    /**
     * Validera lösenordsstyrka
     * Krav: minst 12 tecken, en stor bokstav, en siffra, ett specialtecken
     *
     */

    private String validate(String password) {
        
        StringBuilder response = new StringBuilder();
        
        if (password == null || password.isEmpty()) {
            response.append("Password cannot be empty.\n");
        }

        if (password.length() < MIN_LENGTH) {
            response.append("Password must be at least " + MIN_LENGTH + " characters long.\n");
        }

        if (!password.matches(UPPERCASE_PATTERN)) {
            response.append("Password must contain at least one uppercase letter (A-Z).\n");
        }

        if (!password.matches(DIGIT_PATTERN)) {
            response.append("Password must contain at least one digit (0-9).\n");
        }

        if (!password.matches(SPECIAL_CHAR_PATTERN)) {
            response.append("Password must contain at least one special character (!@#$%^&*()_+-=[]{}; etc.).\n");
        }
        
        return response.toString();
    }

    /**
     * Kolla om det är giltigt lösenord utan att kasta exception
     */
    public String isValid(String password) {
        String response = validate(password);
        return response.isEmpty() ? "OK" : response;
    }
}
/**
 * Utility class for validating user passwords
 * based on security requirements.
 */

    //public boolean isValid(String password)
    //TODO: Implement logic for requirement ACC.10F
    // 1. check if password length is >= 12
    // 2. check for at least one uppercase letter (A-Z)
    // 3. check for at least one digit (0-9)
    // 4. check for at least one special character (e.g !"@$#*€%)

//TODO Add helper methods for speicalCharacter if needed
//TODO Add helper methods for containsDigit if needed
