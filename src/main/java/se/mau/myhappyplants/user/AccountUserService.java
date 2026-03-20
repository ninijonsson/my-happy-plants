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
public class AccountUserService implements UserDetailsService {
      
    @Autowired
    private AccountUserRepository accountUserRepository;
        

    /**
     * Create a new user (registrering)
     * @param username new username
     * @param password new password
     * @return returns a boolean depending on if the username is taken.
     */
    public boolean createUser(String username, String password) {
        // Kolla om username redan finns
        if(username.isBlank()) {
            return false;
        }
        if (accountUserRepository.findByUsername(username).isPresent()) {
            return false;
        }
        
        AccountUser user = new AccountUser();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setRole("USER");
        
        accountUserRepository.save(user);
        
        return accountUserRepository.findByUsername(username).isPresent();
    }

    /**
     * Find user by ID
     * @param userId user to be found.
     * @return user if found.
     *
     */
    public AccountUser getUserById(int userId) {
        return accountUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    /**
     * Find user by username
     * @param username user to be found.
     * @return user if found
     */
    public AccountUser getUserByUsername(String username) {
        return accountUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    /**
     * Uppdatera användarnamn
     * @param userId user to change name
     * @param newUsername new username
     * @return accountUser.
     */
    public AccountUser updateUsername(int userId, String newUsername) {
        // Kolla om nya username redan finns
        if (accountUserRepository.findByUsername(newUsername).isPresent()) {
            throw new RuntimeException("Username already exists: " + newUsername);
        }

        AccountUser user = getUserById(userId);
        user.setUsername(newUsername);
        return accountUserRepository.save(user);
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
        if (!accountUserRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        accountUserRepository.deleteById(userId);
    }

    /**
     * loads userdetails user by username
     * @param username
     * @return user details
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AccountUser> accountUser = accountUserRepository.findByUsername(username);
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
}
