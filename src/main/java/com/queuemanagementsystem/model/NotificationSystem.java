package com.queuemanagementsystem.model;

/**
 * Manages visual notifications for clients.
 */
public class NotificationSystem {
    private String displayMessage;
    private String currentTicket;
    private int currentStation;

    /**
     * Default constructor
     */
    public NotificationSystem() {
        this.displayMessage = "Welcome to the Queue Management System";
    }

    /**
     * Displays a ticket on the notification screen
     *
     * @param ticket The ticket to display
     * @param stationNumber The station number where the ticket should be attended
     * @return true if the display was updated successfully, false otherwise
     */
    public boolean displayTicket(Ticket ticket, int stationNumber) {
        if (ticket == null) {
            return false;
        }

        this.currentTicket = ticket.getCode();
        this.currentStation = stationNumber;
        this.displayMessage = "Ticket " + currentTicket + " please proceed to station " + currentStation;

        // In a real system, this would update a physical display or send notifications
        System.out.println("DISPLAY UPDATE: " + displayMessage);

        return true;
    }

    /**
     * Generates a visual alert for a specific ticket
     *
     * @param ticket The ticket to alert
     * @return true if the alert was generated successfully, false otherwise
     */
    public boolean generateVisualAlert(Ticket ticket) {
        if (ticket == null) {
            return false;
        }

        // Flash the display or use other visual cues
        this.displayMessage = "**ALERT** Ticket " + ticket.getCode() + " is now being called!";

        // In a real system, this would trigger special visual effects
        System.out.println("VISUAL ALERT: " + displayMessage);

        return true;
    }

    /**
     * Updates the display with a custom message
     *
     * @param message The message to display
     * @return true if the display was updated successfully
     */
    public boolean updateDisplay(String message) {
        if (message != null && !message.isEmpty()) {
            this.displayMessage = message;

            // In a real system, this would update a physical display
            System.out.println("DISPLAY UPDATE: " + displayMessage);

            return true;
        }
        return false;
    }

    /**
     * Clears the display
     *
     * @return true indicating the display was cleared
     */
    public boolean clearDisplay() {
        this.displayMessage = "";
        this.currentTicket = null;
        this.currentStation = 0;

        // In a real system, this would clear a physical display
        System.out.println("DISPLAY CLEARED");

        return true;
    }

    // Getters and Setters

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getCurrentTicket() {
        return currentTicket;
    }

    public int getCurrentStation() {
        return currentStation;
    }

    /**
     * Returns a string representation of this NotificationSystem
     *
     * @return A string representation
     */
    @Override
    public String toString() {
        return "NotificationSystem{" +
                "displayMessage='" + displayMessage + '\'' +
                ", currentTicket='" + currentTicket + '\'' +
                ", currentStation=" + currentStation +
                '}';
    }
}