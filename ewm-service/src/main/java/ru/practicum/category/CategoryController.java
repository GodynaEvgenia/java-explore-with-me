package ru.practicum.category;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.exceptions.ResourceConflictException;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Получить все категории
    @GetMapping("/categories")
    public List<CategoryDto> getCategories(
            @RequestParam(value = "from", defaultValue = "0") int from,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return categoryService.getCategories(from, size);
    }

    // Получить категорию по id
    @GetMapping("categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Добавить новую категорию
    @PostMapping("/admin/categories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        try {
            Category createdCategory = categoryService.createCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (ResourceConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(category);
        }
    }

    // Обновить категорию по id
    @PatchMapping("/admin/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        try {
            Category updated = categoryService.updateCategory(id, category)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
            return ResponseEntity.ok(updated);
        } catch (ResourceConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(category);
        }

    }

    // Удалить категорию по id
    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(id);
        }
    }
}
