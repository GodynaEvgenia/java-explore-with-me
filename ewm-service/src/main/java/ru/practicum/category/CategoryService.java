package ru.practicum.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.events.EventRepository;
import ru.practicum.exceptions.ResourceConflictException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
        this.eventRepository = eventRepository;
    }

    // Получить все категории
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<CategoryDto> getCategories(int from, int size) {
        int pageNumber = from / size; // расчет номера страницы
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Category> page = categoryRepository.findAll(pageable);

        return page.getContent().stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getName()))
                .collect(Collectors.toList());
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
        if (eventRepository.existsByCategoryId(id)) {
            throw new ResourceConflictException("На категорию ссылается событие");
        }
        categoryRepository.deleteById(id);
    }
}