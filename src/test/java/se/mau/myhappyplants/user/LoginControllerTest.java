package se.mau.myhappyplants.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.mau.myhappyplants.config.PasswordValidatorConfig;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidatorConfig passwordValidatorConfig;

    @Mock
    private AccountUserService accountUserService;

    @InjectMocks
    private LoginController loginController;

    @Test
    @DisplayName("ACC.01F-Login - User is null -> redirect to /login")
    void testLogInWhenUserIsNull(){
        String viewName = loginController.login();
        assertEquals("/auth/login", viewName);
    }

    @Test
    void testCreateUserBadRequest(){
        when(passwordValidatorConfig.isValid("123")).thenReturn("Password too short");

        ResponseEntity<?> response = loginController.createUser("user", "123");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password too short", ((Map<?, ?>)response.getBody()).get("message"));
    }

    @Test
    void testCreateNewUserSuccess(){
        when(passwordValidatorConfig.isValid("123456789Aa.")).thenReturn("OK");
        when(passwordEncoder.encode("123456789Aa.")).thenReturn("hashedPassword");
        when(accountUserService.createUser("newUser", "hashedPassword", "USER")).thenReturn(true);

        ResponseEntity<?> response = loginController.createUser("newUser", "123456789Aa.");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User created successfully", ((Map<?, ?>)response.getBody()).get("message"));
    }

    @Test
    void createExistingUser(){
        when(passwordValidatorConfig.isValid("123456789Aa.")).thenReturn("OK");
        when(passwordEncoder.encode("123456789Aa.")).thenReturn("hashedPassword");
        when(accountUserService.createUser("newUser", "hashedPassword", "USER")).thenReturn(false);

        ResponseEntity<?> response = loginController.createUser("newUser", "123456789Aa.");
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username already exists", ((Map<?, ?>)response.getBody()).get("message"));
    }
}
