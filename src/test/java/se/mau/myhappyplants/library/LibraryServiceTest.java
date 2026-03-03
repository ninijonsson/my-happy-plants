package se.mau.myhappyplants.library;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import se.mau.myhappyplants.user.AccountUser;
import se.mau.myhappyplants.user.AccountUserRepository;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {
    
    @Mock
    private AccountUserRepository accountUserRepository;
    
    @Mock
    private AccountUserPlantRepository accountUserPlantRepository;
    
    @InjectMocks
    private LibraryService libraryService;
    
    private int userId = 1;
    

    @Test
    @DisplayName("LIB.02F - Library Overview - Get all user's plants")
    void testGetAllUserPlants() {
        // Create mock data
        AccountUserPlant plantOne = new AccountUserPlant("Rose", "1");
        AccountUserPlant plantTwo = new AccountUserPlant("Sunflower", "2");
        //AccountUserPlant plantThree = new AccountUserPlant("Cactus", "3");
        List<AccountUserPlant> expected = Arrays.asList(plantOne, plantTwo);

        // Repo should give us back the two plants initiated
        when(accountUserPlantRepository.findByUserId(userId)).thenReturn(expected);

        // Act
        List<AccountUserPlant> result = libraryService.getAllPlantsForUser(userId);

        assertEquals(2, result.size());
        assertSame(expected, result); // Lists should be same
    }
    
    @Test
    @DisplayName("LIB.01F - Add to Library - Valid")
    void testAddPlantToLibrary() {
    
        AccountUser userMock = mock(AccountUser.class);
        AccountUserPlant plantMock = mock(AccountUserPlant.class);
        PlantDetailsView plantDetailsViewMock = mock(PlantDetailsView.class);
        
        when(plantDetailsViewMock.wateringFrequency()).thenReturn("Every 2 weeks");
        when(accountUserPlantRepository.save(any(AccountUserPlant.class))).thenReturn(plantMock);
        when(accountUserRepository.findById(userId)).thenReturn(Optional.of(userMock));
        
        AccountUserPlant result = libraryService.addPlantToLibrary(plantDetailsViewMock, userId);
        
        assertNotNull(result);
        verify(accountUserPlantRepository).save(any(AccountUserPlant.class));
        verify(accountUserRepository).findById(userId);
    }
    
    @Test
    @DisplayName("LIB.01F - Add to Library - Invalid")
    void testAddPlantToLibraryInvalid() {
        PlantDetailsView plantDetailsViewMock = mock(PlantDetailsView.class);
        
        when(accountUserRepository.findById(userId)).thenReturn(Optional.empty());
        
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> libraryService.addPlantToLibrary(plantDetailsViewMock, userId));
        
        assertTrue(exception.getMessage().contains("User not found"));
        assertTrue(exception.getMessage().contains(String.valueOf(userId)));
        
        verify(accountUserRepository).findById(userId);       
    }

    @Test
    @DisplayName("LIB.04F - Remove Plant - Valid")
    void testRemoveExistingPlant() {
        int plantId = 1;
        int userId = 1;

        AccountUserPlant plant = new AccountUserPlant("European Silver Fir", "1");
        plant.setId(plantId);

        when(accountUserPlantRepository.findByIdAndUserId(plantId, userId)).thenReturn(Optional.of(plant));

        libraryService.removePlant(plantId, userId);

        verify(accountUserPlantRepository).findByIdAndUserId(plantId, userId);
        verify(accountUserPlantRepository).delete(plant);
        verifyNoMoreInteractions(accountUserPlantRepository);
    }

    @Test
    @DisplayName("LIB.04F - Remove Plant - Invalid")
    void testRemoveNonexistingPlant() {
        int plantId = 1234567890;
        int userId = 1;

        when(accountUserPlantRepository.findByIdAndUserId(plantId, userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> libraryService.removePlant(plantId, userId));

        assertTrue(exception.getMessage().contains("Plant not found"));
        assertTrue(exception.getMessage().contains(String.valueOf(plantId)));
        assertTrue(exception.getMessage().contains(String.valueOf(userId)));

        verify(accountUserPlantRepository).findByIdAndUserId(plantId, userId);
        verify(accountUserPlantRepository, never()).delete(any());
        verifyNoMoreInteractions(accountUserPlantRepository);
    }

    @Test
    @DisplayName("LIB.06F - Sort Library - null")
    void testNullSortBy() {
        // What the mock should return when called
        // When user id found, return an empty list
        when(accountUserPlantRepository.findByUserId(eq(userId), any(Sort.class)))
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
        when(accountUserPlantRepository.findByUserId(eq(userId), any(Sort.class)))
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
        when(accountUserPlantRepository.findByUserId(eq(userId), any(Sort.class)))
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
        when(accountUserPlantRepository.findByUserId(eq(userId), any(Sort.class)))
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
        when(accountUserPlantRepository.findByUserId(eq(userId), any(Sort.class)))
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
        verify(accountUserPlantRepository).findByUserId(eq(userId), sortCaptor.capture());

        return sortCaptor.getValue();
    }

    // Tests that the function works correctly and nothing is disturbing the flow of data
    @Test
    @DisplayName("LIB.03F - Search in Library - Correct Flow")
    void testSearchInLibrary() {
        String searchTerm = "sun";
        userId = 1;
        List<AccountUserPlant> expectedPlants = List.of(new AccountUserPlant("Sunflower", "1"), 
                                                    new AccountUserPlant("Summer Lilly", "2"));
        
        when(accountUserPlantRepository.findByUserIdAndPlantNameContainingIgnoreCase(userId, searchTerm)).thenReturn(expectedPlants);
        
        List<AccountUserPlant> result = libraryService.searchPlantsByName(userId, searchTerm);
        
        assertEquals(expectedPlants, result);
        verify(accountUserPlantRepository).findByUserIdAndPlantNameContainingIgnoreCase(userId, searchTerm);
    }
    
    // Tests that the function works correctly when nothing is supposed to be sent back
    @Test
    @DisplayName("LIB.03F - Search in Library - Empty List")
    void testSearchInLibraryEmptyList() {
        String searchTerm = "nonexistent";
        userId = 1;
        List<AccountUserPlant> expectedPlants = List.of();
        
        when(accountUserPlantRepository.findByUserIdAndPlantNameContainingIgnoreCase(userId, searchTerm)).thenReturn(expectedPlants);
        
        List<AccountUserPlant> result = libraryService.searchPlantsByName(userId, searchTerm);
        
        assertEquals(expectedPlants, result);
        verify(accountUserPlantRepository).findByUserIdAndPlantNameContainingIgnoreCase(userId, searchTerm);       
    }
    
    // Tests that the function is not meddling with the parameters
    @Test
    @DisplayName("LIB.03F - Search in Library - Pass Argument Correctly")
    void testSearchInLibraryArgument() {
        String searchTerm = "cactus";
        userId = 42;
        
        when(accountUserPlantRepository.findByUserIdAndPlantNameContainingIgnoreCase(userId, searchTerm)).thenReturn(List.of());
        
        libraryService.searchPlantsByName(userId, searchTerm);
        
        verify(accountUserPlantRepository).findByUserIdAndPlantNameContainingIgnoreCase(42, "cactus");      
    }
}