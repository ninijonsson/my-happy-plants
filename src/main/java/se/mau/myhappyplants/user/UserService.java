package se.mau.myhappyplants.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

/**
 * Business logic for user-related operations (get profile, update settings, change password, etc.).
 */
@Service

public class UserService implements UserDetailsService {
      
    @Autowired
    private UserRepository userRepository;
        

    /**
     * Skapa ny användare (registrering)
     */
    public boolean createUser(String username, String password, String role) {
        // Kolla om username redan finns
        if(username.isBlank()) {
            return false;
        }
        if (userRepository.findByUsername(username).isPresent()) {
            return false;
        }
        
        AccountUser user = new AccountUser();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setRole(role);
        
        userRepository.save(user);
        
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Hitta användare baserat på ID
     */
    public AccountUser getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    /**
     * Hitta användare baserat på username
     */
    public AccountUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    /**
     * Uppdatera användarnamn
     */
    public AccountUser updateUsername(int userId, String newUsername) {
        // Kolla om nya username redan finns
        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new RuntimeException("Username already exists: " + newUsername);
        }

        AccountUser user = getUserById(userId);
        user.setUsername(newUsername);
        return userRepository.save(user);
    }
    
    /**
     * Byta lösenord
     */
//    public void changePassword(int userId, String oldPassword, String newPassword) {
//        AccountUser user = getUserById(userId);
//
//        // Kolla att gamla lösenordet stämmer (jämför krypterat)
//        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
//            throw new RuntimeException("Wrong password");
//        }
//
//        //validera nya lösenordets styrka
//        passwordValidatorConfig.validate(newPassword);
//
//        //kryptera nya lösenordet
//        String hashedNewPassword = passwordEncoder.encode(newPassword);
//
//        user.setPasswordHash(hashedNewPassword);
//        userRepository.save(user);
//    }

    /**
     * Ta bort användare
     */
    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AccountUser> accountUser = userRepository.findByUsername(username);
        if(accountUser.isPresent()) {
            var userObj = accountUser.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .roles(userObj.getRole())
                    .build();
        }else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    private String[] getRoles(AccountUser user) {
        String role = user.getRole();
        if(role.isBlank()) {
            return new String[] {"USER"};
        }

        return role.split(",");
    }
}
