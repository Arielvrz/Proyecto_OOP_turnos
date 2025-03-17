package com.queuemanagementsystem.model;

import java.util.Objects;

/**
 * Base class representing any user that interacts with the system.
 * This serves as the parent class for Employee and Administrator.
 */
public class User {
    private String id;
    private String name;
    private String password;

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Parameterized constructor
     *
     * @param id User's unique identifier
     * @param name User's full name
     * @param password User's authentication password
     */
    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /**
     * Authenticates a user with the provided credentials
     *
     * @param providedId The ID entered by the user
     * @param providedPassword The password entered by the user
     * @return true if authentication is successful, false otherwise
     */
    public boolean login(String providedId, String providedPassword) {
        boolean matches = this.id.equals(providedId) && this.password.equals(providedPassword);
        System.out.println("Debug - Login attempt: " + providedId);
        System.out.println("Debug - ID match: " + this.id.equals(providedId));
        System.out.println("Debug - Password match: " + this.password.equals(providedPassword));
        return matches;
    }

    /**
     * Simulates user logout functionality
     *
     * @return true indicating successful logout
     */
    public boolean logout() {
        // In a real system, this would invalidate sessions, etc.
        return true;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Note: We don't provide a getter for password for security reasons
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Compares this User to another object for equality
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    /**
     * Generates a hash code for this User
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this User
     *
     * @return A string representation
     */
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}