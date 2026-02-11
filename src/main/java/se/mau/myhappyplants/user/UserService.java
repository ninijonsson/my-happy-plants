package se.mau.myhappyplants.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Business logic for user-related operations (get profile, update settings, change password, etc.).
 */
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Skapa ny användare (registrering)
     */
    public User createUser(String username, String password) {
        // Kolla om username redan finns
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }

        validatePassword(password);

        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, hashedPassword);
        return userRepository.save(user);
    }
    /**
     * Validera lösenordsstyrka
     * Krav: minst 8 tecken, en stor bokstav, en siffra, ett specialtecken
     */
    private void validatePassword(String password) {
        if (password.length() < 12) {
            throw new RuntimeException("Password must be at least 12 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new RuntimeException("Password must contain at least one number");
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            throw new RuntimeException("Password must contain at least one special character");
        }
    }

    /**
     * Hitta användare baserat på ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    /**
     * Hitta användare baserat på username
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    /**
     * Uppdatera användarnamn
     */
    public User updateUsername(Long userId, String newUsername) {
        // Kolla om nya username redan finns
        if (userRepository.existsByUsername(newUsername)) {
            throw new RuntimeException("Username already exists: " + newUsername);
        }

        User user = getUserById(userId);
        user.setUsername(newUsername);
        return userRepository.save(user);
    }



    /**
     * Byta lösenord
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);

        // Kolla att gamla lösenordet stämmer (jämför krypterat)
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Wrong password");
        }

        validatePassword(newPassword);

        String hashedNewPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(hashedNewPassword);
        userRepository.save(user);
    }

    /**
     * Ta bort användare
     */
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

}
