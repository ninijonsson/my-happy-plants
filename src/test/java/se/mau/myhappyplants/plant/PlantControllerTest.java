package se.mau.myhappyplants.plant;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import se.mau.myhappyplants.library.AccountUserPlant;
import se.mau.myhappyplants.library.LibraryService;
import se.mau.myhappyplants.perenual.PerenualClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlantControllerTest {

    @Mock
    private PerenualClient perenualClient;

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    PlantsController plantsController;

    @Test
    @DisplayName("INF.02F-Plant Information Page")
    void showLibraryPlantDetailsTestValid(){
        int id = 1;
        AccountUserPlant plant = mock(AccountUserPlant.class);
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();
        when(libraryService.getPlantById(id)).thenReturn(plant);
        when(plant.getPerenualId()).thenReturn("1249");

        plantsController.showLibraryPlantDetails(id, model, session);

        verify(libraryService).getPlantById(id);
    }

    @Test
    @DisplayName("INF.02F-Plant Information Page - Error with retrieving the plant data")
    void showLibraryPlantDetailsTestInvalid_nullPlant(){
        int id = 1;
        HttpSession session = mock(HttpSession.class);
        Model model = new ExtendedModelMap();

        when(libraryService.getPlantById(id)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> plantsController.showLibraryPlantDetails(id, model, session));
        verify(libraryService).getPlantById(id);
    }

}
