package se.mau.myhappyplants.perenual.dto;

import java.util.List;

/**
 * DTO representing the response from the Perenual API when fetching a list of plant species.
 *
 * The response contains a list of PerenualPlant objects.
 * Each entry represents a plant returned from the API search or default listing.
 *
 * This record is used for mapping JSON responses to Java objects.
 *
 * @param data a list of plants returned by the API
 */

public record PerenualSpeciesListResponse(List<PerenualPlant> data) {}