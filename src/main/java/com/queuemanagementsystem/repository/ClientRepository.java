package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.Client;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Client data access operations.
 */
public interface ClientRepository {
    /**
     * Saves a client to the repository
     *
     * @param client The client to save
     * @return The saved client
     */
    Client save(Client client);

    /**
     * Finds a client by their ID
     *
     * @param id The client ID to search for
     * @return Optional containing the client if found, empty otherwise
     */
    Optional<Client> findById(String id);

    /**
     * Gets all clients in the repository
     *
     * @return List of all clients
     */
    List<Client> findAll();

    /**
     * Deletes a client by their ID
     *
     * @param id The ID of the client to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteById(String id);

    /**
     * Saves all clients to persistent storage
     *
     * @return true if save was successful, false otherwise
     */
    boolean saveAll();

    /**
     * Loads all clients from persistent storage
     *
     * @return true if load was successful, false otherwise
     */
    boolean loadAll();
}