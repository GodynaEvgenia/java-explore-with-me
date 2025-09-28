package ru.practicum.category;

import org.springframework.stereotype.Service;
import ru.practicum.exceptions.ResourceConflictException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Получить все категории
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Получить категорию по id
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    // Добавить новую категорию
    public Category createCategory(Category category) {
        boolean nameExists = categoryRepository.existsByName(category.getName());
        if (nameExists) {
            throw new ResourceConflictException("Category name must be unique");
        }
        return categoryRepository.save(category);
    }

    // Обновить категорию (по id)
    public Optional<Category> updateCategory(Long id, Category updatedCategory) {
        boolean nameExists = categoryRepository.existsByNameAndIdNot(updatedCategory.getName(), id);
        if (nameExists) {
            throw new ResourceConflictException("Category name must be unique");
        }
        return categoryRepository.findById(id).map(category -> {
            category.setName(updatedCategory.getName());
            return categoryRepository.save(category);
        });
    }

    // Удалить категорию по id
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}