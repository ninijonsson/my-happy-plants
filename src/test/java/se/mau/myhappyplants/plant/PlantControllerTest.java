package se.mau.myhappyplants.plant;

import jakarta.servlet.http.HttpSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import se.mau.myhappyplants.library.AccountUserPlant;
import se.mau.myhappyplants.library.LibraryService;
import se.mau.myhappyplants.perenual.PerenualClient;
import se.mau.myhappyplants.user.AccountUser;

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
        
        String result = plantsController.showLibraryPlantDetails(id, model, session);
        
        assertEquals("plant-details", result);
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
}
