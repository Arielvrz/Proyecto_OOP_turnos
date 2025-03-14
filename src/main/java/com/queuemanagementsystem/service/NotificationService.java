package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Client;
import com.queuemanagementsystem.model.NotificationSystem;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.repository.ClientRepository;

import java.util.Optional;

/**
 * Service class for handling notifications to clients and displays.
 */
public class NotificationService {
    private final NotificationSystem notificationSystem;
    private final ClientRepository clientRepository;

    /**
     * Constructor with dependencies
     *
     * @param notificationSystem The notification system for displays
     * @param clientRepository Repository for client data
     */
    public NotificationService(NotificationSystem notificationSystem, ClientRepository clientRepository) {
        this.notificationSystem = notificationSystem;
        this.clientRepository = clientRepository;
    }

    /**
     * Notifies a client that their ticket is now being processed
     *
     * @param ticket The ticket that is now in progress
     * @param stationNumber The station number where the client should go
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean notifyClientTicketInProgress(Ticket ticket, int stationNumber) {
        if (ticket == null) {
            return false;
        }

        // Update the display
        boolean displayUpdated = notificationSystem.displayTicket(ticket, stationNumber);

        // Generate a visual alert
        boolean alertGenerated = notificationSystem.generateVisualAlert(ticket);

        // Also notify the client personally if possible
        Optional<Client> clientOpt = clientRepository.findById(ticket.getClientId());
        boolean clientNotified = false;

        if (clientOpt.isPresent()) {
            String message = "Your ticket " + ticket.getCode() + " is now being attended at station " + stationNumber;
            clientNotified = clientOpt.get().receiveAlert(message);
        }

        return displayUpdated && alertGenerated;
    }

    /**
     * Notifies a client that their ticket has been created
     *
     * @param ticket The newly created ticket
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean notifyClientTicketCreated(Ticket ticket) {
        if (ticket == null) {
            return false;
        }

        Optional<Client> clientOpt = clientRepository.findById(ticket.getClientId());

        if (!clientOpt.isPresent()) {
            return false;
        }

        String message = "Your ticket " + ticket.getCode() + " has been created for " +
                (ticket.getCategory() != null ? ticket.getCategory().getName() : "general service");

        return clientOpt.get().receiveAlert(message);
    }

    /**
     * Notifies a client about their position in the queue
     *
     * @param ticket The ticket
     * @param position The position in the queue (1-based)
     * @return true if the notification was sent successfully, false otherwise
     */
    public boolean notifyClientQueuePosition(Ticket ticket, int position) {
        if (ticket == null || position < 1) {
            return false;
        }

        Optional<Client> clientOpt = clientRepository.findById(ticket.getClientId());

        if (!clientOpt.isPresent()) {
            return false;
        }

        String message = "Your ticket " + ticket.getCode() + " is currently in position " + position + " in the queue";

        return clientOpt.get().receiveAlert(message);
    }

    /**
     * Updates the main display with a custom message
     *
     * @param message The message to display
     * @return true if the display was updated successfully, false otherwise
     */
    public boolean updateMainDisplay(String message) {
        return notificationSystem.updateDisplay(message);
    }

    /**
     * Clears the notification display
     *
     * @return true if the display was cleared successfully, false otherwise
     */
    public boolean clearDisplay() {
        return notificationSystem.clearDisplay();
    }

    /**
     * Gets the current display message
     *
     * @return The current display message
     */
    public String getCurrentDisplayMessage() {
        return notificationSystem.getDisplayMessage();
    }

    /**
     * Gets the currently displayed ticket
     *
     * @return The code of the currently displayed ticket
     */
    public String getCurrentTicket() {
        return notificationSystem.getCurrentTicket();
    }

    /**
     * Gets the currently displayed station
     *
     * @return The number of the currently displayed station
     */
    public int getCurrentStation() {
        return notificationSystem.getCurrentStation();
    }
}