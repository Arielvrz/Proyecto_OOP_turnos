package com.queuemanagementsystem.repository;

import com.google.gson.reflect.TypeToken;
import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.util.JsonFileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CategoryRepository using JSON file for persistence.
 */
public class JsonCategoryRepository implements CategoryRepository {
    private static final String FILE_PATH = "data/categories.json";
    private List<Category> categories;

    /**
     * Default constructor. Loads categories from file.
     */
    public JsonCategoryRepository() {
        this.categories = new ArrayList<>();
        loadAll();
    }

    @Override
    public Category save(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        // Check if the category already exists
        Optional<Category> existingCategory = findById(category.getId());
        if (existingCategory.isPresent()) {
            // Update existing category
            categories.remove(existingCategory.get());
        }

        categories.add(category);
        saveAll();
        return category;
    }

    @Override
    public Optional<Category> findById(int id) {
        return categories.stream()
                .filter(category -> category.getId() == id)
                .findFirst();
    }

    @Override
    public List<Category> findAll() {
        return new ArrayList<>(categories);
    }

    @Override
    public List<Category> findAllActive() {
        return categories.stream()
                .filter(Category::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Category> category = findById(id);
        if (category.isPresent()) {
            boolean removed = categories.remove(category.get());
            if (removed) {
                saveAll();
            }
            return removed;
        }
        return false;
    }

    @Override
    public Optional<Category> findByPrefix(String prefix) {
        if (prefix == null) {
            return Optional.empty();
        }
        return categories.stream()
                .filter(category -> prefix.equals(category.getPrefix()))
                .findFirst();
    }

    @Override
    public boolean update(Category category) {
        if (category == null) {
            return false;
        }

        Optional<Category> existingCategory = findById(category.getId());
        if (!existingCategory.isPresent()) {
            return false;
        }

        categories.remove(existingCategory.get());
        categories.add(category);
        return saveAll();
    }

    @Override
    public boolean saveAll() {
        return JsonFileHandler.saveToFile(categories, FILE_PATH);
    }

    @Override
    public boolean loadAll() {
        TypeToken<List<Category>> typeToken = new TypeToken<List<Category>>() {};
        this.categories = JsonFileHandler.loadFromFile(FILE_PATH, typeToken.getType());
        return true;
    }
}