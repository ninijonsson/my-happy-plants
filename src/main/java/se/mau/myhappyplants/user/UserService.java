package se.mau.myhappyplants.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.mau.myhappyplants.util.PasswordValidator;

/**
 * Business logic for user-related operations (get profile, update settings, change password, etc.).
 */
@Service
public class UserService {

    public void deleteAccount(User user){}

    public void login(){}
  
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordValidator passwordValidator;

    /**
     * Skapa ny användare (registrering)
     */
    public User createUser(String username, String password) {
        // Kolla om username redan finns
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        //validera lösenordsstyrka
        passwordValidator.validate(password);

        //kryptera lösenord med BCrypt
        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(username, hashedPassword);
        return userRepository.save(user);
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

        //validera nya lösenordets styrka
        passwordValidator.validate(newPassword);

        //kryptera nya lösenordet
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
