package se.mau.myhappyplants.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class responsible for handling business logic related to tags.
 * This service acts as an intermediary between controllers and the data access layer
 */

@Service
public class TagService {
    
    @Autowired
    private TagRepository tagRepo;

    /**
     * Retrieves all available tags from the database.
     *
     * @return a list of all tags
     */
    
    public List<Tag> getAllTags() {
        return tagRepo.findAll();
    }



}

