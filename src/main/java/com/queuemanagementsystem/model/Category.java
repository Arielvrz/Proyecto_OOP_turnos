package com.queuemanagementsystem.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * Represents a service category that clients can request.
 */
public class Category {
    private int id;
    private String name;
    private String description;
    private String prefix;
    private boolean active;
    // Make ticketQueue transient to exclude it from serialization
    private transient Queue<Ticket> ticketQueue;
    private List<Employee> assignedEmployees;

    /**
     * Default constructor
     */
    public Category() {
        this.ticketQueue = new LinkedList<>();
        this.assignedEmployees = new ArrayList<>();
        this.active = true;
    }

    /**
     * Parameterized constructor with essential fields
     *
     * @param id Category's unique identifier
     * @param name Category's name
     * @param prefix Category's prefix for ticket codes
     */
    public Category(int id, String name, String prefix) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.active = true;
        this.ticketQueue = new LinkedList<>();
        this.assignedEmployees = new ArrayList<>();
    }

    /**
     * Complete constructor with all fields
     *
     * @param id Category's unique identifier
     * @param name Category's name
     * @param description Category's description
     * @param prefix Category's prefix for ticket codes
     * @param active Whether the category is active
     */
    public Category(int id, String name, String description, String prefix, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.prefix = prefix;
        this.active = active;
        this.ticketQueue = new LinkedList<>();
        this.assignedEmployees = new ArrayList<>();
    }

    /**
     * Adds a ticket to the queue
     *
     * @param ticket The ticket to add
     * @return true if the ticket was added successfully, false otherwise
     */
    public boolean addTicketToQueue(Ticket ticket) {
        if (ticket != null && active) {
            // Make sure the ticketQueue is initialized
            if (ticketQueue == null) {
                ticketQueue = new LinkedList<>();
            }
            return ticketQueue.offer(ticket);
        }
        return false;
    }

    /**
     * Gets the next ticket in the queue
     *
     * @return The next ticket or null if the queue is empty
     */
    public Ticket getNextTicket() {
        // Make sure the ticketQueue is initialized
        if (ticketQueue == null) {
            ticketQueue = new LinkedList<>();
        }
        return ticketQueue.poll();
    }

    /**
     * Counts the number of pending tickets in the queue
     *
     * @return The number of pending tickets
     */
    public int countPendingTickets() {
        // Make sure the ticketQueue is initialized
        if (ticketQueue == null) {
            ticketQueue = new LinkedList<>();
        }
        return ticketQueue.size();
    }

    /**
     * Activates the category, allowing new tickets to be added
     *
     * @return true indicating the category was activated
     */
    public boolean activate() {
        this.active = true;
        return true;
    }

    /**
     * Deactivates the category, preventing new tickets from being added
     *
     * @return true indicating the category was deactivated
     */
    public boolean deactivate() {
        this.active = false;
        return true;
    }

    /**
     * Assigns an employee to this category
     *
     * @param employee The employee to assign
     * @return true if the employee was assigned successfully, false otherwise
     */
    public boolean assignEmployee(Employee employee) {
        if (employee != null && !assignedEmployees.contains(employee)) {
            return assignedEmployees.add(employee);
        }
        return false;
    }

    /**
     * Removes an employee from this category
     *
     * @param employee The employee to remove
     * @return true if the employee was removed successfully, false otherwise
     */
    public boolean removeEmployee(Employee employee) {
        return assignedEmployees.remove(employee);
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Employee> getAssignedEmployees() {
        return new ArrayList<>(assignedEmployees);  // Return a copy to maintain encapsulation
    }

    /**
     * Peek at the tickets in the queue without removing them
     *
     * @return A list of tickets in the queue
     */
    public List<Ticket> peekTicketQueue() {
        // Make sure the ticketQueue is initialized
        if (ticketQueue == null) {
            ticketQueue = new LinkedList<>();
        }
        return new ArrayList<>(ticketQueue);  // Return a copy to maintain encapsulation
    }

    /**
     * Compares this Category to another object for equality
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    /**
     * Generates a hash code for this Category
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this Category
     *
     * @return A string representation
     */
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", prefix='" + prefix + '\'' +
                ", active=" + active +
                ", pendingTickets=" + (ticketQueue != null ? ticketQueue.size() : 0) +
                ", assignedEmployees=" + assignedEmployees.size() +
                '}';
    }
}