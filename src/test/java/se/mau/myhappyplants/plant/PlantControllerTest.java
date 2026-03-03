package se.mau.myhappyplants.plant;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import se.mau.myhappyplants.library.LibraryService;
import se.mau.myhappyplants.perenual.PerenualClient;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import se.mau.myhappyplants.user.AccountUser;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


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
}
