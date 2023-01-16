package carshare.service;

import carshare.advice.exception.ImageCreationException;
import carshare.advice.exception.ImageNotFoundException;
import carshare.database.entity.Car;
import carshare.database.entity.Image;
import carshare.database.repository.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ImageServiceTest {

    @MockBean
    private ImageRepository imageRepository;

    private final ImageService imageService;

    @Autowired
    public ImageServiceTest(final ImageService imageService) {
        this.imageService = imageService;
    }

    static Image image;
    static Car car;

    @BeforeEach
    void setUp() {
        car = new Car(UUID.randomUUID(), "TEST", "TEST", "TEST", "TEST", LocalDateTime.now(),
                "TEST", "TEST", "TEST", null, null);
        image = new Image(UUID.randomUUID(), "TEST",  car);
    }

    @Test
    void createTest() throws ImageCreationException {
        //Init
        when(imageRepository.save(image)).thenReturn(image);

        //When
        Image createdImage = imageService.create(image);

        //Then
        assertNotNull(createdImage, "Image is not created");
        assertEquals(image.getUrl(), createdImage.getUrl());
        assertEquals(image.getCar().getModel(), createdImage.getCar().getModel());
        assertEquals(image.getCar().getNumber(), createdImage.getCar().getNumber());
        assertEquals(image.getCar().getVin(), createdImage.getCar().getVin());
        assertEquals(image.getCar().getBrand(), createdImage.getCar().getBrand());
        verify(imageRepository, times(1)).save(image);

    }

    @Test
    void createIfImageIsNullTest() {
        //Init
        Throwable exception = assertThrows(ImageCreationException.class, () -> imageService.create(null));

        //Then
        assertEquals("Check your image data.", exception.getMessage());
        verify(imageRepository, times(0)).save(image);
    }

    @Test
    void getByIdTest() throws ImageNotFoundException {
        //Init
        when(imageRepository.existsById(image.getId())).thenReturn(true);
        when(imageRepository.findById(image.getId())).thenReturn(Optional.ofNullable(image));

        //When
        Image imageById = imageService.getById(image.getId());

        //Then
        assertNotNull(imageById, "Image not found.");
        assertEquals(image.getUrl(), imageById.getUrl());
        assertEquals(image.getCar().getModel(), imageById.getCar().getModel());
        assertEquals(image.getCar().getNumber(), imageById.getCar().getNumber());
        assertEquals(image.getCar().getVin(), imageById.getCar().getVin());
        assertEquals(image.getCar().getBrand(), imageById.getCar().getBrand());
        verify(imageRepository, times(1)).existsById(image.getId());
        verify(imageRepository, times(1)).findById(image.getId());
    }

    @Test
    void getByIdIfImageNotExistTest() {
        //Init
        when(imageRepository.existsById(image.getId())).thenReturn(false);

        //Then
        assertThrows(ImageNotFoundException.class, () -> imageService.getById(image.getId()));
        verify(imageRepository, times(1)).existsById(image.getId());
        verify(imageRepository, times(0)).findById(image.getId());
    }

    @Test
    void updateTest() throws ImageNotFoundException {
        //Init
        image.setUrl("TEST_TEST");
        when(imageRepository.existsById(image.getId())).thenReturn(true);
        when(imageRepository.save(image)).thenReturn(image);

        //When
        Image updatedImage = imageService.update(image);

        //Then
        assertNotNull(updatedImage, "Image is not updated.");
        assertEquals("TEST_TEST", updatedImage.getUrl());
        verify(imageRepository, times(1)).existsById(image.getId());
        verify(imageRepository, times(1)).save(image);
    }

    @Test
    void updateIfImageNotExistTest() {
        //Init
        when(imageRepository.existsById(image.getId())).thenReturn(false);

        //Then
        assertThrows(ImageNotFoundException.class, () -> imageService.update(image));
        verify(imageRepository, times(1)).existsById(image.getId());
        verify(imageRepository, times(0)).save(image);
    }

    @Test
    void deleteTest() throws ImageNotFoundException {
        //Init
        when(imageRepository.existsById(image.getId())).thenReturn(true);
        doNothing().when(imageRepository).deleteById(image.getId());

        //When
        UUID deletedImageId = imageService.delete(image.getId());

        //Then
        assertNotNull(deletedImageId, "Image is not updated.");
        assertEquals(image.getId(), deletedImageId);
        verify(imageRepository, times(1)).existsById(image.getId());
        verify(imageRepository, times(1)).deleteById(image.getId());
    }

    @Test
    void deleteIfImageNotExistTest() {
        //Init
        when(imageRepository.existsById(image.getId())).thenReturn(false);

        //Then
        assertThrows(ImageNotFoundException.class, () -> imageService.delete(image.getId()));
        verify(imageRepository, times(1)).existsById(image.getId());
        verify(imageRepository, times(0)).deleteById(image.getId());
    }
}