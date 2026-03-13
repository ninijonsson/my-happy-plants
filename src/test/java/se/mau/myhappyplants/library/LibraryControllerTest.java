package se.mau.myhappyplants.library;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import se.mau.myhappyplants.user.AccountUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryControllerTest {

    @Mock
    private LibraryService libraryService;

    @Mock
    private TagService tagService;
    
    @Mock
    private HttpSession session;
    
    @Mock
    private Model model;

    @InjectMocks
    private LibraryController libraryController;

    @Test
    @DisplayName("LIB.02F - Library Overview - User is null -> redirect to /login")
    // TODO: Change kravnamn? To something more fitting?
    void testShowLibraryIfUserIsNull() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();

        when(session.getAttribute("user")).thenReturn(null);

        String viewName = libraryController.showLibrary("water", null, model, session);

        assertEquals("redirect:/login", viewName);

        // No call to services if user is null
        verifyNoInteractions(libraryService, tagService);
    }

    @Test
    @DisplayName("LIB.02F - Library Overview - Adds plants/user/needsWatering/currentSort to model and returns view")
    void testValidUserShowLibraryView() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();

        AccountUser user = mock(AccountUser.class);
        when(user.getId()).thenReturn(1);
        when(session.getAttribute("user")).thenReturn(user);

        String sort = "asc";

        List<AccountUserPlant> plants = List.of(
                new AccountUserPlant("Rose", "1"),
                new AccountUserPlant("Sunflower", "2")
        );

        when(libraryService.getUserLibrary(1, sort)).thenReturn(plants);
        when(libraryService.countPlantsNeedingWater(1)).thenReturn(1L);

        String viewName = libraryController.showLibrary(sort, null, model, session);

        assertEquals("/library/my-plants", viewName);
        assertEquals(plants, model.getAttribute("plants"));
        assertEquals(user, model.getAttribute("user"));
        assertEquals(1L, model.getAttribute("needsWatering"));
        assertEquals(sort, model.getAttribute("currentSort"));

        verify(libraryService).getUserLibrary(1, sort);
        verify(libraryService).countPlantsNeedingWater(1);
        verifyNoMoreInteractions(libraryService);

        // Check tagService is not interacting with controller
        verifyNoInteractions(tagService);
    }

    @Test
    @DisplayName("LIB.04F - Remove Plant - Controller deletePlant calls service and returns 200 OK")
    void testDeletePlantReturns200OK() {
        int userId = 1;
        int plantId = 10;

        ResponseEntity<Void> response = libraryController.deletePlant(userId, plantId);

        assertEquals(200, response.getStatusCode().value());
        verify(libraryService).removePlant(plantId, userId);
        verifyNoMoreInteractions(libraryService);
        verifyNoInteractions(tagService);
    }
    
    @Test
    @DisplayName("LIB.03F - Search in Library")
    void testSearchInLibrary() {
        String searchTerm = "sun";
        String sort = "asc";
        long needsWatering = 1;
        List<AccountUserPlant> plantsMock = List.of(new AccountUserPlant("Sunflower", "1"), 
                                                    new AccountUserPlant("Summer Lilly", "2"), 
                                                    new AccountUserPlant("Rose", "3"),
                                                    new AccountUserPlant("Marigold", "4"));
        
        List<AccountUserPlant> mockRegularPlants = List.of();
        List<AccountUserPlant> mockWishlistPlants = List.of();

        AccountUser userMock = mock(AccountUser.class);

        when(session.getAttribute("user")).thenReturn(userMock);
        when(libraryService.searchPlantsByName(userMock.getId(), searchTerm)).thenReturn(plantsMock);
        when(libraryService.countPlantsNeedingWater(userMock.getId())).thenReturn(needsWatering);

        String viewName = libraryController.showLibrary(sort, searchTerm, model, session);

        assertEquals("/library/my-plants", viewName);

        verify(model).addAttribute("plants", plantsMock);
        verify(model).addAttribute("regularPlants", mockRegularPlants);
        verify(model).addAttribute("wishlistPlants", mockWishlistPlants);
        verify(model).addAttribute("user", userMock);
        verify(model).addAttribute("needsWatering", needsWatering);
        verify(model).addAttribute("currentSort", sort);
        verify(model).addAttribute("currentPage", "library");

        verifyNoMoreInteractions(model);
    }
    

    @Test
    @DisplayName("LIB.06F - Sort Library")
    void testSortLibrary() {
        String sort = "asc";
        long needsWatering = 1;
        List<AccountUserPlant> plantsMock = List.of(new AccountUserPlant("Sunflower", "1"), 
                                                    new AccountUserPlant("Summer Lilly", "2"));

        List<AccountUserPlant> mockRegularPlants = List.of();
        List<AccountUserPlant> mockWishlistPlants = List.of();
        
        
        AccountUser userMock = mock(AccountUser.class);

        when(session.getAttribute("user")).thenReturn(userMock);
        when(libraryService.getUserLibrary(userMock.getId(), sort)).thenReturn(plantsMock);
        when(libraryService.countPlantsNeedingWater(userMock.getId())).thenReturn(needsWatering);

        String viewName = libraryController.showLibrary(sort, null, model, session);

        assertEquals("/library/my-plants", viewName);

        verify(model).addAttribute("plants", plantsMock);
        verify(model).addAttribute("regularPlants", mockRegularPlants);
        verify(model).addAttribute("wishlistPlants", mockWishlistPlants);
        verify(model).addAttribute("user", userMock);
        verify(model).addAttribute("needsWatering", needsWatering);
        verify(model).addAttribute("currentSort", sort);
        verify(model).addAttribute("currentPage", "library");

        verifyNoMoreInteractions(model);
    }
}