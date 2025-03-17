package com.queuemanagementsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an employee who attends to clients according to their turns.
 * Extends the base User class.
 */
public class Employee extends User {
    private String availabilityStatus; // "AVAILABLE", "BUSY", "PAUSED", "OFFLINE"
    private transient Station assignedStation; // Add transient here
    private List<Ticket> attendedTickets;

    /**
     * Default constructor
     */
    public Employee() {
        super();
        this.availabilityStatus = "OFFLINE";
        this.attendedTickets = new ArrayList<>();
    }

    /**
     * Parameterized constructor
     *
     * @param id Employee's unique identifier
     * @param name Employee's full name
     * @param password Employee's authentication password
     */
    public Employee(String id, String name, String password) {
        super(id, name, password);
        this.availabilityStatus = "OFFLINE";
        this.attendedTickets = new ArrayList<>();
    }

    /**
     * Complete constructor with all fields
     *
     * @param id Employee's unique identifier
     * @param name Employee's full name
     * @param password Employee's authentication password
     * @param availabilityStatus Current availability status
     * @param assignedStation Station where the employee works
     */
    public Employee(String id, String name, String password, String availabilityStatus, Station assignedStation) {
        super(id, name, password);
        this.availabilityStatus = availabilityStatus;
        this.assignedStation = assignedStation;
        this.attendedTickets = new ArrayList<>();
    }

    /**
     * Marks the current ticket as completed
     *
     * @param ticket The ticket being attended
     * @return true if successful, false otherwise
     */
    public boolean markTicketAsCompleted(Ticket ticket) {
        if (ticket != null && "IN_PROGRESS".equals(ticket.getStatus())) {
            ticket.setStatus("COMPLETED");
            ticket.setCompletionTime(LocalDateTime.now());
            this.attendedTickets.add(ticket);
            this.availabilityStatus = "AVAILABLE";
            return true;
        }
        return false;
    }

    /**
     * Attends to the next client in the queue
     *
     * @param ticket The next ticket to be attended
     * @return true if the employee started attending the client, false otherwise
     */
    public boolean attendNextClient(Ticket ticket) {
        if (ticket != null && "WAITING".equals(ticket.getStatus()) && "AVAILABLE".equals(this.availabilityStatus)) {
            ticket.setStatus("IN_PROGRESS");
            ticket.setAttentionTime(LocalDateTime.now());
            this.availabilityStatus = "BUSY";
            return true;
        }
        return false;
    }

    /**
     * Pauses the assignment of new tickets to this employee
     *
     * @return true if the status was changed to PAUSED, false otherwise
     */
    public boolean pauseAssignment() {
        if (!"BUSY".equals(this.availabilityStatus)) {
            this.availabilityStatus = "PAUSED";
            return true;
        }
        return false;
    }

    /**
     * Resumes the assignment of tickets to this employee
     *
     * @return true if the status was changed to AVAILABLE, false otherwise
     */
    public boolean resumeAttention() {
        if ("PAUSED".equals(this.availabilityStatus) || "OFFLINE".equals(this.availabilityStatus)) {
            this.availabilityStatus = "AVAILABLE";
            return true;
        }
        return false;
    }

    /**
     * Retrieves information about a client's ticket
     *
     * @param ticket The ticket to get information about
     * @return A string with client information or null if the ticket is invalid
     */
    public String getClientInformation(Ticket ticket) {
        if (ticket != null) {
            return "Ticket: " + ticket.getCode() +
                    "\nCategory: " + (ticket.getCategory() != null ? ticket.getCategory().getName() : "N/A") +
                    "\nGeneration Time: " + ticket.getGenerationTime() +
                    "\nWaiting Time: " + ticket.calculateWaitingTime() + " minutes";
        }
        return null;
    }

    /**
     * Generates a summary of tickets attended by this employee during the current session
     *
     * @return A string summarizing the attended tickets
     */
    public String getAttentionSummary() {
        StringBuilder summary = new StringBuilder("Attention Summary for " + getName() + ":\n");
        summary.append("Total tickets attended: ").append(attendedTickets.size()).append("\n");

        double totalServiceTime = 0;
        for (Ticket ticket : attendedTickets) {
            if (ticket.getAttentionTime() != null && ticket.getCompletionTime() != null) {
                long serviceTimeMinutes = java.time.Duration.between(
                        ticket.getAttentionTime(), ticket.getCompletionTime()).toMinutes();
                totalServiceTime += serviceTimeMinutes;
            }
        }

        if (!attendedTickets.isEmpty()) {
            summary.append("Average service time: ")
                    .append(String.format("%.2f", totalServiceTime / attendedTickets.size()))
                    .append(" minutes\n");
        }

        return summary.toString();
    }

    // Getters and Setters

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Station getAssignedStation() {
        return assignedStation;
    }

    public void setAssignedStation(Station assignedStation) {
        this.assignedStation = assignedStation;
    }

    public List<Ticket> getAttendedTickets() {
        return new ArrayList<>(attendedTickets);  // Return a copy to maintain encapsulation
    }

    /**
     * Returns a string representation of this Employee
     *
     * @return A string representation
     */
    @Override
    public String toString() {
        return "Employee{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", availabilityStatus='" + availabilityStatus + '\'' +
                ", station=" + (assignedStation != null ? assignedStation.getNumber() : "None") +
                ", attendedTickets=" + attendedTickets.size() +
                '}';
    }
}