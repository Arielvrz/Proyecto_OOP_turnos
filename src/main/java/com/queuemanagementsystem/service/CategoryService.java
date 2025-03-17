package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing service categories.
 */
public class CategoryService {
    private final CategoryRepository categoryRepository;

    /**
     * Constructor with repository dependency
     *
     * @param categoryRepository Repository for category data
     */
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new service category
     *
     * @param name Category name
     * @param description Category description
     * @param prefix Prefix for ticket codes
     * @return The created category if successful, null otherwise
     */
    public Category createCategory(String name, String description, String prefix) {
        // Check if a category with the same prefix already exists
        boolean prefixExists = categoryRepository.findAll().stream()
                .anyMatch(c -> prefix.equals(c.getPrefix()));

        if (prefixExists) {
            return null; // Prefix must be unique
        }

        // Generate a new ID (in a real system, this might be handled differently)
        int newId = getNextCategoryId();

        Category category = new Category(newId, name, description, prefix, true);

        // Replace this line:
        // if (categoryRepository.save(category)) {
        //     return category;
        // }

        // With this:
        Category savedCategory = categoryRepository.save(category);
        if (savedCategory != null) {
            return savedCategory;
        }

        return null;
    }

    /**
     * Updates an existing category
     *
     * @param category The category with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateCategory(Category category) {
        if (category == null || !categoryRepository.findById(category.getId()).isPresent()) {
            return false;
        }

        // Check if updating the prefix would create a duplicate
        if (categoryRepository.findAll().stream()
                .anyMatch(c -> category.getPrefix().equals(c.getPrefix()) && c.getId() != category.getId())) {
            return false;
        }

        return categoryRepository.update(category);
    }

    /**
     * Activates a category
     *
     * @param categoryId The ID of the category to activate
     * @return true if the activation was successful, false otherwise
     */
    public boolean activateCategory(int categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return false;
        }

        Category category = categoryOpt.get();
        category.activate();

        return categoryRepository.update(category);
    }

    /**
     * Deactivates a category
     *
     * @param categoryId The ID of the category to deactivate
     * @return true if the deactivation was successful, false otherwise
     */
    public boolean deactivateCategory(int categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return false;
        }

        Category category = categoryOpt.get();
        category.deactivate();

        return categoryRepository.update(category);
    }

    /**
     * Assigns an employee to a category
     *
     * @param categoryId The category ID
     * @param employee The employee to assign
     * @return true if the assignment was successful, false otherwise
     */
    public boolean assignEmployeeToCategory(int categoryId, Employee employee) {
        if (employee == null) {
            return false;
        }

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return false;
        }

        Category category = categoryOpt.get();
        if (category.assignEmployee(employee)) {
            return categoryRepository.update(category);
        }

        return false;
    }

    /**
     * Removes an employee from a category
     *
     * @param categoryId The category ID
     * @param employee The employee to remove
     * @return true if the removal was successful, false otherwise
     */
    public boolean removeEmployeeFromCategory(int categoryId, Employee employee) {
        if (employee == null) {
            return false;
        }

        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return false;
        }

        Category category = categoryOpt.get();
        if (category.removeEmployee(employee)) {
            return categoryRepository.update(category);
        }

        return false;
    }

    /**
     * Gets all active categories
     *
     * @return List of active categories
     */
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAll().stream()
                .filter(Category::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Gets all categories (active and inactive)
     *
     * @return List of all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Gets a category by ID
     *
     * @param categoryId The category ID
     * @return Optional containing the category if found, empty otherwise
     */
    public Optional<Category> getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId);
    }

    /**
     * Gets a category by prefix
     *
     * @param prefix The category prefix
     * @return Optional containing the category if found, empty otherwise
     */
    public Optional<Category> getCategoryByPrefix(String prefix) {
        return categoryRepository.findAll().stream()
                .filter(c -> prefix.equals(c.getPrefix()))
                .findFirst();
    }

    /**
     * Generates the next available category ID
     *
     * @return The next available ID
     */
    private int getNextCategoryId() {
        return categoryRepository.findAll().stream()
                .mapToInt(Category::getId)
                .max()
                .orElse(0) + 1;
    }
}