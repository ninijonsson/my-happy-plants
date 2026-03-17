package se.mau.myhappyplants.perenual;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import se.mau.myhappyplants.perenual.dto.PerenualPlant;
import se.mau.myhappyplants.perenual.dto.PerenualSpeciesListResponse;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PerenualClientTest {
    
    @Mock private WebClient webClient;
    @Mock private WebClient.RequestHeadersUriSpec uriSpec;
    @Mock private WebClient.RequestHeadersSpec headersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    
    @Mock 
    private PerenualProperties props;
    
    @InjectMocks 
    private PerenualClient perenualClient;
    
    @BeforeEach
    void setUp() {
        given(webClient.get()).willReturn(uriSpec);
        given(uriSpec.uri(any(Function.class))).willAnswer(invocation -> {
            Function<UriBuilder, java.net.URI> uriBuilderFunction = invocation.getArgument(0);
            uriBuilderFunction.apply(UriComponentsBuilder.newInstance());  //To execute the lambda expression
            return headersSpec;
        });
        given(headersSpec.retrieve()).willReturn(responseSpec);
    }


    //Tests for fetchPlants
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_returnsCorrectlyMappedResults() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant("Rose", "Rosa", "https://img.jpg",
                        "thumb.jpg", "7", "A flower.")));

        List<PlantDetailsView> result = perenualClient.fetchPlants("rose");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Rose");
        assertThat(result.get(0).scientificName()).isEqualTo("Rosa");
        assertThat(result.get(0).imageUrl()).isEqualTo("https://img.jpg");
        assertThat(result.get(0).wateringFrequency()).isEqualTo("7");
        assertThat(result.get(0).description()).isEqualTo("A flower.");
    }
    
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_nameNull_useDefaultLable() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant(null, null, null, null, null, null)));
        
        assertThat(perenualClient.fetchPlants("rose").get(0).name()).isEqualTo("(no common name)");
    }
    
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_nullImage_useDefaultImage() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant("Rose", "Rosa", null, null, null, null)));
        
        assertThat(perenualClient.fetchPlants("rose").get(0).imageUrl()).isEqualTo("/images/plant.jpg");
    }
    
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_nullWateringFrequency_defaultToZero() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant("Rose", "Rosa", "https://img.jpg", "https://thumbnail.jpg", null, null)));
        
        assertThat(perenualClient.fetchPlants("rose").get(0).wateringFrequency()).isEqualTo("0");
    }
    
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_nullResponse_returnsEmptyList() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.empty());
        
        assertThat(perenualClient.fetchPlants("rose")).isEmpty();
    }
    
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_apiThrows_returnsEmptyList() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.error(new RuntimeException("API down")));
        
        assertThat(perenualClient.fetchPlants("rose")).isEmpty();
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_tooManyRequests_returnsEmptyList() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.error(WebClientResponseException.create(429, "Too Many Requests", null, null, null)));

        assertThat(perenualClient.fetchPlants("rose")).isEmpty();
    }
    
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_limitIs100() {
        var bigList = IntStream.range(0, 150)
                .mapToObj(i -> makePerenualPlant(i, "Name " + i, "Scientific Name " + i, null, null, null, null))
                .toList();
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(new PerenualSpeciesListResponse(bigList)));
        
        assertThat(perenualClient.fetchPlants("rose")).hasSize(100);
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_nullQuery(){
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(new PerenualSpeciesListResponse(null)));
        assertThat(perenualClient.fetchPlants(null)).isEmpty();
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_blankQuery(){
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(new PerenualSpeciesListResponse(List.of())));
        assertThat(perenualClient.fetchPlants("  ")).isEmpty();
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_blankCommonName(){
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant("  ", "Rosa", "https://imge.jpg",
                        "thumb.jpg", "7", "description")));
        assertThat(perenualClient.fetchPlants("rose").get(0).name()).isEqualTo("(no common name)");
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_blankWateringFrequency() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant("Rose", "Rosa", "https://img.jpg",
                        "thumb.jpg", "   ", "desc")));

        assertThat(perenualClient.fetchPlants("rose").get(0).wateringFrequency()).isEqualTo("0");
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_blankDescription() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant("Rose", "Rosa", "https://img.jpg",
                        "thumb.jpg", "7", "   ")));

        assertThat(perenualClient.fetchPlants("rose").get(0).description()).isNull();
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_nullScientificNameList() {
        PerenualPlant plant = new PerenualPlant(
                1, "Rose", null,
                new PerenualPlant.DefaultImage("thumb.jpg", "https://img.jpg"),
                new PerenualPlant.WateringFrequency("7"),
                "description");
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(new PerenualSpeciesListResponse(List.of(plant))));
        assertThat(perenualClient.fetchPlants("rose").get(0).scientificName()).isNull();
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlants_nullWateringFrequencyDays() {
        PerenualPlant plant = new PerenualPlant(1, "Rose", List.of("Rosa"),
                new PerenualPlant.DefaultImage("thumb.jpg", "https://imge.jpg"),
                new PerenualPlant.WateringFrequency(null), "description");

        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(new PerenualSpeciesListResponse(List.of(plant))));
        assertThat(perenualClient.fetchPlants("rose").get(0).wateringFrequency()).isEqualTo("0");
    }

    //End of tests for fetchPlants
    
    //Tests for fetchPlantById
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantById_returnsCorrectlyMappedResult() {
        given(responseSpec.bodyToMono(PerenualPlant.class))
                .willReturn(Mono.just(makePerenualPlant(1, "Rose", "Rosa", "https://img.jpg", "https://thumbnail.jpg", "7", "A flower.")));
        
        PlantDetailsView result = perenualClient.fetchPlantById("1");
        
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Rose");
        assertThat(result.scientificName()).isEqualTo("Rosa");
        assertThat(result.imageUrl()).isEqualTo("https://img.jpg");
        assertThat(result.wateringFrequency()).isEqualTo("7");
        assertThat(result.description()).isEqualTo("A flower.");
    }
    
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantById_nullResponse_returnsNull() {
        given(responseSpec.bodyToMono(PerenualPlant.class))
                .willReturn(Mono.empty());
        
        assertThat(perenualClient.fetchPlantById("1")).isNull();
    }
    
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantById_nameNull() {
        given(responseSpec.bodyToMono(PerenualPlant.class))
                .willReturn(Mono.just(makePerenualPlant(1, null, null, null, null, null, null)));
        assertThat(perenualClient.fetchPlantById("1").name()).isEqualTo(null);
        assertThat(perenualClient.fetchPlantById("1").scientificName()).isEqualTo(null);
    }
    
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantById_nullImage() {
        given(responseSpec.bodyToMono(PerenualPlant.class))
                .willReturn(Mono.just(makePerenualPlant(1, "Rose", "Rosa", null, null, null, null)));
        assertThat(perenualClient.fetchPlantById("1").imageUrl()).isEqualTo(null);
    }
    
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantById_nullWateringFrequency() {
        given(responseSpec.bodyToMono(PerenualPlant.class))
                .willReturn(Mono.just(makePerenualPlant(1, "Rose", "Rosa", "https://img.jpg", null, null, null)));
        assertThat(perenualClient.fetchPlantById("1").wateringFrequency()).isEqualTo(null);
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantById_wateringFrequencyDaysNull() {
        PerenualPlant plant = new PerenualPlant(1, "Rose", List.of("Rosa"),
                new PerenualPlant.DefaultImage("thumb.jpg", "https://imge.jpg"),
                new PerenualPlant.WateringFrequency(null), "description");
        given(responseSpec.bodyToMono(PerenualPlant.class)).willReturn(Mono.just(plant));
        assertThat(perenualClient.fetchPlantById("1").wateringFrequency()).isNull();
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantById_emptyScientificNameList() {
        PerenualPlant plant = new PerenualPlant(1, "Rose", List.of(),
                new PerenualPlant.DefaultImage("thumb.jpg", "https://imge.jpg"),
                new PerenualPlant.WateringFrequency("7"), "description");
        given(responseSpec.bodyToMono(PerenualPlant.class)).willReturn(Mono.just(plant));
        assertThat(perenualClient.fetchPlantById("1").scientificName()).isNull();
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantById_nullScientificNameList() {
        PerenualPlant plant = new PerenualPlant(1, "Rose", null,
                new PerenualPlant.DefaultImage("thumb.jpg", "https://img.jpg"),
                new PerenualPlant.WateringFrequency("7"), "description");

        given(responseSpec.bodyToMono(PerenualPlant.class)).willReturn(Mono.just(plant));
        assertThat(perenualClient.fetchPlantById("1").scientificName()).isNull();
    }

    // End of tests for fetchPlantById
    
    //Tests for fetchPlantDetails
    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantDetails_ReturnsCorrectlyMappedResult() {
        given(responseSpec.bodyToMono(PerenualPlantDetailsResponse.class))
                .willReturn(Mono.just(makePerenualPlantDetailsResponse(1, "Rose", "Rosa", 
                                                                "Somthing Fancy", "7", 
                                                                "More than once", "A flower.", 
                                                                "No can do", "Once every 10 000 km", 
                                                                "https://img.jpg", "https://thumbnail.jpg")));

        PerenualPlantDetailsResponse result = perenualClient.fetchPlantDetails("1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getCommonName()).isEqualTo("Rose");
        assertThat(result.getScientificName()).contains("Rosa");
        assertThat(result.getFamily()).isEqualTo("Somthing Fancy");
        assertThat(result.getSunlight()).contains("More than once");
        assertThat(result.getCycle()).isEqualTo("No can do");
        assertThat(result.getMaintenance()).isEqualTo("Once every 10 000 km");
        assertThat(result.getWatering()).isEqualTo("7");
        assertThat(result.getDescription()).isEqualTo("A flower.");
        assertThat(result.getDefaultImage().regularUrl()).isEqualTo("https://img.jpg");
        assertThat(result.getDefaultImage().thumbnail()).isEqualTo("https://thumbnail.jpg");
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantDetailsNullResponse(){
        given(responseSpec.bodyToMono(PerenualPlantDetailsResponse.class))
                .willReturn(Mono.empty());
        assertThat(perenualClient.fetchPlantDetails("1")).isNull();
    }

    @Test
    @DisplayName("SEA.01.5F-Third Party API-")
    void fetchPlantDetails_ThrowingPropagatesException(){
        given(responseSpec.bodyToMono(PerenualPlantDetailsResponse.class))
                .willReturn(Mono.error(new Exception("API down")));
        assertThatThrownBy(() -> perenualClient.fetchPlantDetails("1"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("API down");
    }
    //End of tests for fetchPlantDetails
    
    // Helper methods to imitate the real object
    private PerenualSpeciesListResponse responseWithPlant(String name, String scientificName,
                                                          String imageUrl, String thumbnail, String wateringDays,
                                                          String description) {
        return new PerenualSpeciesListResponse(
                List.of(makePerenualPlant(1, name, scientificName, imageUrl, thumbnail, wateringDays, description))
        );
    }

    private PerenualPlant makePerenualPlant(int id, String name, String scientificName,
                                            String imageUrl, String thumbnail, String wateringDays, String description) {
        return new PerenualPlant(
                id,
                name,
                scientificName != null ? List.of(scientificName) : List.of(),
                imageUrl != null ? new PerenualPlant.DefaultImage(thumbnail, imageUrl) : null,
                wateringDays != null ? new PerenualPlant.WateringFrequency(wateringDays) : null,
                description
        );
    }
    
    private PerenualPlantDetailsResponse makePerenualPlantDetailsResponse(int id, String commonName, String scientificName, String family,
                                                                          String watering, String sunlight, String description, String cycle, String maintenance, String imageUrl, String thumbnail) {
        PerenualPlantDetailsResponse response = new PerenualPlantDetailsResponse();
        response.setId(id);
        response.setCommonName(commonName);
        response.setScientificName(List.of(scientificName));
        response.setFamily(family);
        response.setWatering(watering);
        response.setSunlight(List.of(sunlight));
        response.setDescription(description);
        response.setCycle(cycle);
        response.setMaintenance(maintenance);
        response.setDefaultImage(new PerenualPlant.DefaultImage(thumbnail, imageUrl));
        return response;
    }
    
}