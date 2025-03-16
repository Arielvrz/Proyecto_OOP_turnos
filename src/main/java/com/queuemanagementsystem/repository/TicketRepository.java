package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.Ticket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Ticket data access operations.
 */
public interface TicketRepository {
    /**
     * Saves a ticket to the repository
     *
     * @param ticket The ticket to save
     * @return true if save was successful, false otherwise
     */
    boolean save(Ticket ticket);

    /**
     * Finds a ticket by its code
     *
     * @param code The ticket code to search for
     * @return Optional containing the ticket if found, empty otherwise
     */
    Optional<Ticket> findByCode(String code);

    /**
     * Gets all tickets in the repository
     *
     * @return List of all tickets
     */
    List<Ticket> findAll();

    /**
     * Finds tickets by client ID
     *
     * @param clientId The client ID to search for
     * @return List of tickets for the specified client
     */
    List<Ticket> findByClientId(String clientId);

    /**
     * Finds tickets by category ID
     *
     * @param categoryId The category ID to search for
     * @return List of tickets for the specified category
     */
    List<Ticket> findByCategoryId(int categoryId);

    /**
     * Finds tickets by status
     *
     * @param status The status to search for
     * @return List of tickets with the specified status
     */
    List<Ticket> findByStatus(String status);

    /**
     * Finds tickets generated between specified times
     *
     * @param start The start time
     * @param end The end time
     * @return List of tickets generated between start and end times
     */
    List<Ticket> findByGenerationTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Deletes a ticket by its code
     *
     * @param code The code of the ticket to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteByCode(String code);

    /**
     * Updates an existing ticket
     *
     * @param ticket The ticket with updated information
     * @return true if update was successful, false otherwise
     */
    boolean update(Ticket ticket);

    /**
     * Saves all tickets to persistent storage
     *
     * @return true if save was successful, false otherwise
     */
    boolean saveAll();

    /**
     * Loads all tickets from persistent storage
     *
     * @return true if load was successful, false otherwise
     */
    boolean loadAll();
}