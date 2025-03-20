package com.queuemanagementsystem.repository;

import com.google.gson.reflect.TypeToken;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.util.JsonFileHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de TicketRepository usando archivo JSON para persistencia.
 */
public class JsonTicketRepository implements TicketRepository {
    private static final String FILE_PATH = "data/tickets.json";
    private List<Ticket> tickets;

    /**
     * Constructor por defecto. Carga tickets desde el archivo.
     */
    public JsonTicketRepository() {
        this.tickets = new ArrayList<>();
        loadAll();
    }

    @Override
    public boolean save(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }

        // Verifica si el ticket ya existe
        Optional<Ticket> existingTicket = findByCode(ticket.getCode());
        if (existingTicket.isPresent()) {
            // Actualiza el ticket existente
            tickets.remove(existingTicket.get());
        }

        tickets.add(ticket);
        return saveAll();
    }

    @Override
    public Optional<Ticket> findByCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        return tickets.stream()
                .filter(ticket -> code.equals(ticket.getCode()))
                .findFirst();
    }

    @Override
    public List<Ticket> findAll() {
        return new ArrayList<>(tickets);
    }

    @Override
    public List<Ticket> findByClientId(String clientId) {
        if (clientId == null) {
            return new ArrayList<>();
        }
        return tickets.stream()
                .filter(ticket -> clientId.equals(ticket.getClientId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> findByCategoryId(int categoryId) {
        return tickets.stream()
                .filter(ticket -> ticket.getCategory() != null && ticket.getCategory().getId() == categoryId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> findByStatus(String status) {
        if (status == null) {
            return new ArrayList<>();
        }
        return tickets.stream()
                .filter(ticket -> status.equals(ticket.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> findByGenerationTimeBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return new ArrayList<>();
        }
        return tickets.stream()
                .filter(ticket -> {
                    LocalDateTime genTime = ticket.getGenerationTime();
                    return genTime != null && !genTime.isBefore(start) && !genTime.isAfter(end);
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteByCode(String code) {
        Optional<Ticket> ticket = findByCode(code);
        if (ticket.isPresent()) {
            boolean removed = tickets.remove(ticket.get());
            if (removed) {
                saveAll();
            }
            return removed;
        }
        return false;
    }

    @Override
    public boolean update(Ticket ticket) {
        if (ticket == null || ticket.getCode() == null) {
            return false;
        }

        Optional<Ticket> existingTicket = findByCode(ticket.getCode());
        if (!existingTicket.isPresent()) {
            return false;
        }

        tickets.remove(existingTicket.get());
        tickets.add(ticket);
        return saveAll();
    }

    @Override
    public boolean saveAll() {
        return JsonFileHandler.saveToFile(tickets, FILE_PATH);
    }

    @Override
    public boolean loadAll() {
        TypeToken<List<Ticket>> typeToken = new TypeToken<List<Ticket>>() {};
        this.tickets = JsonFileHandler.loadFromFile(FILE_PATH, typeToken.getType());
        return true;
    }
}