package com.queuemanagementsystem.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a ticket for client service.
 */
public class Ticket {
    private String code;
    private String status; // "WAITING", "IN_PROGRESS", "COMPLETED"
    private Category category;
    private String clientId;
    private LocalDateTime generationTime;
    private LocalDateTime attentionTime;
    private LocalDateTime completionTime;

    /**
     * Default constructor
     */
    public Ticket() {
        this.generationTime = LocalDateTime.now();
        this.status = "WAITING";
    }

    /**
     * Parameterized constructor with essential fields
     *
     * @param category The service category
     * @param clientId The client's ID
     */
    public Ticket(Category category, String clientId) {
        this.category = category;
        this.clientId = clientId;
        this.generationTime = LocalDateTime.now();
        this.status = "WAITING";
        this.code = generateCode();
    }

    /**
     * Complete constructor with all fields
     *
     * @param code Ticket's unique code
     * @param status Current status of the ticket
     * @param category Service category
     * @param clientId Client's ID
     * @param generationTime Time when the ticket was generated
     * @param attentionTime Time when service began
     * @param completionTime Time when service was completed
     */
    public Ticket(String code, String status, Category category, String clientId,
                  LocalDateTime generationTime, LocalDateTime attentionTime,
                  LocalDateTime completionTime) {
        this.code = code;
        this.status = status;
        this.category = category;
        this.clientId = clientId;
        this.generationTime = generationTime;
        this.attentionTime = attentionTime;
        this.completionTime = completionTime;
    }

    /**
     * Generates a unique code for the ticket
     *
     * @return The generated code
     */
    private String generateCode() {
        // Format: Category prefix + sequential number
        // In a real system, this would use a more sophisticated approach
        return (category != null ? category.getPrefix() : "GEN") + "-" +
                System.currentTimeMillis() % 1000;
    }

    /**
     * Changes the status of the ticket
     *
     * @param newStatus The new status
     * @return true if the status was changed successfully, false otherwise
     */
    public boolean changeStatus(String newStatus) {
        if (isValidStatusTransition(newStatus)) {
            this.status = newStatus;

            // Update timestamps based on the new status
            if ("IN_PROGRESS".equals(newStatus)) {
                this.attentionTime = LocalDateTime.now();
            } else if ("COMPLETED".equals(newStatus)) {
                this.completionTime = LocalDateTime.now();
            }

            return true;
        }
        return false;
    }

    /**
     * Checks if a status transition is valid
     *
     * @param newStatus The new status
     * @return true if the transition is valid, false otherwise
     */
    private boolean isValidStatusTransition(String newStatus) {
        if (newStatus == null) {
            return false;
        }

        switch (this.status) {
            case "WAITING":
                return "IN_PROGRESS".equals(newStatus);
            case "IN_PROGRESS":
                return "COMPLETED".equals(newStatus);
            case "COMPLETED":
                return false; // Terminal state
            default:
                return false;
        }
    }

    /**
     * Calculates the waiting time in minutes
     *
     * @return The waiting time in minutes
     */
    public long calculateWaitingTime() {
        if (attentionTime != null) {
            return Duration.between(generationTime, attentionTime).toMinutes();
        } else if ("WAITING".equals(status)) {
            return Duration.between(generationTime, LocalDateTime.now()).toMinutes();
        }
        return 0;
    }

    /**
     * Calculates the service time in minutes
     *
     * @return The service time in minutes
     */
    public long calculateServiceTime() {
        if (attentionTime != null && completionTime != null) {
            return Duration.between(attentionTime, completionTime).toMinutes();
        } else if ("IN_PROGRESS".equals(status) && attentionTime != null) {
            return Duration.between(attentionTime, LocalDateTime.now()).toMinutes();
        }
        return 0;
    }

    /**
     * Generates a visual alert for the client
     *
     * @return The alert message
     */
    public String generateVisualAlert() {
        return "ATTENTION: Ticket " + code + " is now being served at station " +
                (category != null ? category.getName() : "general service");
    }

    // Getters and Setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(LocalDateTime generationTime) {
        this.generationTime = generationTime;
    }

    public LocalDateTime getAttentionTime() {
        return attentionTime;
    }

    public void setAttentionTime(LocalDateTime attentionTime) {
        this.attentionTime = attentionTime;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    /**
     * Compares this Ticket to another object for equality
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(code, ticket.code);
    }

    /**
     * Generates a hash code for this Ticket
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    /**
     * Returns a string representation of this Ticket
     *
     * @return A string representation
     */
    @Override
    public String toString() {
        return "Ticket{" +
                "code='" + code + '\'' +
                ", status='" + status + '\'' +
                ", category=" + (category != null ? category.getName() : "None") +
                ", clientId='" + clientId + '\'' +
                ", generationTime=" + generationTime +
                ", waitingTime=" + calculateWaitingTime() + " minutes" +
                '}';
    }
}