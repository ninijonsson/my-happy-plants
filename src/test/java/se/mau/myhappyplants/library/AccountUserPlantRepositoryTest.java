package se.mau.myhappyplants.library;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

// Using mock to make sure we dont mess up the real database with out test
@ExtendWith(MockitoExtension.class)
class AccountUserPlantRepositoryTest {

    @BeforeEach
    void setUp() {
    }

    @Mock
    private UserPlantRepository userPlantRepository; // Mock database interaction

    @InjectMocks
    private LibraryService libraryService; // Klassen som vi vill testa

    @Test
    @Disabled("Awaiting LIB.01F implementation")
    @DisplayName("LIB.01F - Successfully add plant to user library")
    void testAddPlantToUserLibrary() {
        //TODO: get the user and a plant from search results
        // add the plantID to the correct userID
    }

    @Test
    @Disabled("Awaiting LIB.04F implementation")
    @DisplayName("LIB.04F - Successfully remove plant from library")
    void testSuccessfullyRemovePlantFromUserLibrary() {
        //TODO: get a plantID from userID
        // remove the libraryItem (the plant based on the ID)
    }

    @Test
    @Disabled("Awaiting LIB.04.1F implementation")
    @DisplayName("LIB.04.1F - Deletion requires confirmation logic check")
    void testRemovePlantRequiresConfirmation() {
        //TODO: test the confirmation logic if the user has selected that
        // they want to remove a plant from their library.
    }


    @AfterEach
    void tearDown() {
    }
}