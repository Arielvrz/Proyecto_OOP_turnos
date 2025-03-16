package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.Category;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category data access operations.
 */
public interface CategoryRepository {
    /**
     * Saves a category to the repository
     *
     * @param category The category to save
     * @return The saved category
     */
    Category save(Category category);

    /**
     * Finds a category by its ID
     *
     * @param id The category ID to search for
     * @return Optional containing the category if found, empty otherwise
     */
    Optional<Category> findById(int id);

    /**
     * Gets all categories in the repository
     *
     * @return List of all categories
     */
    List<Category> findAll();

    /**
     * Gets all active categories in the repository
     *
     * @return List of active categories
     */
    List<Category> findAllActive();

    /**
     * Deletes a category by its ID
     *
     * @param id The ID of the category to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteById(int id);

    /**
     * Finds a category by its prefix
     *
     * @param prefix The category prefix to search for
     * @return Optional containing the category if found, empty otherwise
     */
    Optional<Category> findByPrefix(String prefix);

    /**
     * Updates an existing category
     *
     * @param category The category with updated information
     * @return true if update was successful, false otherwise
     */
    boolean update(Category category);

    /**
     * Saves all categories to persistent storage
     *
     * @return true if save was successful, false otherwise
     */
    boolean saveAll();

    /**
     * Loads all categories from persistent storage
     *
     * @return true if load was successful, false otherwise
     */
    boolean loadAll();
}