package se.mau.myhappyplants.library;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagTest {
    
    @Mock
    private Tag tag;

    @Mock
    private AccountUserPlant plant;
    
    @Mock
    private AccountUserPlantRepository plantRepository;
    
    @Mock
    private TagRepository tagRepository;
    
    @InjectMocks
    private LibraryService  libraryService;
    
    @Test
    @DisplayName("LIB.05F - add with tag")
    void addTagTest() {
        
        int plantId = 1;
        int tagId = 1;
        
        when(plantRepository.findById(plantId)).thenReturn(Optional.of(plant));
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));

        when(plantRepository.existsById(plantId)).thenReturn(true);
        
        boolean result = libraryService.setTagOnPlant(plantId, tagId);
        
        assertTrue(result);
        
        verify(plant).setTag(tag);
        verify(plantRepository).save(plant);
    }
    
    @Test
    @DisplayName("LIB.05.3F - change tag")
    void changeTagTest() {
        
        when(plantRepository.findById(plant.getId())).thenReturn(Optional.of(plant));
        when(tagRepository.findById(tag.getId())).thenReturn(Optional.of(tag));
        when(plantRepository.existsById(plant.getId())).thenReturn(true);
        
        boolean result = libraryService.setTagOnPlant(plant.getId(), tag.getId());
        
        assertTrue(result);
        
        verify(plant).setTag(tag);
        verify(plantRepository).save(plant);
    }
    
    @Test
    @DisplayName("Remove Tag")
    void removeTagTest() {
        int plantId = 1;
        int tagId = 1;
        int removeTagId = -1;
        
        plant.setId(plantId);
        tag.setId(tagId);
        
        plant.setTag(tag);
        
        when(plantRepository.findById(plantId)).thenReturn(Optional.of(plant));
        
        libraryService.setTagOnPlant(plantId, removeTagId);

        assertNull(plant.getTag());

        verify(plantRepository).findById(plantId);
        verify(plantRepository).save(plant);
        verifyNoMoreInteractions(plantRepository);
    }
    
    @Test
    @DisplayName("Throw RuntimeExp when plant is not found")
    void expWhenPlantNotFound() {
        when(plantRepository.findById(anyInt())).thenReturn(Optional.empty());
        
        var exeption = assertThrows(RuntimeException.class, 
                () -> libraryService.setTagOnPlant(plant.getId(), tag.getId()));
        
        assertEquals("Plant not found with id: " + plant.getId(), exeption.getMessage());

        verify(plantRepository).findById(plant.getId());
        verifyNoMoreInteractions(plantRepository,  tagRepository);
    }
    
    @Test
    @DisplayName("Throw RunTimeExp when tag is not found")
    void  expWhenTagNotFound() {
        int plantId = 1;
        int tagId = 1;
        
        plant.setId(plantId);
        
        when(plantRepository.findById(plantId)).thenReturn(Optional.of(plant));
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());
        
        var exeption = assertThrows(RuntimeException.class, 
                () -> libraryService.setTagOnPlant(plantId, tagId));
        
        assertEquals("Tag not found with id: " + tagId, exeption.getMessage());
        
        verify(plantRepository).findById(plantId);
        verify(tagRepository).findById(tagId);
    }
}