package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Client;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.repository.CategoryRepository;
import com.queuemanagementsystem.repository.ClientRepository;
import com.queuemanagementsystem.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing tickets.
 */
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ClientRepository clientRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;

    /**
     * Constructor with repository dependencies
     *
     * @param ticketRepository Repository for ticket data
     * @param clientRepository Repository for client data
     * @param categoryRepository Repository for category data
     * @param notificationService Service for sending notifications
     */
    public TicketService(TicketRepository ticketRepository, ClientRepository clientRepository,
                         CategoryRepository categoryRepository, NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.clientRepository = clientRepository;
        this.categoryRepository = categoryRepository;
        this.notificationService = notificationService;
    }

    /**
     * Creates a new ticket for a client
     *
     * @param clientId The client's ID
     * @param categoryId The service category ID
     * @return The created ticket if successful, null otherwise
     */
    public Ticket createTicket(String clientId, int categoryId) {
        Optional<Client> clientOpt = clientRepository.findById(clientId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!clientOpt.isPresent() || !categoryOpt.isPresent()) {
            return null;
        }

        Client client = clientOpt.get();
        Category category = categoryOpt.get();

        if (!category.isActive()) {
            return null;
        }

        Ticket ticket = new Ticket(category, clientId);

        if (ticketRepository.save(ticket)) {
            // Add the ticket to the category's queue
            category.addTicketToQueue(ticket);
            categoryRepository.update(category);
            return ticket;
        }

        return null;
    }

    /**
     * Assigns the next ticket to an employee
     *
     * @param employee The employee to assign the ticket to
     * @return The assigned ticket if successful, null otherwise
     */
    public Ticket assignNextTicket(Employee employee) {
        if (employee == null || !"AVAILABLE".equals(employee.getAvailabilityStatus())) {
            return null;
        }

        // Check if the employee's station supports any categories
        if (employee.getAssignedStation() == null) {
            return null;
        }

        // Get category IDs from the station
        List<Integer> categoryIds = employee.getAssignedStation().getSupportedCategoryIds();

        // Create a list for the actual category objects
        List<Category> supportedCategories = new ArrayList<>();

        // Resolve each category ID to its actual object
        for (Integer categoryId : categoryIds) {
            categoryRepository.findById(categoryId).ifPresent(supportedCategories::add);
        }

        // Find the next ticket from any supported category
        for (Category category : supportedCategories) {
            Ticket nextTicket = category.getNextTicket();

            if (nextTicket != null) {
                if (employee.attendNextClient(nextTicket)) {
                    // Update the ticket status
                    nextTicket.changeStatus("IN_PROGRESS");
                    nextTicket.setAttentionTime(LocalDateTime.now());

                    // Update the ticket in the repository
                    ticketRepository.update(nextTicket);

                    // Send notification
                    notificationService.notifyClientTicketInProgress(nextTicket,
                            employee.getAssignedStation().getNumber());

                    return nextTicket;
                }
            }
        }

        return null; // No tickets found in any category
    }

    /**
     * Completes a ticket that's currently being attended by an employee
     *
     * @param ticket The ticket to complete
     * @param employee The employee serving the ticket
     * @return true if the ticket was completed successfully, false otherwise
     */
    public boolean completeTicket(Ticket ticket, Employee employee) {
        if (ticket == null || employee == null ||
                !"IN_PROGRESS".equals(ticket.getStatus())) {
            return false;
        }

        if (employee.markTicketAsCompleted(ticket)) {
            ticket.changeStatus("COMPLETED");
            ticket.setCompletionTime(LocalDateTime.now());
            return ticketRepository.update(ticket);
        }

        return false;
    }

    /**
     * Cancels a ticket
     *
     * @param ticketCode The code of the ticket to cancel
     * @return true if the ticket was cancelled successfully, false otherwise
     */
    public boolean cancelTicket(String ticketCode) {
        Optional<Ticket> ticketOpt = ticketRepository.findByCode(ticketCode);

        if (!ticketOpt.isPresent() || !"WAITING".equals(ticketOpt.get().getStatus())) {
            return false;
        }

        Ticket ticket = ticketOpt.get();
        ticket.setStatus("CANCELLED");

        // Remove from category queue (this would need to be implemented in Category)
        Category category = ticket.getCategory();
        if (category != null) {
            // Here we're assuming there's a method to remove a specific ticket from the queue
            // In a real system, you might need a more sophisticated approach
            category.peekTicketQueue().remove(ticket);
            categoryRepository.update(category);
        }

        return ticketRepository.update(ticket);
    }

    /**
     * Gets all waiting tickets for a specific category
     *
     * @param categoryId The category ID
     * @return List of waiting tickets
     */
    public List<Ticket> getWaitingTicketsByCategory(int categoryId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getCategory() != null &&
                        ticket.getCategory().getId() == categoryId &&
                        "WAITING".equals(ticket.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all tickets for a specific client
     *
     * @param clientId The client's ID
     * @return List of the client's tickets
     */
    public List<Ticket> getTicketsByClient(String clientId) {
        return ticketRepository.findByClientId(clientId);
    }

    /**
     * Gets all tickets attended by a specific employee
     *
     * @param employeeId The employee's ID
     * @return List of tickets attended by the employee
     */
    public List<Ticket> getTicketsAttendedByEmployee(String employeeId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> (ticket.getStatus().equals("IN_PROGRESS") ||
                        ticket.getStatus().equals("COMPLETED")))
                .collect(Collectors.toList());
    }

    /**
     * Gets a ticket by its code
     *
     * @param code The ticket code
     * @return Optional containing the ticket if found, empty otherwise
     */
    public Optional<Ticket> getTicketByCode(String code) {
        return ticketRepository.findByCode(code);
    }

    /**
     * Gets the queue position of a waiting ticket
     *
     * @param ticketCode The ticket code
     * @return The position in queue (1-based) or -1 if not found or not waiting
     */
    public int getTicketQueuePosition(String ticketCode) {
        Optional<Ticket> ticketOpt = ticketRepository.findByCode(ticketCode);

        if (!ticketOpt.isPresent() || !"WAITING".equals(ticketOpt.get().getStatus())) {
            return -1;
        }

        Ticket ticket = ticketOpt.get();
        Category category = ticket.getCategory();

        if (category == null) {
            return -1;
        }

        List<Ticket> queue = category.peekTicketQueue();
        for (int i = 0; i < queue.size(); i++) {
            if (ticket.equals(queue.get(i))) {
                return i + 1; // 1-based position
            }
        }

        return -1; // Not found in queue
    }
}