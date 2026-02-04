package se.mau.myhappyplants.plant.dto;

/**
 * DTO for the plant details view.
 * Contains the data required by the Thymeleaf template for a plant details page.
 */
public record PlantDetailsView(
        int id,
        String name,
        String scientificName,
        String imageUrl
) {}