package se.mau.myhappyplants.plant;

import jakarta.servlet.http.HttpSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.mau.myhappyplants.library.AccountUserPlant;
import se.mau.myhappyplants.library.LibraryService;
import se.mau.myhappyplants.perenual.PerenualClient;
import se.mau.myhappyplants.perenual.PerenualPlantDetailsResponse;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import se.mau.myhappyplants.user.AccountUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantControllerTest {

    @Mock
    private PerenualClient perenualClient;

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private PlantsController plantsController;

    @Test
    @DisplayName("INF.02F-Plant Information Page")
    void showLibraryPlantDetailsTestValid() {
        int id = 1;
        AccountUserPlant plant = mock(AccountUserPlant.class);
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();
        when(libraryService.getPlantById(id)).thenReturn(plant);
        when(session.getAttribute("user")).thenReturn(mock(AccountUser.class));
        when(plant.getPerenualId()).thenReturn("1");
        when(perenualClient.fetchPlantDetails("1")).thenReturn(new PerenualPlantDetailsResponse());
        assertEquals("plant-details", plantsController.showLibraryPlantDetails(id, model, session));
    }

    @Test
    @DisplayName("INF.02F-Plant Information Page - Error with retrieving the plant data")
    void showLibraryPlantDetailsTestInvalid_nullPlant(){
        int id = 1;
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();

        when(session.getAttribute("user")).thenReturn(mock(AccountUser.class));
        when(libraryService.getPlantById(id)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> plantsController.showLibraryPlantDetails(id, model, session));
    }

    @Test
    @DisplayName("ACC.01F-Login - redirect when not logged in")
    void showLibraryPlantDetails_nullUser_redirectsToLogin() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();
        when(session.getAttribute("user")).thenReturn(null);
        String result = plantsController.showLibraryPlantDetails(1, model, session);
        assertEquals("redirect:/login", result);
    }

    @Test
    @DisplayName("ACC.01F-Login - redirect when not logged in")
    void previewSearchPlant_nullUser_redirectsToLogin() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();
        when(session.getAttribute("user")).thenReturn(null);

        String result = plantsController.previewSearchPlant("42", model, session);

        assertEquals("redirect:/login", result);
    }

    @Test
    @DisplayName("SEA.01F-Plant Search - null query")
    void showPlants_nullQuery_setsEmptyQueryString() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();
        plantsController.showPlants(null, model, session);
        assertEquals("", model.getAttribute("query"));
    }

    @Test
    @DisplayName("SEA.01F-Plant Search - with query")
    void showPlants_withQuery_setsQueryInModel() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();
        plantsController.showPlants("rose", model, session);
        assertEquals("rose", model.getAttribute("query"));
    }

    @Test
    @DisplayName("SEA.01F-Plant Search")
    void getPlantById_returnsOk() {
        Model model = new ExtendedModelMap();
        when(perenualClient.fetchPlants("1")).thenReturn(List.of());
        var response = plantsController.getPlantById("1", model);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("ACC.01F-Login - redirect when not logged in")
    void addPlant_nullUser_redirectsToLogin() {
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(session.getAttribute("user")).thenReturn(null);
        String result = plantsController.addPlant("1", redirectAttributes, session);
        assertEquals("redirect:/login", result);
    }

    @Test
    @DisplayName("LIB.01F-Add to Library")
    void addPlant_success_addsSuccessMessage() {
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        AccountUser user = mock(AccountUser.class);
        PlantDetailsView plant = mock(PlantDetailsView.class);

        when(session.getAttribute("user")).thenReturn(user);
        when(perenualClient.fetchPlantById("1")).thenReturn(plant);
        String result = plantsController.addPlant("1", redirectAttributes, session);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), any());
        assertEquals("redirect:/plants/search", result);
    }

    @Test
    @DisplayName("LIB.01F-Add to Library - service failure")
    void addPlant_serviceThrows_addsErrorMessage() throws Exception {
        HttpSession session = mock(HttpSession.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        AccountUser user = mock(AccountUser.class);
        PlantDetailsView plant = mock(PlantDetailsView.class);

        when(session.getAttribute("user")).thenReturn(user);
        when(perenualClient.fetchPlantById("1")).thenReturn(plant);
        doThrow(new RuntimeException("DB error")).when(libraryService).addPlantToLibrary(any(), anyInt());
        String result = plantsController.addPlant("1", redirectAttributes, session);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), any());
        assertEquals("redirect:/plants/search", result);
    }

    @Test
    @DisplayName("INF.02F-Plant Information Page")
    void previewSearchPlant_validUser_returnsPlantDetails() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();
        when(session.getAttribute("user")).thenReturn(mock(AccountUser.class));
        when(perenualClient.fetchPlantDetails("42")).thenReturn(new PerenualPlantDetailsResponse()); // add this

        assertEquals("plant-details", plantsController.previewSearchPlant("42", model, session));
    }

    @Test
    @DisplayName("INF.02F-Plant Information Page - API returns null details")
    void prepareDetails_nullApiResponse_redirectsToLibrary() {
        AccountUser user = mock(AccountUser.class);
        Model model = new ExtendedModelMap();
        when(perenualClient.fetchPlantDetails("1")).thenReturn(null);
        String result = plantsController.prepareDetails("1", null, model, user);
        assertEquals("redirect:/library", result);
    }

    @Test
    @DisplayName("INF.02F-Plant Information Page - API returns null via previewSearchPlant")
    void previewSearchPlant_nullApiResponse_redirectsToLibrary() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();

        when(session.getAttribute("user")).thenReturn(mock(AccountUser.class));
        when(perenualClient.fetchPlantDetails("42")).thenReturn(null);
        String result = plantsController.previewSearchPlant("42", model, session);
        assertEquals("redirect:/library", result);
    }
}
