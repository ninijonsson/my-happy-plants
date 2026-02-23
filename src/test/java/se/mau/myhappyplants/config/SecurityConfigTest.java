package se.mau.myhappyplants.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    private HTTPSecurityConfig config;


    @BeforeEach
    void setUp() {
        config = new HTTPSecurityConfig();
    }

    @Test
    @DisplayName("ACC.01F Login")
    void testLoginConfiguration() {

    }


    @Test
    @DisplayName("ACC.02F Logout")
    void testLogoutConfiguration() {
        assertNotNull(config, "Configuration should not be null");
    }


    @AfterEach
    void tearDown() {
    }
}