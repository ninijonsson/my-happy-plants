package se.mau.myhappyplants.plant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import se.mau.myhappyplants.library.LibraryService;
import se.mau.myhappyplants.perenual.PerenualClient;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;
import se.mau.myhappyplants.user.AccountUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


/**
 * Unit tests for SEA.01F - Plant Search
 *
 *  TODO: Remove disabled when SEA.01.2F, SEA.01.3F, SEA.01.4F is implemented
 */

@ExtendWith(MockitoExtension.class)
@DisplayName("SEA.01F - Plant Search)")
public class PlantSearchServiceTest {

    @Mock
    private PerenualClient perenualClient;

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    PlantsController plantsController;

    private MockHttpSession session;
    private ExtendedModelMap model;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        model = new ExtendedModelMap();
        AccountUser user = new AccountUser();
        user.setId(1);
        session.setAttribute("user", user); 
    }


    @Test
    @DisplayName("SEA.01F - Search should return a valid response object")
    void searchWithValidQuery() {
        List<PlantDetailsView> mockResults = List.of(
                new PlantDetailsView(1,"Heather", "Calluna vulgaris", "img/heather.jpg", "7", "A purple plant"),
                new PlantDetailsView(2, "Heather Pink", "Erica carnea", "/img/pink.jpg", "7", null)
        );
        when(perenualClient.fetchPlants("Heather")).thenReturn(mockResults);

        String view = plantsController.showPlants("Heather", model, session);

        assertEquals("plant/plant-search", view);
        assertEquals(mockResults, model.get("plants"));
        assertEquals("Heather", model.get("query"));
    }

    @Test
    @DisplayName("SEA.01F - PerenualClient is called exactly one time per search")
    void searchCallsApiExactlyOnce(){
        when(perenualClient.fetchPlants("Monstera")).thenReturn(List.of());

        plantsController.showPlants("Monstera", model, session);

        verify(perenualClient, times(1)).fetchPlants("Monstera");
       
    }

    // SEA.01.1F — Inform the user if no valid results exists

    @Test
    @DisplayName("SEA.01.1F - Unknown search term returns empty list to the view")
    void searchWithInvalidQuery(){
        when(perenualClient.fetchPlants("uvwxyzåäö999")).thenReturn(List.of());

        String view = plantsController.showPlants("uvwxyzåäö999", model, session);

        assertEquals("plant/plant-search", view);
        assertEquals(List.of(), model.get("plants"));
    }

    @Test
    @DisplayName("SEA.01.1F - Null-query puts an empty String as query-attribute ")
    void SearchWithNullQuery(){
        when(perenualClient.fetchPlants(null)).thenReturn(List.of());

        assertDoesNotThrow(() -> plantsController.showPlants(null, model, session));

        assertEquals("", model.get("query"));
    }

    @Test
    @DisplayName("SEA.01.1F - Empty String as query wihtout a crash")
    void SearchWithBlankQuery(){
        when(perenualClient.fetchPlants("")).thenReturn(List.of());

        assertDoesNotThrow(() -> plantsController.showPlants("", model, session));
    }

    // SEA.01.2F — Filter indoor plants

    @Disabled("SEA.01.2F - Waiting for 'indoor'-field to be added in PlantDetailsView")
    @Test
    @DisplayName("SEA.01.2F - Returns all indoor plants")
    void searchFilterIndoor() {
        // TODO: Implement when outdoor-field exists in PlantDetailsView

    }


    // SEA.01.3F — Filter outdoor plants

    @Disabled("SEA.01.3F - Waiting for 'outdoor'-field to be added in PlantDetailsView")
    @Test
    @DisplayName("SEA.01.3F -Returns all outdoor plants")
    void searchFilterOutdoor() {
        // TODO: Implement when outdoor-field exists in PlantDetailsView
    }

    // SEA.01.4F — Filter non-poisonous plants

    @Disabled("SEA.01.4F - Waiting for 'non-poisonous'-field to be added in PlantDetailsView")
    @Test
    @DisplayName("SEA.01.4F - Returns all non-poisonous plants")
    void searchFilterNonPoisonous() {
        // TODO: Implement when non-poisonous-field exists in PlantDetailsView
    }

    @Disabled("SEA.01.4F -  Waiting for 'non-poisonous'-field to be added in PlantDetailsView")
    @Test
    @DisplayName("SEA.01.4F - Empty list if all the results are poisonous")
    void search_FilterNonPoisonous_WhenAllPoisonous_ReturnsEmptyList() {
        // TODO: Implement when non-poisonous-field exists in PlantDetailsView
    }
}
