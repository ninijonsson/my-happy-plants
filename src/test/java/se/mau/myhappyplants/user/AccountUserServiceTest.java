package se.mau.myhappyplants.user;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import se.mau.myhappyplants.config.PasswordValidatorConfig;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AccountUserServiceTest {

    /**
     * Using a @Mock annotation allows us to use fake data
     * to test instead of cramming useless data into the database
     */
    @Mock
    UserRepository userRepository; // Fake mocked database

    @InjectMocks
    private UserService userService;  // Class for handling registration logic
    private PasswordValidatorConfig passwordValidator;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidatorConfig();
    }

    @Test
    @Disabled("Waiting for UserService.register and UserRepository.existsByUsername (ACC.01F)")
    @DisplayName("ACC.01F - Should return false if username is taken")
    void registerUserButUsernameIsTaken() {
        /**
         * This is the plan for when the logic is implemented
         * This is a Mock test to make sure that the real database does
         * not get impacted by our fake profile tests
         */

//        String username = "HussanLovesPlants";
//        when(userRepository.existsByUsername(username)).thenReturn(true);
//
//        boolean result = userService.register(new User(username, "password"));
//
//        assertFalse(result, "Registration should fail when username exists in DB");
    }

    @Disabled("Väntar på att logiken för ACC.05F, ACC.04F ACC.07F ska implementeras")
    @Test
    @DisplayName("ACC.04F ACC.04.1F ACC.07F - Delete Account Test")
    void deleteAccountTest() {
      //  User user = new User("Sven", "hashed_password123");
      //  Userservice.deleteAccount(user);
      //  UserService.login();

        /**
         * Test will be performed by creating an account, deleting it,
         * then asserting the login results with a missing account result.
         */


    }
    @Disabled // funkar inte!!!!
    @Test
    @DisplayName("ACC.03F Create Account")
    void testCreateValidAccount() {
        boolean result = userService.createUser("Random1!", "AbitNicole2026!!!", "USER");
        assertEquals(true, result);
    }


    @Test
    @DisplayName("ACC.05F Error Message Missing Username")
    void testMissingUsernameRegistration() {
        boolean result = userService.createUser("", "123", "USER");
        assertEquals(false, result);
    }

    @Disabled
    @Test
    @DisplayName("ACC.06F Error Message Incorrect Password")
    void testIncorrectPassword() {
        //boolean result = userService.("test", "123", "USER");
        //assertEquals(false, result);
    }

    @Test
    @DisplayName("ACC.10F Password Rules")
    void testValidPasswordRules() {
        String result = passwordValidator.isValid("AbitNicole2026!");
        assertEquals("OK", result);
    }


    @Test
    @DisplayName("ACC.10.1F Password Rules Infomration + ACC.10F Password Rules when invalid")
    void testPasswordRulesInformation() {
        String result = passwordValidator.validate("abit");

        StringBuilder response = new StringBuilder();
        response.append("Password must be at least 12 characters long.\n");
        response.append("Password must contain at least one uppercase letter (A-Z).\n");
        response.append("Password must contain at least one digit (0-9).\n");
        response.append("Password must contain at least one special character (!@#$%^&*()_+-=[]{}; etc.).\n");

        String expected = response.toString();

        assertEquals(expected, result);
    }



    @AfterEach
    void tearDown() {
    }




}