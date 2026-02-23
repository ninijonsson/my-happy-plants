package se.mau.myhappyplants.user;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.mau.myhappyplants.config.PasswordValidatorConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountUserServiceTest {

    @Mock
    private AccountUserRepository accountUserRepository;


    @InjectMocks
    private AccountUserService accountUserService;

    private PasswordValidatorConfig passwordValidator;

    /**
     * Using a @Mock annotation allows us to use fake data
     * to test instead of cramming useless data into the database
     */


    @BeforeEach
    void setUp() {
       passwordValidator = new PasswordValidatorConfig();
        //accountUserService = new AccountUserService();
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
    //@Disabled // funkar inte!!!!
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
                "AbitNicole2026!!!",
                "USER"
        );

        assertTrue(result);
    }


    //@Disabled
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

    @Test
    @DisplayName("ACC.11F Username Taken Error Message")
    void testUsernameTaken() {

        AccountUser existingUser = new AccountUser();
        existingUser.setUsername("test");

        when(accountUserRepository.findByUsername("test"))
                .thenReturn(Optional.of(existingUser));

        boolean result = accountUserService.createUser(
                "test",
                "password123!",
                "USER"
        );

        assertFalse(result, "Username is already taken");
    }



    @AfterEach
    void tearDown() {
    }




}