package se.mau.myhappyplants.user;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @BeforeEach
    void setUp() {
    }

    @Disabled("Väntar på att logiken för ACC.05F ska implementeras")
    @Test
    @DisplayName("ACC.05F - Create account without username should fail")
    void createAccountWithoutUsername() {
        User user = new User(null, "hashed_password123");

        assertNotNull(user.getUsername(), "Username should not be null according to requirement ACC.05F");
    }

    @Test
    @DisplayName("ACC.03F - Create account with valid credentials")
    void createAccountWithValidCredentials() {
        User user = new User("TestUser", "secure_password");

        assertAll("User object should be correctly initialized",
                () -> assertEquals("TestUser", user.getUsername()),
                () -> assertEquals("secure_password", user.getPasswordHash())
        );
    }

    @Disabled
    @Test
    @DisplayName("ACC.01F - Login with valid credentials")
    void loginWithValidCredentials() {

        /**
         * a login test, assertion can be made with either a notification; SET.02F,
         * or other user info.
         */

    }

    @Disabled
    @Test
    @DisplayName("ACC.02F - Login with valid credentials")
    void logOut() {

        /**
         * a login test, assertion can be made with either a notification; SET.02F,
         * or other user info.
         */

    }



    @AfterEach
    void tearDown() {
    }
}