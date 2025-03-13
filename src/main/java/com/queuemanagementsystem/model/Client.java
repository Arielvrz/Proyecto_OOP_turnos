package com.queuemanagementsystem.model;

import java.util.Objects;

/**
 * Represents a client who requests service and gets a ticket.
 */
public class Client {
    private String id;
    private String name;
    private String contactInfo;

    /**
     * Default constructor
     */
    public Client() {
    }

    /**
     * Parameterized constructor
     *
     * @param id Client's unique identifier
     * @param name Client's name
     */
    public Client(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Complete constructor with all fields
     *
     * @param id Client's unique identifier
     * @param name Client's name
     * @param contactInfo Client's contact information
     */
    public Client(String id, String name, String contactInfo) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    /**
     * Requests a ticket for a specific service category
     *
     * @param category The service category
     * @return A message indicating the result of the request
     */
    public String requestTicket(Category category) {
        // Note: In the actual implementation, this would interact with a service
        if (category == null) {
            return "Invalid category selected.";
        }

        if (!category.isActive()) {
            return "The selected service category is currently not available.";
        }

        return "Ticket request for category " + category.getName() + " has been created.";
    }

    /**
     * Checks the status of the queue for a specific category
     *
     * @param category The service category to check
     * @return A string describing the current queue status
     */
    public String checkQueueStatus(Category category) {
        // Note: In the actual implementation, this would query the service
        if (category == null) {
            return "Invalid category selected.";
        }

        return "Queue status for " + category.getName() +
                ":\nPending tickets: " + category.countPendingTickets();
    }

    // The cancelTicket functionality has been removed as per requirements

    /**
     * Receives an alert notification
     *
     * @param message The alert message
     * @return true indicating the alert was received
     */
    public boolean receiveAlert(String message) {
        // In a real system, this might send an SMS, push notification, etc.
        System.out.println("ALERT for client " + name + ": " + message);
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

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    /**
     * Compares this Client to another object for equality
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    /**
     * Generates a hash code for this Client
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this Client
     *
     * @return A string representation
     */
    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                '}';
    }
}