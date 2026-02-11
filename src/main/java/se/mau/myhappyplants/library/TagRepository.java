package se.mau.myhappyplants.library;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Database queries for tags (per user)
 * Data access layer for Tag entities.
 * Supports retrieving/creating tags per user.
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    /**
     * Hitta en tagg baserat på dess label
     */
    Optional<Tag> findByLabel(String label);

    /**
     * Hitta taggar som innehåller en viss text (case-insensitive)
     * Användbart för sökfunktion
     */
    List<Tag> findByLabelContainingIgnoreCase(String labelPart);

    /**
     * Kolla om en tagg med detta label redan finns
     */
    boolean existsByLabel(String label);
}
