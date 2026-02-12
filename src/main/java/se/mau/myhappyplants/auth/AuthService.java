package se.mau.myhappyplants.auth;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.mau.myhappyplants.user.User;
import se.mau.myhappyplants.user.UserRepository;


/**
 * Service för autentisering (inloggning)
 */

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Logga in användare
     * Jämför lösenord med BCrypt
     */
    public User login(String username, String password) {
        // Hitta användaren
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Kolla att lösenordet stämmer (jämför krypterat)
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Wrong password");
        }

        return user;
    }
}
