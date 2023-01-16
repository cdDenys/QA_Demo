package carshare.service;

import carshare.database.entity.Category;
import carshare.database.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Service for category management
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Method accepts category data and save it to database
     *
     * @param category                      Category data
     */
    @Transactional
    public Category create(final Category category) {
        if (category == null) {
            return null;
        }
        return categoryRepository.save(category);
    }

    /**
     * Method accepts UUID of category and return category by UUID
     *
     * @param categoryId                    UUID of category data
     * @return                              Category data
     */
    public Category getById(final UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)){
            return null;
        }
        return categoryRepository.findById(categoryId).orElse(new Category());
    }

    /**
     * Method return list of all categories from database
     *
     * @return                              List of all categories
     */
    public List<Category> getAll(){
        return new ArrayList<>((Collection<? extends Category>) categoryRepository.findAll());
    }

    /**
     * Method accepts category data change fields and rewrite it to database
     *
     * @param category                      Category data
     */
    @Transactional
    public Category update(final Category category) {
        if (!categoryRepository.existsById(category.getId())){
            return null;
        }
        return categoryRepository.save(category);
    }

    /**
     * Method accepts UUID of category and delete it from database
     *
     * @param categoryId                    UUID of category data
     */
    @Transactional
    public UUID delete(final UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)){
            return null;
        }
        categoryRepository.deleteById(categoryId);
        return categoryId;
    }
}
