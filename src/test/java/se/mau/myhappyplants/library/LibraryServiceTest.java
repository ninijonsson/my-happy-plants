package se.mau.myhappyplants.library;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("LIB.04F - Remove Plant - Valid")
    void testRemoveExistingPlant() {
        // Needs as an argument for library service
        AccountUserPlantRepository userPlantRepo = mock(AccountUserPlantRepository.class);
        LibraryService libraryService = new LibraryService(userPlantRepo);

        int plantId = 1;
        int userId = 1;

        AccountUserPlant plant = new AccountUserPlant("European Silver Fir", "1");
        plant.setId(plantId);

        when(userPlantRepo.findByIdAndUserId(plantId, userId)).thenReturn(Optional.of(plant));

        libraryService.removePlant(plantId, userId);

        verify(userPlantRepo).findByIdAndUserId(plantId, userId);
        verify(userPlantRepo).delete(plant);
        verifyNoMoreInteractions(userPlantRepo);
    }

    @Test
    @DisplayName("LIB.04F - Remove Plant - Invalid")
    void testRemoveNonexistingPlant() {
        // Needs as an argument for library service
        AccountUserPlantRepository userPlantRepo = mock(AccountUserPlantRepository.class);
        LibraryService libraryService = new LibraryService(userPlantRepo);

        int plantId = 1234567890;
        int userId = 1;

        when(userPlantRepo.findByIdAndUserId(plantId, userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> libraryService.removePlant(plantId, userId));

        assertTrue(exception.getMessage().contains("Plant not found"));
        assertTrue(exception.getMessage().contains(String.valueOf(plantId)));
        assertTrue(exception.getMessage().contains(String.valueOf(userId)));

        verify(userPlantRepo).findByIdAndUserId(plantId, userId);
        verify(userPlantRepo, never()).delete(any());
        verifyNoMoreInteractions(userPlantRepo);
    }

    @AfterEach
    void tearDown() {
    }
}