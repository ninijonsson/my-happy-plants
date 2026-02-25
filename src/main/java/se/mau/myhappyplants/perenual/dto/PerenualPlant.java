package se.mau.myhappyplants.perenual.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a plant object as returned by the Perenual API.
 * This record is designed to map the structure of the response from the external API.
 * It contains details such as the identifier, common name, scientific name(s),
 * images, watering information, and a description of the plant.
 *
 * Note:
 * - The fields are annotated with {@link JsonProperty} to facilitate JSON deserialization.
 * - This record is structured to work specifically with the data format of the Perenual API.
 *
 * Fields:
 * - id (int): The unique identifier of the plant.
 * - commonName (String): The common name of the plant. Maps to `common_name` in JSON.
 * - scientificName (List<String>): A list of scientific names of the plant. Maps to `scientific_name` in JSON.
 * - defaultImage (DefaultImage): Information about the plant's default image, including a thumbnail and regular URL. Maps to `default_image` in JSON.
 * - wateringFrequency (WateringFrequency): General watering benchmark for the plant. Maps to `watering_general_benchmark` in JSON.
 * - description (String): A textual description of the plant.
 *
 * Nested Records:
 * - DefaultImage: Represents details about the images related to the plant.
 * - WateringFrequency: Represents information about the recommended watering frequency.
 */
public record PerenualPlant(
        int id,
        @JsonProperty("common_name") String commonName,
        @JsonProperty("scientific_name") List<String> scientificName,
        @JsonProperty("default_image") DefaultImage defaultImage,
        @JsonProperty("watering_general_benchmark") WateringFrequency wateringFrequency,
        @JsonProperty("description") String description
) {
    public record DefaultImage(
            @JsonProperty("thumbnail") String thumbnail,
            @JsonProperty("regular_url") String regularUrl
    ) {}
    public record WateringFrequency(
            @JsonProperty("value") String wateringFrequencyDays
    ){}
}