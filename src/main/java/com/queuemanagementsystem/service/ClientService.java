package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Client;
import com.queuemanagementsystem.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing clients.
 */
public class ClientService {
    private final ClientRepository clientRepository;

    /**
     * Constructor with repository dependency
     *
     * @param clientRepository Repository for client data
     */
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Registers a new client
     *
     * @param client The client to register
     * @return true if registration was successful, false otherwise
     */
    public boolean registerClient(Client client) {
        if (client == null || client.getId() == null || client.getId().isEmpty()) {
            return false;
        }

        // Check if client already exists
        if (clientRepository.findById(client.getId()).isPresent()) {
            return false;
        }

        return clientRepository.save(client) != null;
    }

    /**
     * Updates an existing client
     *
     * @param client The client with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateClient(Client client) {
        if (client == null || client.getId() == null || client.getId().isEmpty()) {
            return false;
        }

        // Check if client exists
        if (!clientRepository.findById(client.getId()).isPresent()) {
            return false;
        }

        return clientRepository.save(client) != null;
    }

    /**
     * Gets a client by ID
     *
     * @param clientId The client ID
     * @return Optional containing the client if found, empty otherwise
     */
    public Optional<Client> getClientById(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            return Optional.empty();
        }

        return clientRepository.findById(clientId);
    }

    /**
     * Gets all clients
     *
     * @return List of all clients
     */
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    /**
     * Deletes a client
     *
     * @param clientId The ID of the client to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteClient(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            return false;
        }

        return clientRepository.deleteById(clientId);
    }
}