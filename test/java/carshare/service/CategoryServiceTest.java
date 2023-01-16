package carshare.service;

import carshare.database.entity.Category;
import carshare.database.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CategoryServiceTest {

    @MockBean
    private CategoryRepository categoryRepository;

    private final CategoryService categoryService;

    @Autowired
    public CategoryServiceTest(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    static Category category;

    @BeforeEach
    void setUp() {
        category = new Category(UUID.randomUUID(), "B2", "Regular");
    }

    @Test
    void createTest() {
        //Init
        category.setDescription("TEST");
        when(categoryRepository.save(category)).thenReturn(category);

        //When
        Category createdCategory = categoryService.create(category);

        //Then
        assertNotNull(createdCategory, "Category is not created.");
        assertEquals(category.getName(), createdCategory.getName());
        assertEquals(category.getDescription(), createdCategory.getDescription());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void createIfCategoryIsNullTest() {
        assertNull(categoryService.create(null));
        verify(categoryRepository, times(0)).save(category);
    }

    @Test
    void getByIdTest() {
        //Init
        when(categoryRepository.existsById(category.getId())).thenReturn(true);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.ofNullable(category));

        //When
        Category categoryById = categoryService.getById(category.getId());

        //Then
        assertNotNull(categoryById, "Category not found.");
        assertEquals(category.getName(), categoryById.getName());
        assertEquals(category.getDescription(), categoryById.getDescription());
        verify(categoryRepository, times(1)).findById(category.getId());
    }

    @Test
    void getByIdIfCategoryNotExistTest() {
        //Init
        when(categoryRepository.existsById(category.getId())).thenReturn(false);

        //Then
        assertNull(categoryService.getById(category.getId()));
        verify(categoryRepository, times(0)).findById(category.getId());
    }

    @Test
    void updateTest() {
        //Init
        category.setName("NEW TEST");
        category.setDescription("NEW TEST");
        when(categoryRepository.existsById(category.getId())).thenReturn(true);
        when(categoryRepository.save(category)).thenReturn(category);

        //When
        Category updatedCategory = categoryService.update(category);

        //Then
        assertNotNull(updatedCategory, "Category is not updated.");
        assertEquals(category.getName(), updatedCategory.getName());
        assertEquals(category.getDescription(), updatedCategory.getDescription());
        verify(categoryRepository, times(1)).existsById(category.getId());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateIfCategoryNotExistTest() {
        //Init
        when(categoryRepository.existsById(category.getId())).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);

        //Then
        assertNull(categoryService.update(category));
        verify(categoryRepository, times(1)).existsById(category.getId());
        verify(categoryRepository, times(0)).save(category);
    }

    @Test
    void deleteTest() {
        //Init
        when(categoryRepository.existsById(category.getId())).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(category.getId());

        //When
        UUID deletedCategoryId = categoryService.delete(category.getId());

        //Then
        assertNotNull(deletedCategoryId, "Category is not deleted.");
        assertEquals(category.getId(), deletedCategoryId);
        verify(categoryRepository, times(1)).existsById(category.getId());
        verify(categoryRepository, times(1)).deleteById(category.getId());
    }

    @Test
    void deleteIfCategoryNotExistTest() {
        //Init
        when(categoryRepository.existsById(category.getId())).thenReturn(false);

        //Then
        assertNull(categoryService.delete(category.getId()));
        verify(categoryRepository, times(1)).existsById(category.getId());
        verify(categoryRepository, times(0)).deleteById(category.getId());
    }
}