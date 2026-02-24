package se.mau.myhappyplants.config;

import org.springframework.stereotype.Component;

/**
 * Configuration class for validating password strength.
 * Enforces the following criteria for passwords:
 * - Minimum length of 12 characters.
 * - At least one uppercase letter (A-Z).
 * - At least one digit (0-9).
 * - At least one special character (!@#$%^&*()_+-=[]{}; etc.).
 *
 * Contains methods for validating a given password against these rules and
 * determining whether a password meets the defined security standards.
 */

@Component
public class PasswordValidatorConfig {

    private static final int MIN_LENGTH = 12;
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String DIGIT_PATTERN = ".*[0-9].*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*";

    /**
     * Validates a given password against the defined security rules.
     * The rules include checks for minimum length, presence of uppercase letters, digits,
     * and special characters. If the password does not meet one or more criteria,
     * a detailed response message is returned specifying the violations.
     *
     * @param password the password to validate, provided as a String
     * @return a String containing validation error messages if the password is invalid;
     *         an empty String if the password passes all validations
     */

    public String validate(String password) {
        
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
     * Check if the password is valid without throwing an exception
     */
    public String isValid(String password) {
        String response = validate(password);
        return response.isEmpty() ? "OK" : response;
    }
}
