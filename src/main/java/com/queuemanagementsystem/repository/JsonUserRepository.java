package com.queuemanagementsystem.repository;

import com.google.gson.reflect.TypeToken;
import com.queuemanagementsystem.model.User;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Administrator;
import com.queuemanagementsystem.util.JsonFileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of UserRepository using JSON file for persistence.
 */
public class JsonUserRepository implements UserRepository {
    private static final String FILE_PATH = "data/users.json";
    private List<User> users;

    /**
     * Default constructor. Loads users from file.
     */
    public JsonUserRepository() {
        this.users = new ArrayList<>();
        loadAll();
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Check if the user already exists
        Optional<User> existingUser = findById(user.getId());
        if (existingUser.isPresent()) {
            // Update existing user
            users.remove(existingUser.get());
        }

        users.add(user);
        saveAll();
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public boolean deleteById(String id) {
        Optional<User> user = findById(id);
        if (user.isPresent()) {
            boolean removed = users.remove(user.get());
            if (removed) {
                saveAll();
            }
            return removed;
        }
        return false;
    }

    @Override
    public Optional<User> authenticate(String id, String password) {
        if (id == null || password == null) {
            return Optional.empty();
        }

        return users.stream()
                .filter(user -> user.getId().equals(id))
                .filter(user -> user.login(id, password))
                .findFirst();
    }

    @Override
    public boolean saveAll() {
        return JsonFileHandler.saveToFile(users, FILE_PATH);
    }

    @Override
    public boolean loadAll() {
        TypeToken<List<User>> typeToken = new TypeToken<List<User>>() {};
        this.users = JsonFileHandler.loadFromFile(FILE_PATH, typeToken.getType());

        // Debug logging
        System.out.println("Debug - Loaded " + users.size() + " users from " + FILE_PATH);
        for (User user : users) {
            System.out.println("Debug - Loaded user: " + user.getId() + " - " + user.getClass().getSimpleName());
        }

        return true;
    }

    @Override
    public boolean update(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }

        Optional<User> existingUser = findById(user.getId());
        if (!existingUser.isPresent()) {
            return false;
        }

        users.remove(existingUser.get());
        users.add(user);
        return saveAll();
    }

    @Override
    public boolean delete(String id) {
        return deleteById(id);
    }

    @Override
    public List<Employee> findAllEmployees() {
        return users.stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());
    }

    @Override
    public List<Administrator> findAllAdministrators() {
        return users.stream()
                .filter(user -> user instanceof Administrator)
                .map(user -> (Administrator) user)
                .collect(Collectors.toList());
    }
}