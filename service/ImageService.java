package carshare.service;

import carshare.advice.exception.ImageCreationException;
import carshare.advice.exception.ImageNotFoundException;
import carshare.database.entity.Image;
import carshare.database.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Service for image management
 */
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(final ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * Method accepts image data and save it to database
     *
     * @param image                                 Image data
     * @throws ImageCreationException               if image is not created
     */
    @Transactional
    public Image create(final Image image) throws ImageCreationException {
        if (image == null) {
            throw new ImageCreationException("Check your image data.");
        }
        return imageRepository.save(image);
    }

    /**
     * Method accepts UUID of image and return image by UUID
     *
     * @param imageId                               UUID of image data
     * @return                                      Image data
     * @throws ImageNotFoundException               if image not found
     */
    public Image getById(final UUID imageId) throws ImageNotFoundException {
        if (!imageRepository.existsById(imageId)){
            throw new ImageNotFoundException("Image not exists.");
        }
        return imageRepository.findById(imageId).orElse(new Image());
    }

    /**
     * Method return list of all images from database
     *
     * @return                                      List of all images
     */
    public List<Image> getAll() {
        return new ArrayList<>((Collection<? extends Image>) imageRepository.findAll());
    }

    /**
     * Method accepts image data change fields and rewrite it to database
     *
     * @param image                                 Image data
     */
    @Transactional
    public Image update(final Image image) throws ImageNotFoundException {
        if (!imageRepository.existsById(image.getId())){
            throw new ImageNotFoundException("Image not exists.");
        }
        return imageRepository.save(image);
    }

    /**
     * Method accepts UUID of image and delete it from database
     *
     * @param imageId                               UUID of image data
     * @throws ImageNotFoundException               if image not found
     */
    @Transactional
    public UUID delete(final UUID imageId) throws ImageNotFoundException {
        if (!imageRepository.existsById(imageId)){
            throw new ImageNotFoundException("Image not exists.");
        }
        imageRepository.deleteById(imageId);
        return imageId;
    }
}
