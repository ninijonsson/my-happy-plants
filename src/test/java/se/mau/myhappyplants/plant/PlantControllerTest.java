package se.mau.myhappyplants.plant;

import jakarta.servlet.http.HttpSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import se.mau.myhappyplants.library.AccountUserPlant;
import se.mau.myhappyplants.library.LibraryService;
import se.mau.myhappyplants.perenual.PerenualClient;
import se.mau.myhappyplants.perenual.PerenualPlantDetailsResponse;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import se.mau.myhappyplants.user.AccountUser;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(PlantsController.class)
@ExtendWith(MockitoExtension.class)
public class PlantControllerTest {
    
    @Autowired
    private MockMvc mvc;
    
    @MockitoBean
    private PerenualClient perenualClient;
    
    @MockitoBean
    private LibraryService libraryService;

    @MockitoBean // or @MockBean depending on your import status
    private org.springframework.cache.CacheManager cacheManager;
  
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
        when(session.getAttribute("user")).thenReturn(mock(AccountUser.class));

        String result = plantsController.showLibraryPlantDetails(id, model, session);

        assertEquals("plant-details", result);
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
  
    @Test
    @DisplayName("LIB.01F - Add to Library - Valid")
    void testAddPlantToLibrary() throws Exception {
        String perenualPlantId = String.valueOf('1');
        PlantDetailsView plantDetailsViewMock = mock(PlantDetailsView.class);
        AccountUser userMock = mock(AccountUser.class);
        
        when(perenualClient.fetchPlantById(perenualPlantId)).thenReturn(plantDetailsViewMock);
        
        mvc.perform(MockMvcRequestBuilders
                .post("/plants/add")
                .accept(MediaType.APPLICATION_FORM_URLENCODED)
                .param("perenualPlantId", perenualPlantId)
                .sessionAttr("user", userMock))
            .andExpect(MockMvcResultMatchers.redirectedUrl("/plants/search"));
    }
    
    @Test
    @DisplayName("LIB.01F - Add to Library - Invalid")
    void testAddPlantToLibraryInvalid() throws Exception {
        String perenualPlantId = String.valueOf('1');
        PlantDetailsView plantDetailsViewMock = mock(PlantDetailsView.class);
        

        when(perenualClient.fetchPlantById(perenualPlantId)).thenReturn(plantDetailsViewMock);

        mvc.perform(MockMvcRequestBuilders
                        .post("/plants/add")
                        .accept(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("perenualPlantId", perenualPlantId))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"));
    }

    @Test
    @DisplayName("INF.02F-Plant Information Page - view plant info in user library ")
    void testShowLibraryPlantDetailsValid() throws Exception {
        AccountUserPlant plant = new AccountUserPlant();
        plant.setPerenualId("100");
        AccountUser userMock = mock(AccountUser.class);

        when(libraryService.getPlantById(1)).thenReturn(plant);

        PerenualPlantDetailsResponse detailsResponseMock = mock(PerenualPlantDetailsResponse.class);
        when(perenualClient.fetchPlantDetails("100")).thenReturn(detailsResponseMock);

        mvc.perform(MockMvcRequestBuilders.get("/plants/plant-details/1").sessionAttr("user", userMock))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.view().name("plant-details"))
                        .andExpect(MockMvcResultMatchers.model().attributeExists("details", "plant"));
    }

    @Test
    @DisplayName("INF.02F-Plant Information Page - plant is null ")
    @Disabled("Add the error handling in the code if the plant is null. Look in PlantsController class")
    void testShowLibraryPlantDetailsInvalidId() throws  Exception {
        when(libraryService.getPlantById(100)).thenReturn(null);

        mvc.perform(MockMvcRequestBuilders.get("/plants/plant-details/100"))
                        .andExpect(MockMvcResultMatchers.status().is5xxServerError());
        verify(libraryService).getPlantById(100);
    }

    @Test
    @DisplayName("INF.02F-Plant Information Page - no user redirect to login ")
    void testShowLibraryPlantDetailsNullUser() throws  Exception {
        AccountUserPlant plant = new AccountUserPlant();
        plant.setPerenualId("111");
        when(libraryService.getPlantById(1)).thenReturn(plant);

        mvc.perform(MockMvcRequestBuilders.get("/plants/plant-details/1"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/login"));
        verify(libraryService).getPlantById(1);
    }

    @Test
    @DisplayName("INF.02F-Plant Information Page - Plant details page contains required plant information fields")
    void testShowLibraryPlantDetails_correctPlantInfo() throws  Exception {
        AccountUserPlant plant = new AccountUserPlant();
        plant.setPerenualId("200");
        AccountUser userMock = mock(AccountUser.class);

        PerenualPlantDetailsResponse detailsMock = mock(PerenualPlantDetailsResponse.class);
        when(detailsMock.getCommonName()).thenReturn("Lavender");
        when(detailsMock.getScientificName()).thenReturn(List.of("Lavandula angustifolia"));
        when(detailsMock.getFamily()).thenReturn("Lamiaceae");
        when(detailsMock.getWatering()).thenReturn("3");
        when(detailsMock.getSunlight()).thenReturn(List.of("Full sun"));
        when(libraryService.getPlantById(1)).thenReturn(plant);
        when(perenualClient.fetchPlantDetails("200")).thenReturn(detailsMock);

        mvc.perform(MockMvcRequestBuilders.get("/plants/plant-details/1").sessionAttr("user", userMock))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("details", detailsMock))
                .andExpect(MockMvcResultMatchers.model().attributeExists("details"))

                .andExpect(result -> {
                    PerenualPlantDetailsResponse details = (PerenualPlantDetailsResponse)
                            result.getModelAndView().getModel().get("details");
                    assertEquals("Lavender", details.getCommonName());
                    assertFalse(details.getScientificName().isEmpty());
                    assertEquals("Lamiaceae", details.getFamily());
                    assertEquals("3", details.getWatering());
                    assertFalse(details.getSunlight().isEmpty());
                });
    }

}
