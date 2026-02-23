package se.mau.myhappyplants.library;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {
    private AccountUserPlantRepository plantRepo;
    private LibraryService libraryService;
    private int userId;

    @BeforeEach
    void setUp() {
        plantRepo = mock(AccountUserPlantRepository.class); // Creating mock of repo
        libraryService = new LibraryService(plantRepo);     // Service object where we send our mock repo
        userId = 1;                                         // Default user id
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

    @Test
    @DisplayName("LIB.06F - Sort Library - null")
    void testNullSortBy() {
        // What the mock should return when called
        // When user id found, return an empty list
        when(plantRepo.findByUserId(eq(userId), any(Sort.class)))
                .thenReturn(List.of());

        libraryService.getUserLibrary(userId, null);
        // null should sort to "water"

        Sort usedSort = captureSort();
        Sort.Order expectedOrder = usedSort.getOrderFor("nextWateringDate");

        assertNotNull(usedSort, "Expected nextWateringDate for null input");
        assertEquals(Sort.Direction.ASC, expectedOrder.getDirection());
    }

    @Test
    @DisplayName("LIB.06.1F - Sort by Water Status (needs water)")
    void testSortPlantsByWaterStatus() {
        // What the mock should return when called
        // When user id found, return an empty list
        when(plantRepo.findByUserId(eq(userId), any(Sort.class)))
                .thenReturn(List.of());

        libraryService.getUserLibrary(userId, "water"); // sortDir for water sorting

        Sort usedSort = captureSort();
        Sort.Order expectedOrder = usedSort.getOrderFor("nextWateringDate");

        assertNotNull(expectedOrder, "Expected Sort.Order for nextWateringDate but got null");
        assertEquals(Sort.Direction.ASC, expectedOrder.getDirection());
    }

    @Test
    @DisplayName("LIB.06.2F - Sort by Name - Ascending (a-z)")
    void testSortPlantsByNameAscending() {
        // What the mock should return when called
        // When user id found, return an empty list
        when(plantRepo.findByUserId(eq(userId), any(Sort.class)))
                .thenReturn(List.of());

        libraryService.getUserLibrary(userId, "asc"); // a-z

        Sort usedSort = captureSort();
        Sort.Order expectedOrder = usedSort.getOrderFor("plantName");

        assertNotNull(expectedOrder, "Expected Sort.Order for plantName but got null");
        assertEquals(Sort.Direction.ASC, expectedOrder.getDirection());
    }

    @Test
    @DisplayName("LIB.06.2F - Sort by Name - Descending (z-a)")
    void testSortPlantsByNameDescending() {
        // What the mock should return when called
        // When user id found, return an empty list
        when(plantRepo.findByUserId(eq(userId), any(Sort.class)))
                .thenReturn(List.of());

        libraryService.getUserLibrary(userId, "desc"); // z-a

        Sort usedSort = captureSort();
        Sort.Order expectedOrder = usedSort.getOrderFor("plantName");

        assertNotNull(expectedOrder, "Expected Sort.Order for plantName but got null");
        assertEquals(Sort.Direction.DESC, expectedOrder.getDirection()); // TODO: Change from direction?
    }

    @Test
    @DisplayName("LIB.06.3F - Sort by date added (most recently)")
    void testSortPlantsByRecentlyAdded() {
        // What the mock should return when called
        // When user id found, return an empty list
        when(plantRepo.findByUserId(eq(userId), any(Sort.class)))
                .thenReturn(List.of());

        libraryService.getUserLibrary(userId, "recent");

        Sort usedSort = captureSort();
        Sort.Order expectedOrder = usedSort.getOrderFor("createdAt");

        assertNotNull(expectedOrder, "Expected Sort.Order for createdAt but got null");
        assertEquals(Sort.Direction.DESC, expectedOrder.getDirection()); // TODO: Change from direction?
    }

    public Sort captureSort() {
        // ArgumentCaptor to check correct Sort object is returned
        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        // Controls that the repo method is called and capturing Sort argument
        verify(plantRepo).findByUserId(eq(userId), sortCaptor.capture());

        return sortCaptor.getValue();
    }

    @AfterEach
    void tearDown() {
    }
}