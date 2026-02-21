package se.mau.myhappyplants.library;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    
    @Autowired
    private TagRepository tagRepo;
    
    public List<Tag> getAllTags() {
        return tagRepo.findAll();
    }
}
