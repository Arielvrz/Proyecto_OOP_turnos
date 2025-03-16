package com.queuemanagementsystem.repository;

import com.google.gson.reflect.TypeToken;
import com.queuemanagementsystem.model.Client;
import com.queuemanagementsystem.util.JsonFileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ClientRepository using JSON file for persistence.
 */
public class JsonClientRepository implements ClientRepository {
    private static final String FILE_PATH = "data/clients.json";
    private List<Client> clients;

    /**
     * Default constructor. Loads clients from file.
     */
    public JsonClientRepository() {
        this.clients = new ArrayList<>();
        loadAll();
    }

    @Override
    public Client save(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }

        // Check if the client already exists
        Optional<Client> existingClient = findById(client.getId());
        if (existingClient.isPresent()) {
            // Update existing client
            clients.remove(existingClient.get());
        }

        clients.add(client);
        saveAll();
        return client;
    }

    @Override
    public Optional<Client> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return clients.stream()
                .filter(client -> client.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Client> findAll() {
        return new ArrayList<>(clients);
    }

    @Override
    public boolean deleteById(String id) {
        Optional<Client> client = findById(id);
        if (client.isPresent()) {
            boolean removed = clients.remove(client.get());
            if (removed) {
                saveAll();
            }
            return removed;
        }
        return false;
    }

    @Override
    public boolean saveAll() {
        return JsonFileHandler.saveToFile(clients, FILE_PATH);
    }

    @Override
    public boolean loadAll() {
        TypeToken<List<Client>> typeToken = new TypeToken<List<Client>>() {};
        this.clients = JsonFileHandler.loadFromFile(FILE_PATH, typeToken.getType());
        return true;
    }
}