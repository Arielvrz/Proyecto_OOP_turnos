package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Administrator;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.User;
import com.queuemanagementsystem.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing users (employees and administrators).
 */
public class UserService {
    private final UserRepository userRepository;

    /**
     * Constructor with repository dependency
     *
     * @param userRepository The repository for user data
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a user with the provided credentials
     *
     * @param id User ID
     * @param password User password
     * @return The authenticated User object if successful, null otherwise
     */
    public User authenticate(String id, String password) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent() && user.get().login(id, password)) {
            return user.get();
        }

        return null;
    }

    /**
     * Finds a user by ID without authentication
     *
     * @param id User ID
     * @return Optional containing the user if found
     */
    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }

    /**
     * Registers a new employee in the system
     *
     * @param employee The employee to register
     * @return true if registration was successful, false otherwise
     */
    public boolean registerEmployee(Employee employee) {
        if (employee == null || employee.getId() == null || userRepository.findById(employee.getId()).isPresent()) {
            return false;
        }

        return userRepository.save(employee).logout();
    }

    /**
     * Registers a new administrator in the system
     *
     * @param administrator The administrator to register
     * @return true if registration was successful, false otherwise
     */
    public boolean registerAdministrator(Administrator administrator) {
        if (administrator == null || administrator.getId() == null ||
                userRepository.findById(administrator.getId()).isPresent()) {
            return false;
        }

        return userRepository.save(administrator).logout();
    }

    /**
     * Updates an existing user's information
     *
     * @param user The user with updated information
     * @return true if the update was successful, false otherwise
     */
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null || !userRepository.findById(user.getId()).isPresent()) {
            return false;
        }

        return userRepository.update(user);
    }

    /**
     * Deletes a user from the system
     *
     * @param userId The ID of the user to delete
     * @return true if the deletion was successful, false otherwise
     */
    public boolean deleteUser(String userId) {
        return userRepository.delete(userId);
    }

    /**
     * Gets all employees registered in the system
     *
     * @return List of all employees
     */
    public List<Employee> getAllEmployees() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());
    }

    /**
     * Gets all administrators registered in the system
     *
     * @return List of all administrators
     */
    public List<Administrator> getAllAdministrators() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Administrator)
                .map(user -> (Administrator) user)
                .collect(Collectors.toList());
    }

    /**
     * Gets an employee by ID
     *
     * @param employeeId The employee's ID
     * @return Optional containing the employee if found, empty otherwise
     */
    public Optional<Employee> getEmployeeById(String employeeId) {
        Optional<User> user = userRepository.findById(employeeId);

        if (user.isPresent() && user.get() instanceof Employee) {
            return Optional.of((Employee) user.get());
        }

        return Optional.empty();
    }

    /**
     * Gets an administrator by ID
     *
     * @param adminId The administrator's ID
     * @return Optional containing the administrator if found, empty otherwise
     */
    public Optional<Administrator> getAdministratorById(String adminId) {
        Optional<User> user = userRepository.findById(adminId);

        if (user.isPresent() && user.get() instanceof Administrator) {
            return Optional.of((Administrator) user.get());
        }

        return Optional.empty();
    }
}