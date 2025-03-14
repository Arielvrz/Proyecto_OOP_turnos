package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.User;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Administrator;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User data access operations.
 */
public interface UserRepository {
    /**
     * Saves a user to the repository
     *
     * @param user The user to save
     * @return The saved user
     */
    User save(User user);

    /**
     * Finds a user by their ID
     *
     * @param id The user ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(String id);

    /**
     * Gets all users in the repository
     *
     * @return List of all users
     */
    List<User> findAll();

    /**
     * Deletes a user by their ID
     *
     * @param id The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteById(String id);

    /**
     * Authenticates a user with provided credentials
     *
     * @param id The user ID
     * @param password The user password
     * @return Optional containing the authenticated user if successful, empty otherwise
     */
    Optional<User> authenticate(String id, String password);

    /**
     * Saves all users to persistent storage
     *
     * @return true if save was successful, false otherwise
     */
    boolean saveAll();

    /**
     * Loads all users from persistent storage
     *
     * @return true if load was successful, false otherwise
     */
    boolean loadAll();

    /**
     * Updates an existing user
     *
     * @param user The user with updated information
     * @return true if update was successful, false otherwise
     */
    boolean update(User user);

    /**
     * Deletes a user
     *
     * @param id The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(String id);

    /**
     * Finds all employees in the repository
     *
     * @return A list of all employees
     */
    List<Employee> findAllEmployees();

    /**
     * Finds all administrators in the repository
     *
     * @return A list of all administrators
     */
    List<Administrator> findAllAdministrators();
}