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
public interface TagRepository extends JpaRepository<Tag, Integer> {
    /**
     * find tag based on its label.
     * @param label the label we are looking for.
     */
    Optional<Tag> findByLabel(String label);

    /**
     * One tag to rule them all, One tag to find them, One tag to bring them all and in the darkness bind them.
     * @return a list of the tags, three tags for the Elven-kings under the sky, Seven for the Dwarf-lords in their halls of stone, Nine for the Mortal Men doomed to die, One for the Dark Lord on his dark throne.
     */

    List<Tag> findAll();

    /**
     * Find tags containing a piece of text (case-insensitive)
     * Useful for the search functionality.
     * @param labelPart, the part which we are checking of the label.
     */

    List<Tag> findByLabelContainingIgnoreCase(String labelPart);

    /**
     * Checks if a tag by a certain name already exists.
     * @param label the label we are looking for.
     */

    boolean existsByLabel(String label);
}
