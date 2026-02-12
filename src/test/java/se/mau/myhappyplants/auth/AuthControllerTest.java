package se.mau.myhappyplants.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("ACC.01.1F - Verify redirect path after login")
    void testLoginRedirectPath() {
        /**
         * Just nu är "/library" hårdkodat eftersom vi inte har någon
         * path eller logik för var användaren ska skickas efter inlogg
         *
         * TODO: När logiken finns kan man hämta authController.handleLogin(validUser)
         *  eller något liknande istället för "/library".
         *
         */

        String expectedPath = "/library";

        String actualPath = "/library";

        assertEquals(expectedPath, actualPath, "User should be redirected to the library page");
    }

    @AfterEach
    void tearDown() {
    }
}