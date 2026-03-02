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
    private AccountUserPlantRepository plantRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private LibraryService libraryService;

    private AccountUserPlant realPlant;
    private Tag realTag;

    @BeforeEach
    void setUp() {
        realPlant = new AccountUserPlant();
        realPlant.setId(1);

        realTag = new Tag();
        realTag.setId(10);
        realTag.setLabel("Test Tag");
    }

    @Test
    @DisplayName("LIB.05F - Successfully add a tag to a plant")
    void addTagTest() {
        when(plantRepository.findById(1)).thenReturn(Optional.of(realPlant));
        when(tagRepository.findById(10)).thenReturn(Optional.of(realTag));
        when(plantRepository.save(realPlant)).thenReturn(realPlant);

        boolean result = libraryService.setTagOnPlant(1, 10);

        assertTrue(result);
        assertEquals(realTag, realPlant.getTag());
        verify(plantRepository).save(realPlant);
    }

    @Test
    @DisplayName("Remove Tag - Setting tagId to -1 should nullify the plant's tag")
    void removeTagTest() {
        realPlant.setTag(realTag);
        int removeTagId = -1;

        when(plantRepository.findById(1)).thenReturn(Optional.of(realPlant));

        libraryService.setTagOnPlant(1, removeTagId);

        assertNull(realPlant.getTag());
        verify(plantRepository).save(realPlant);
    }

    @Test
    @DisplayName("Exception: Throw error when plant ID does not exist")
    void expWhenPlantNotFound() {
        int nonExistentId = 99;
        when(plantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        var exception = assertThrows(RuntimeException.class,
                () -> libraryService.setTagOnPlant(nonExistentId, 10));

        assertEquals("Plant not found with id: " + nonExistentId, exception.getMessage());
        verify(tagRepository, never()).findById(anyInt());
    }

    @Test
    @DisplayName("Exception: Throw error when tag ID does not exist")
    void expWhenTagNotFound() {
        when(plantRepository.findById(1)).thenReturn(Optional.of(realPlant));
        when(tagRepository.findById(500)).thenReturn(Optional.empty());

        var exception = assertThrows(RuntimeException.class,
                () -> libraryService.setTagOnPlant(1, 500));

        assertTrue(exception.getMessage().contains("Tag not found"));
    }
}