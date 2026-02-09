package se.mau.myhappyplants.user;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * Using a @Mock annotation allows us to use fake data
     * to test instead of cramming useless data into the database
     */
    @Mock
    UserRepository userRepository; // Fake mocked database

    @InjectMocks
    private UserService userService;  // Class for handling registration logic

    @BeforeEach
    void setUp() {
    }

    @Test
    @Disabled("Waiting for UserService.regiseter and UserRepository.existsByUsername (ACC.01F)")
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

    @AfterEach
    void tearDown() {
    }
}