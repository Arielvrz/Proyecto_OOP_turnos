package com.queuemanagementsystem.ui;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Client;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.service.CategoryService;
import com.queuemanagementsystem.service.ClientService;
import com.queuemanagementsystem.service.TicketService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu for client interactions with the system.
 */
public class ClientMenu implements Menu {
    private final Scanner scanner;
    private final ClientService clientService;
    private final CategoryService categoryService;
    private final TicketService ticketService;
    private Client currentClient;

    /**
     * Constructor with dependencies
     *
     * @param scanner Scanner for reading user input
     * @param clientService Service for client operations
     * @param categoryService Service for category operations
     * @param ticketService Service for ticket operations
     */
    public ClientMenu(Scanner scanner, ClientService clientService,
                      CategoryService categoryService, TicketService ticketService) {
        this.scanner = scanner;
        this.clientService = clientService;
        this.categoryService = categoryService;
        this.ticketService = ticketService;
    }

    /**
     * Prompts the user to enter their client ID or create a new client profile
     *
     * @return true if a client was identified or created, false otherwise
     */
    public boolean identifyClient() {
        System.out.println("\n=== Client Identification ===");
        System.out.println("1. Enter existing client ID");
        System.out.println("2. Register as a new client");
        System.out.println("0. Return to main menu");

        System.out.print("Select an option: ");
        String option = scanner.nextLine().trim();

        switch (option) {
            case "1":
                return loginExistingClient();
            case "2":
                return registerNewClient();
            case "0":
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
                return identifyClient();
        }
    }

    /**
     * Handles the login of an existing client
     *
     * @return true if login was successful, false otherwise
     */
    private boolean loginExistingClient() {
        System.out.print("Enter your client ID: ");
        String clientId = scanner.nextLine().trim();

        Optional<Client> clientOpt = clientService.getClientById(clientId);

        if (clientOpt.isPresent()) {
            this.currentClient = clientOpt.get();
            System.out.println("Welcome back, " + currentClient.getName() + "!");
            return true;
        } else {
            System.out.println("Client not found. Would you like to register as a new client? (y/n)");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("y") || response.equals("yes")) {
                return registerNewClient();
            } else {
                return false;
            }
        }
    }

    /**
     * Handles the registration of a new client
     *
     * @return true if registration was successful, false otherwise
     */
    private boolean registerNewClient() {
        System.out.println("\n=== New Client Registration ===");

        System.out.print("Enter your ID (e.g., national ID, passport): ");
        String id = scanner.nextLine().trim();

        // Check if client already exists
        if (clientService.getClientById(id).isPresent()) {
            System.out.println("A client with this ID already exists. Please login instead.");
            return loginExistingClient();
        }

        System.out.print("Enter your full name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter contact information (phone or email): ");
        String contactInfo = scanner.nextLine().trim();

        Client newClient = new Client(id, name, contactInfo);

        if (clientService.registerClient(newClient)) {
            this.currentClient = newClient;
            System.out.println("Registration successful! Welcome, " + name + "!");
            return true;
        } else {
            System.out.println("Registration failed. Please try again later.");
            return false;
        }
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Client Menu ===");
        System.out.println("1. Request a new ticket");
        System.out.println("2. Check queue status");
        System.out.println("3. View my tickets");
        System.out.println("4. Cancel a ticket");
        System.out.println("0. Exit");
    }

    @Override
    public boolean processOption(String option) {
        switch (option) {
            case "1":
                requestTicket();
                return true;
            case "2":
                checkQueueStatus();
                return true;
            case "3":
                viewMyTickets();
                return true;
            case "4":
                cancelTicket();
                return true;
            case "0":
                System.out.println("Thank you for using our service. Goodbye!");
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
                return true;
        }
    }

    /**
     * Handles the process of requesting a new ticket
     */
    private void requestTicket() {
        System.out.println("\n=== Request a Ticket ===");

        // Get active categories
        List<Category> activeCategories = categoryService.getAllActiveCategories();

        if (activeCategories.isEmpty()) {
            System.out.println("Sorry, there are no active service categories at the moment.");
            return;
        }

        // Display categories
        System.out.println("Available service categories:");
        for (int i = 0; i < activeCategories.size(); i++) {
            Category category = activeCategories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " - " + category.getDescription());
        }

        // Get user selection
        System.out.print("Select a category (1-" + activeCategories.size() + "): ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection < 1 || selection > activeCategories.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Category selectedCategory = activeCategories.get(selection - 1);

            // Create the ticket
            Ticket ticket = ticketService.createTicket(currentClient.getId(), selectedCategory.getId());

            if (ticket != null) {
                System.out.println("Ticket created successfully!");
                System.out.println("Your ticket code is: " + ticket.getCode());
                System.out.println("Category: " + selectedCategory.getName());
                System.out.println("Current position in queue: " + ticketService.getTicketQueuePosition(ticket.getCode()));
            } else {
                System.out.println("Failed to create a ticket. Please try again later.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Handles the process of checking the status of a queue
     */
    private void checkQueueStatus() {
        System.out.println("\n=== Queue Status ===");

        // Get active categories
        List<Category> categories = categoryService.getAllActiveCategories();

        if (categories.isEmpty()) {
            System.out.println("There are no active service categories at the moment.");
            return;
        }

        // Display each category's queue status
        for (Category category : categories) {
            int pendingTickets = category.countPendingTickets();
            System.out.println(category.getName() + ": " + pendingTickets + " tickets waiting");

            // Option to see more details
            if (pendingTickets > 0) {
                System.out.println("  * Estimated waiting time: ~" + (pendingTickets * 10) + " minutes");
            }
        }
    }

    /**
     * Displays the client's tickets and their status
     */
    private void viewMyTickets() {
        System.out.println("\n=== My Tickets ===");

        List<Ticket> clientTickets = ticketService.getTicketsByClient(currentClient.getId());

        if (clientTickets.isEmpty()) {
            System.out.println("You have no tickets.");
            return;
        }

        for (Ticket ticket : clientTickets) {
            System.out.println("Code: " + ticket.getCode());
            System.out.println("  Category: " + (ticket.getCategory() != null ? ticket.getCategory().getName() : "N/A"));
            System.out.println("  Status: " + ticket.getStatus());
            System.out.println("  Generated: " + ticket.getGenerationTime());

            if ("WAITING".equals(ticket.getStatus())) {
                int position = ticketService.getTicketQueuePosition(ticket.getCode());
                System.out.println("  Queue position: " + (position > 0 ? position : "Unknown"));
            }

            if (ticket.getAttentionTime() != null) {
                System.out.println("  Started service: " + ticket.getAttentionTime());
            }

            if (ticket.getCompletionTime() != null) {
                System.out.println("  Completed: " + ticket.getCompletionTime());
            }

            System.out.println("  Waiting time: " + ticket.calculateWaitingTime() + " minutes");

            if ("IN_PROGRESS".equals(ticket.getStatus()) || "COMPLETED".equals(ticket.getStatus())) {
                System.out.println("  Service time: " + ticket.calculateServiceTime() + " minutes");
            }

            System.out.println("-----");
        }
    }

    /**
     * Handles the process of canceling a ticket
     */
    private void cancelTicket() {
        System.out.println("\n=== Cancel a Ticket ===");

        List<Ticket> waitingTickets = ticketService.getTicketsByClient(currentClient.getId()).stream()
                .filter(ticket -> "WAITING".equals(ticket.getStatus()))
                .toList();

        if (waitingTickets.isEmpty()) {
            System.out.println("You have no waiting tickets that can be canceled.");
            return;
        }

        System.out.println("Your waiting tickets:");
        for (int i = 0; i < waitingTickets.size(); i++) {
            Ticket ticket = waitingTickets.get(i);
            System.out.println((i + 1) + ". " + ticket.getCode() + " - " +
                    (ticket.getCategory() != null ? ticket.getCategory().getName() : "N/A"));
        }

        System.out.print("Select a ticket to cancel (1-" + waitingTickets.size() + ") or 0 to go back: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > waitingTickets.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Ticket selectedTicket = waitingTickets.get(selection - 1);

            System.out.println("Are you sure you want to cancel ticket " + selectedTicket.getCode() + "? (y/n)");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("y") || confirmation.equals("yes")) {
                if (ticketService.cancelTicket(selectedTicket.getCode())) {
                    System.out.println("Ticket successfully canceled.");
                } else {
                    System.out.println("Failed to cancel the ticket. Please try again later.");
                }
            } else {
                System.out.println("Cancellation aborted.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Starts the client menu after identifying the client
     */
    public void start() {
        if (identifyClient()) {
            start(scanner);
        }
    }
}