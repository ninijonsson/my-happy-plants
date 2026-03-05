package se.mau.myhappyplants.perenual;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import se.mau.myhappyplants.perenual.dto.PerenualPlant;
import se.mau.myhappyplants.perenual.dto.PerenualSpeciesListResponse;
import se.mau.myhappyplants.plant.dto.PlantDetailsView;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
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
        given(uriSpec.uri(any(Function.class))).willReturn(headersSpec);
        given(headersSpec.retrieve()).willReturn(responseSpec);
    }


    @Test
    void fetchPlants_returnsCorrectlyMappedResults() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant("Rose", "Rosa", "https://img.jpg", "https://thumbnail.jpg", "7", "A flower.")));

        List<PlantDetailsView> result = perenualClient.fetchPlants("rose");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Rose");
        assertThat(result.get(0).scientificName()).isEqualTo("Rosa");
        assertThat(result.get(0).imageUrl()).isEqualTo("https://img.jpg");
        assertThat(result.get(0).wateringFrequency()).isEqualTo("7");
        assertThat(result.get(0).description()).isEqualTo("A flower.");
    }
    
    @Test
    void fetchPlants_nameNull_useDefaultLable() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant(null, null, null, null, null, null)));
        
        assertThat(perenualClient.fetchPlants("rose").get(0).name()).isEqualTo("(no common name)");
    }
    
    @Test
    void fetchPlants_nullImage_useDefaultImage() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant("Rose", "Rosa", null, null, null, null)));
        
        assertThat(perenualClient.fetchPlants("rose").get(0).imageUrl()).isEqualTo("/images/plant.jpg");
    }
    
    @Test
    void fetchPlants_nullWateringFrequency_defaultToZero() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(responseWithPlant("Rose", "Rosa", "https://img.jpg", "https://thumbnail.jpg", null, null)));
        
        assertThat(perenualClient.fetchPlants("rose").get(0).wateringFrequency()).isEqualTo("0");
    }
    
    @Test
    void fetchPlants_nullResponse_returnsEmptyList() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.empty());
        
        assertThat(perenualClient.fetchPlants("rose")).isEmpty();
    }
    
    @Test
    void fetchPlants_apiThrows_returnsEmptyList() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.error(new RuntimeException("API down")));
        
        assertThat(perenualClient.fetchPlants("rose")).isEmpty();
    }

    @Test
    void fetchPlants_tooManyRequests_returnsEmptyList() {
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.error(WebClientResponseException.create(429, "Too Many Requests", null, null, null)));

        assertThat(perenualClient.fetchPlants("rose")).isEmpty();
    }
    
    @Test
    void fetchPlants_limitIs100() {
        var bigList = IntStream.range(0, 150)
                .mapToObj(i -> makeSpecies(i, "Name " + i, "Scientific Name " + i, null, null, null, null))
                .toList();
        given(responseSpec.bodyToMono(PerenualSpeciesListResponse.class))
                .willReturn(Mono.just(new PerenualSpeciesListResponse(bigList)));
        
        assertThat(perenualClient.fetchPlants("rose")).hasSize(100);
    }
    
    // Helper methods to imitate real object

    private PerenualSpeciesListResponse responseWithPlant(String name, String scientificName,
                                                          String imageUrl, String thumbnail, String wateringDays,
                                                          String description) {
        return new PerenualSpeciesListResponse(
                List.of(makeSpecies(1, name, scientificName, imageUrl, thumbnail, wateringDays, description))
        );
    }

    private PerenualPlant makeSpecies(int id, String name, String scientificName,
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
    
    
    
}