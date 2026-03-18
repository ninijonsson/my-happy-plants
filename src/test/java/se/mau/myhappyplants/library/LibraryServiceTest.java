package se.mau.myhappyplants.library;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {
    
    @Mock
    private AccountUserRepository accountUserRepository;
    
    @Mock
    private AccountUserPlantRepository accountUserPlantRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private WateringHistoryRepository wateringHistoryRepository;
    
    @InjectMocks
    private LibraryService libraryService;
    
    private int userId = 1;
    private AccountUserPlant plant;
    private AccountUser user;

    @BeforeEach
    void setUp() {
        user = new AccountUser();
        user.setId(1);

        plant = new AccountUserPlant("Monstera", "123");
        plant.setId(1);
        plant.setUser(user);
        plant.setWateringFrequencyDays(7);
    }
    

    @Test
    @DisplayName("LIB.02F - Library Overview - Get all user's plants")
    void testGetAllUserPlants() {
        // Create mock data
        AccountUserPlant plantOne = new AccountUserPlant("Rose", "1");
        AccountUserPlant plantTwo = new AccountUserPlant("Sunflower", "2");
        //AccountUserPlant plantThree = new AccountUserPlant("Cactus", "3");
        List<AccountUserPlant> expected = Arrays.asList(plantOne, plantTwo);

        // Repo should give us back the two plants initiated
        when(accountUserPlantRepository.findByUserId(userId, Sort.unsorted())).thenReturn(expected);

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
//        when(accountUserPlantRepository.findByUserId(eq(userId), any(Sort.class)))
//                .thenReturn(List.of());

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

    @Test
    @DisplayName("LIB.06.4F - getPlantsSortedByTag asc correctly orders plants by tag label")
    void testGetPlantsSortedByTagAscOrder() {
        Tag tagA = mock(Tag.class);
        when(tagA.getLabel()).thenReturn("Aloe");
        Tag tagZ = mock(Tag.class);
        when(tagZ.getLabel()).thenReturn("Zebra");

        AccountUserPlant plantZ = new AccountUserPlant("ZebraPlant", "30");
        plantZ.setTag(tagZ);
        AccountUserPlant plantA = new AccountUserPlant("AloePlant", "31");
        plantA.setTag(tagA);
        AccountUserPlant plantNoTag = new AccountUserPlant("NoTagPlant", "32");

        when(accountUserPlantRepository.findByUserId(eq(userId), eq(Sort.unsorted())))
                .thenReturn(new java.util.ArrayList<>(List.of(plantZ, plantNoTag, plantA)));

        List<AccountUserPlant> result = libraryService.getUserLibrary(userId, "asctag");

        assertEquals("AloePlant", result.get(0).getPlantName());
        assertEquals("ZebraPlant", result.get(1).getPlantName());
        assertEquals("NoTagPlant", result.get(2).getPlantName(), "Plant with no tag should be last");
    }


    @Test
    @DisplayName("LIB.06.4F - getPlantsSortedByTag desc correctly orders plants by tag label")
    void testGetPlantsSortedByTagDescOrder() {
        Tag tagA = mock(Tag.class);
        when(tagA.getLabel()).thenReturn("Aloe");
        Tag tagZ = mock(Tag.class);
        when(tagZ.getLabel()).thenReturn("Zebra");

        AccountUserPlant plantA = new AccountUserPlant("AloePlant", "40");
        plantA.setTag(tagA);
        AccountUserPlant plantZ = new AccountUserPlant("ZebraPlant", "41");
        plantZ.setTag(tagZ);
        AccountUserPlant plantNoTag = new AccountUserPlant("NoTagPlant", "42");

        when(accountUserPlantRepository.findByUserId(eq(userId), eq(Sort.unsorted())))
                .thenReturn(new java.util.ArrayList<>(List.of(plantA, plantNoTag, plantZ)));

        List<AccountUserPlant> result = libraryService.getUserLibrary(userId, "desctag");

        assertEquals("ZebraPlant", result.get(0).getPlantName());
        assertEquals("AloePlant", result.get(1).getPlantName());
        assertEquals("NoTagPlant", result.get(2).getPlantName(), "Plant with no tag should be last");
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

    
    @Test
    @DisplayName("CAR.01F - countPlantsNeedingWater() returns correct count when plants are overdue")
    void testCountPlantsNeedingWaterWhenOverdue() {
        AccountUserPlant overduePlant1 = new AccountUserPlant("Monstera", "1");
        overduePlant1.setWateringFrequencyDays(7);
        overduePlant1.setLastWatered(LocalDateTime.now().minusDays(8)); // overdue

        AccountUserPlant overduePlant2 = new AccountUserPlant("Cactus", "2");
        overduePlant2.setWateringFrequencyDays(14);
        overduePlant2.setLastWatered(LocalDateTime.now().minusDays(15)); // overdue

        AccountUserPlant okPlant = new AccountUserPlant("Pothos", "3");
        okPlant.setWateringFrequencyDays(7);
        okPlant.setLastWatered(LocalDateTime.now().minusDays(2)); // not overdue

        when(accountUserPlantRepository.findByUserId(1, Sort.unsorted()))
                .thenReturn(List.of(overduePlant1, overduePlant2, okPlant));

        long count = libraryService.countPlantsNeedingWater(1);

        assertEquals(2, count,
                "Should return 2 since only two plants are overdue");
    }

    @Test
    @DisplayName("CAR.01F - countPlantsNeedingWater() returns 0 when no plants are overdue")
    void testCountPlantsNeedingWaterNoneOverdue() {
        AccountUserPlant plant1 = new AccountUserPlant("Monstera", "1");
        plant1.setWateringFrequencyDays(7);
        plant1.setLastWatered(LocalDateTime.now().minusDays(2));

        AccountUserPlant plant2 = new AccountUserPlant("Cactus", "2");
        plant2.setWateringFrequencyDays(14);
        plant2.setLastWatered(LocalDateTime.now().minusDays(1));

        when(accountUserPlantRepository.findByUserId(1, Sort.unsorted()))
                .thenReturn(List.of(plant1, plant2));

        long count = libraryService.countPlantsNeedingWater(1);

        assertEquals(0, count,
                "Should return 0 when no plants are overdue");
    }

    @Test
    @DisplayName("CAR.01F - countPlantsNeedingWater() returns 0 when user has no plants")
    void testCountPlantsNeedingWaterWhenNoPlants() {
        when(accountUserPlantRepository.findByUserId(1, Sort.unsorted())).thenReturn(List.of());

        long count = libraryService.countPlantsNeedingWater(1);

        assertEquals(0, count,
                "Should return 0 when user has no plants");
    }

    @Test
    @DisplayName("CAR.01F - countPlantsNeedingWater() skips plants without lastWatered")
    void testCountPlantsNeedingWaterWhenLastWateredNull(){
        AccountUserPlant plantWithoutDate = new AccountUserPlant("Monstera", "1");
        plantWithoutDate.setWateringFrequencyDays(7);
        // lastWatered is not set

        when(accountUserPlantRepository.findByUserId(1, Sort.unsorted()))
                .thenReturn(List.of(plantWithoutDate));

        long count = libraryService.countPlantsNeedingWater(1);

        assertEquals(0,count, "Should skip plants without a lastWatered date");
    }


    @Test
    @DisplayName("CAR.02F - Watering a plant updates lastWatered to the given date")
    void testWaterPlantUpdatesLastWatered() {
        LocalDateTime wateringDate = LocalDateTime.now();
        when(accountUserPlantRepository.findByIdAndUserId(1, 1))
                .thenReturn(Optional.of(plant));
        when(accountUserPlantRepository.save(any(AccountUserPlant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(wateringHistoryRepository.save(any(WateringHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        libraryService.waterPlant(1, 1, wateringDate);

        assertEquals(wateringDate, plant.getLastWatered(),
                "lastWatered should be updated to the given watering date");
    }

    @Test
    @DisplayName("CAR.02F - Watering a plant saves the plant to the repository")
    void testWaterPlantSavesPlantToRepository() {
        LocalDateTime wateringDate = LocalDateTime.now();
        when(accountUserPlantRepository.findByIdAndUserId(1, 1))
                .thenReturn(Optional.of(plant));
        when(accountUserPlantRepository.save(any(AccountUserPlant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(wateringHistoryRepository.save(any(WateringHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        libraryService.waterPlant(1, 1, wateringDate);

        verify(accountUserPlantRepository, times(1)).save(plant);
    }

    @Test
    @DisplayName("CAR.02F - Watering a plant saves a record to watering history")
    void testWaterPlantSavesWateringHistory() {
        LocalDateTime wateringDate = LocalDateTime.now();
        when(accountUserPlantRepository.findByIdAndUserId(1, 1))
                .thenReturn(Optional.of(plant));
        when(accountUserPlantRepository.save(any(AccountUserPlant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(wateringHistoryRepository.save(any(WateringHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        libraryService.waterPlant(1, 1, wateringDate);

        verify(wateringHistoryRepository, times(1)).save(any(WateringHistory.class));
    }

    @Test
    @DisplayName("CAR.02F - Watering history record contains correct date")
    void testWaterPlantHistoryRecordHasCorrectDate() {
        LocalDateTime wateringDate = LocalDateTime.of(2025, 3, 1, 12, 0);
        when(accountUserPlantRepository.findByIdAndUserId(1, 1))
                .thenReturn(Optional.of(plant));
        when(accountUserPlantRepository.save(any(AccountUserPlant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(wateringHistoryRepository.save(any(WateringHistory.class)))
                .thenAnswer(invocation -> {
                    WateringHistory saved = invocation.getArgument(0);
                    assertEquals(wateringDate, saved.getWateredAt(),
                            "WateringHistory should contain the correct watering date");
                    return saved;
                });

        libraryService.waterPlant(1, 1, wateringDate);
    }

    @Test
    @DisplayName("CAR.02F - Throws exception if plant does not belong to user")
    void testWaterPlantWhenPlantNotFound() {
        when(accountUserPlantRepository.findByIdAndUserId(1, 1))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> libraryService.waterPlant(1, 1, LocalDateTime.now()),
                "Should throw RuntimeException if plant is not found for this user");
    }

    @Test
    @DisplayName("CAR.04F - getUserWateringSummary() groups watering events correctly by date")
    void testGetUserWateringSummaryDate(){
        LocalDateTime day1 = LocalDateTime.of(2025,3,1,10,0);
        LocalDateTime day2 = LocalDateTime.of(2025,2,1,10,0);

        WateringHistory h1 = new WateringHistory(user,plant,day1);
        WateringHistory h2 = new WateringHistory(user,plant,day1); //same day as h1
        WateringHistory h3 = new WateringHistory(user,plant,day2);

        when(wateringHistoryRepository.findByUserIdOrderByWateredAtDesc(1))
                .thenReturn(List.of(h1,h2,h3));

        List<Map<String, Object>> summary = libraryService.getUserWateringSummary(1);

        assertEquals(2, summary.size(), "Should have 2 entries, one per unique date");

        Map<String, Object> entryDay1 = summary.stream()
                .filter(e -> e.get("date").equals(LocalDate.of(2025, 3, 1).toString()))
                .findFirst()
                .orElseThrow();

        assertEquals(2L, entryDay1.get("count"), "Day 1 should have a count of 2");
    }

    @Test
    @DisplayName("CAR.04F - getUserWateringSummary() returns empty list when no history exists")
    void testGetUserWateringSummaryEmpty() {
        when(wateringHistoryRepository.findByUserIdOrderByWateredAtDesc(1))
                .thenReturn(List.of());

        List<Map<String, Object>> summary = libraryService.getUserWateringSummary(1);

        assertTrue(summary.isEmpty(), "Should return empty list when user has no watering history");
    }

    @Test
    @DisplayName("CAR.04F - getUserWateringSummary() returns one entry per unique date")
    void testGetUserWateringSummaryUnique() {
        LocalDateTime day1 = LocalDateTime.of(2025, 1, 1, 9, 0);
        LocalDateTime day2 = LocalDateTime.of(2025, 2, 1, 9, 0);
        LocalDateTime day3 = LocalDateTime.of(2025, 3, 1, 9, 0);

        when(wateringHistoryRepository.findByUserIdOrderByWateredAtDesc(1))
                .thenReturn(List.of(
                        new WateringHistory(user, plant, day1),
                        new WateringHistory(user, plant, day2),
                        new WateringHistory(user, plant, day3)
                ));

        List<Map<String, Object>> summary = libraryService.getUserWateringSummary(1);

        assertEquals(3, summary.size(), "Should return one entry per unique date");
    }

    @Test
    @DisplayName("LIB.06.4F - Default case returns plants sorted by nextWateringDate, wishlist last")
    void testDefaultCaseSortsCorrectly() {
        AccountUserPlant wishlistPlant = new AccountUserPlant("Orchid", "10");
        Tag wishlistTag = mock(Tag.class);
        when(wishlistTag.getLabel()).thenReturn("Wishlist");
        wishlistPlant.setTag(wishlistTag);
        wishlistPlant.setWateringFrequencyDays(7);
        wishlistPlant.setLastWatered(LocalDateTime.now().minusDays(1));

        AccountUserPlant urgentPlant = new AccountUserPlant("Monstera", "11");
        urgentPlant.setWateringFrequencyDays(7);
        urgentPlant.setLastWatered(LocalDateTime.now().minusDays(10)); // very overdue

        AccountUserPlant normalPlant = new AccountUserPlant("Pothos", "12");
        normalPlant.setWateringFrequencyDays(7);
        normalPlant.setLastWatered(LocalDateTime.now().minusDays(3));

        when(accountUserPlantRepository.findByUserId(eq(userId), eq(Sort.unsorted())))
                .thenReturn(new java.util.ArrayList<>(List.of(wishlistPlant, normalPlant, urgentPlant)));

        List<AccountUserPlant> result = libraryService.getUserLibrary(userId, "unknownvalue");

        assertEquals("Monstera", result.get(0).getPlantName(), "Most overdue plant should be first");
        assertEquals("Pothos", result.get(1).getPlantName(), "Less overdue plant should be second");
        assertEquals("Orchid", result.get(2).getPlantName(), "Wishlist plant should always be last");
    }
    
    @Test
    @DisplayName("LIB.05.1F - setTagOnPlantByLabel")
    void testSetTagOnPlantByLabel() {
        String label = "tag1";  
        int plantId = 1;
        AccountUserPlant mockPlant = mock(AccountUserPlant.class);
        mockPlant.setId(plantId);
        Tag mockTag = mock(Tag.class);
        
        
        when(accountUserPlantRepository.findById(plantId)).thenReturn(Optional.of(mockPlant));
        when(tagRepository.findByLabel(label)).thenReturn(Optional.of(mockTag));
        
        assertTrue(libraryService.setTagOnPlantByLabel(plantId, label));
        
        verify(accountUserPlantRepository).findById(plantId);
        verify(tagRepository).findByLabel(label);
        verify(accountUserPlantRepository).save(mockPlant);
        
        verifyNoMoreInteractions(accountUserPlantRepository, tagRepository);
    }
    
    @Test
    @DisplayName("LIB.05.1F - setTagOnPlantByLabel - label is null")
    void testSetTagOnPlantByLabelPlantNotFound() {
        String label = null;  
        int plantId = 1;
        
        assertFalse(libraryService.setTagOnPlantByLabel(plantId, label));
        
        verifyNoMoreInteractions(accountUserPlantRepository, tagRepository);
    }

    @AfterEach
    void tearDown() {
        plant = null;
        user = null;
    }
}