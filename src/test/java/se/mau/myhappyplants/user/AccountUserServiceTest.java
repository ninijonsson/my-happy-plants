package se.mau.myhappyplants.user;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import se.mau.myhappyplants.config.PasswordValidatorConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountUserServiceTest {

    @Mock
    private AccountUserRepository accountUserRepository;
    
    @InjectMocks
    private AccountUserService accountUserService;
    
    @Test
    @DisplayName("ACC.01F - Should return false if username is taken")
    void registerUserButUsernameIsTaken() {
        
        AccountUser existingUser = mock(AccountUser.class);
        existingUser.setUsername("HussanLovesPlants");
        String username = "HussanLovesPlants";
        when(accountUserRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        boolean result = accountUserService.createUser(username, "password");

        assertFalse(result, "Registration should fail when username exists in DB");
    }

    @Test
    @DisplayName("ACC.04F ACC.04.1F ACC.07F - Deleting Account with existing user")
    void testDeleteAccountValid() {
        when(accountUserRepository.existsById(1)).thenReturn(true);
        accountUserService.deleteUser(1);
        verify(accountUserRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("ACC.04F ACC.04.1F ACC.07F - Deleting when User not found")
    void testDeleteAccountTestNotFound() {
        when(accountUserRepository.existsById(1)).thenReturn(false);
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountUserService.deleteUser(1));
        assertEquals("User not found with id: 1", exception.getMessage());
        verify(accountUserRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("ACC.03F Create Account")
    void testCreateValidAccount() {

        AccountUser user = new AccountUser();
        user.setUsername("RandomUser");

        when(accountUserRepository.findByUsername("RandomUser"))
                .thenReturn(Optional.empty())  // före save
                .thenReturn(Optional.of(user)); // efter save

        when(accountUserRepository.save(any(AccountUser.class)))
                .thenReturn(user);

        boolean result = accountUserService.createUser(
                "RandomUser",
                "AbitNicole2026!!!"
        );

        assertTrue(result);
    }


    @Test
    @DisplayName("ACC.05F Error Message Missing Username")
    void testMissingUsernameRegistration() {
        boolean result = accountUserService.createUser("", "123");
        assertEquals(false, result);
    }

    @Test
    @DisplayName("ACC.07F Error Message Non-Existent Account")
    void testNonExistentAccount() {

        when(accountUserRepository.findByUsername("NoName"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountUserService.getUserByUsername("NoName");
        });

        assertEquals(
                "User not found with username: NoName",
                exception.getMessage()
        );
    }
    
    @Test
    @DisplayName("ACC.10F Password Rules")
    void testValidPasswordRules() {
        PasswordValidatorConfig passwordValidator = new PasswordValidatorConfig();
        
        String result = passwordValidator.isValid("AbitNicole2026!");
        assertEquals("OK", result);
    }


    @Test
    @DisplayName("ACC.10.1F Password Rules Infomration + ACC.10F Password Rules when invalid")
    void testPasswordRulesInformation() {
        PasswordValidatorConfig passwordValidator = new PasswordValidatorConfig();
        String result = passwordValidator.validate("abit");

        StringBuilder response = new StringBuilder();
        response.append("Password must be at least 12 characters long.\n");
        response.append("Password must contain at least one uppercase letter (A-Z).\n");
        response.append("Password must contain at least one digit (0-9).\n");
        response.append("Password must contain at least one special character (!@#$%^&*()_+-=[]{}; etc.).\n");

        String expected = response.toString();

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("ACC.11F Username Taken Error Message")
    void testUsernameTaken() {

        AccountUser existingUser = new AccountUser();
        existingUser.setUsername("test");

        when(accountUserRepository.findByUsername("test"))
                .thenReturn(Optional.of(existingUser));

        boolean result = accountUserService.createUser(
                "test",
                "password123!"
        );

        assertFalse(result, "Username is already taken");
    }

    @Test
    void testLoadUserByUsernameValid() {
        AccountUser mockUser = new AccountUser();
        mockUser.setUsername("Mock");
        mockUser.setPasswordHash("hashedPass£$");
        mockUser.setRole("USER");

        when(accountUserRepository.findByUsername("Mock")).thenReturn(Optional.of(mockUser));
        UserDetails details = accountUserService.loadUserByUsername("Mock");

        assertEquals("Mock", details.getUsername());
        assertEquals("hashedPass£$", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(
                a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("ACC.06F - Error Message Incorrect Password - Unknown username throws exception")
    void testLoadUserByUsernameInvalid() {
        when(accountUserRepository.findByUsername("Mock")).thenReturn(Optional.empty());
        UsernameNotFoundException e = assertThrows(UsernameNotFoundException.class, () ->
            accountUserService.loadUserByUsername("Mock"));
        assertEquals("User not found with username: Mock", e.getMessage());
    }

    @Test
    @DisplayName("This method is called by other method (updateUsername) but with no usage")
    void testGetUserByIdValid() {
        AccountUser mockUser = new AccountUser();
        mockUser.setUsername("Mock");
        mockUser.setId(1);

        when(accountUserRepository.findById(1)).thenReturn(Optional.of(mockUser));
        AccountUser result = accountUserService.getUserById(1);
        assertEquals(1, result.getId());
        assertEquals("Mock", result.getUsername());
    }

    @Test
    @DisplayName("This method is called by other method (updateUsername) but with no usage")
    void testGetUserByIdInvalid() {
        when(accountUserRepository.findById(1)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            accountUserService.getUserById(1));
        assertEquals("User not found with id: 1", exception.getMessage());
    }
}