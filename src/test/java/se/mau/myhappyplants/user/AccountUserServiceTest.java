package se.mau.myhappyplants.user;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import se.mau.myhappyplants.config.PasswordValidatorConfig;

import static org.junit.jupiter.api.Assertions.*;

class AccountUserServiceTest {

    /**
     * Using a @Mock annotation allows us to use fake data
     * to test instead of cramming useless data into the database
     */

    private AccountUserService accountUserService;  // Class for handling registration logic
    private PasswordValidatorConfig passwordValidator;
    private LoginSuccessHandler loginSuccessHandler; // Class for redirecting successful login

    @BeforeEach
    void setUp() {
        userService = new UserService();
        passwordValidator = new PasswordValidatorConfig();
<<<<<<< Simon-backend-frontend-sort
        accountUserService = new AccountUserService();
=======
        loginSuccessHandler = new LoginSuccessHandler(userService);
>>>>>>> main
    }

    @Test
    @Disabled("Waiting for AccountUserService.register and AccountUserRepository.existsByUsername (ACC.01F)")
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
      //  AccountUserService.login();

        /**
         * Test will be performed by creating an account, deleting it,
         * then asserting the login results with a missing account result.
         */


    }
    @Disabled // funkar inte!!!!
    @Test
    @DisplayName("ACC.03F Create Account")
    void testCreateValidAccount() {
        boolean result = accountUserService.createUser("Random1!", "AbitNicole2026!!!", "USER");
        assertEquals(true, result);
    }


    @Disabled
    @Test
    @DisplayName("ACC.05F Error Message Missing Username")
    void testMissingUsernameRegistration() {
        boolean result = accountUserService.createUser("", "123", "USER");
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

    @Disabled
    @Test
    @DisplayName("ACC.0.3.1F - Login after creation")
    void testValidLoginAfterCreation() {

    }

    @Disabled
    @Test
    @DisplayName("ACC.0.3.1F - Login after creation")
    void testInvalidLoginAfterCreation() {}

    @AfterEach
    void tearDown() {
    }




}