package com.queuemanagementsystem.ui;

import java.util.Scanner;

/**
 * Base interface for menu classes with common functionality.
 */
public interface Menu {
    /**
     * Displays the menu options
     */
    void displayMenu();

    /**
     * Processes user input and executes the corresponding action
     *
     * @param option The option selected by the user
     * @return true if the menu should continue, false if it should exit
     */
    boolean processOption(String option);

    /**
     * Starts the menu interaction loop
     *
     * @param scanner The scanner for reading user input
     */
    default void start(Scanner scanner) {
        boolean continueMenu = true;
        while (continueMenu) {
            displayMenu();
            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();
            continueMenu = processOption(option);
        }
    }
}

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

package com.queuemanagementsystem.ui;

import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.service.StationService;
import com.queuemanagementsystem.service.TicketService;
import com.queuemanagementsystem.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu for employee interactions with the system.
 */
public class EmployeeMenu implements Menu {
    private final Scanner scanner;
    private final UserService userService;
    private final TicketService ticketService;
    private final StationService stationService;
    private Employee currentEmployee;
    private Ticket currentTicket;

    /**
     * Constructor with dependencies
     *
     * @param scanner Scanner for reading user input
     * @param userService Service for user operations
     * @param ticketService Service for ticket operations
     * @param stationService Service for station operations
     */
    public EmployeeMenu(Scanner scanner, UserService userService,
                        TicketService ticketService, StationService stationService) {
        this.scanner = scanner;
        this.userService = userService;
        this.ticketService = ticketService;
        this.stationService = stationService;
    }

    /**
     * Prompts the user to enter their employee credentials
     *
     * @return true if authentication was successful, false otherwise
     */
    public boolean authenticate() {
        System.out.println("\n=== Employee Login ===");

        System.out.print("Enter your ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine().trim();

        Optional<Employee> employeeOpt = userService.getEmployeeById(id);

        if (employeeOpt.isPresent() && employeeOpt.get().login(id, password)) {
            currentEmployee = employeeOpt.get();
            System.out.println("Login successful! Welcome, " + currentEmployee.getName() + "!");

            // Check if employee has an assigned station
            if (currentEmployee.getAssignedStation() == null) {
                System.out.println("Warning: You don't have an assigned station. Please contact an administrator.");
            } else {
                // Make sure the employee is available
                if (!"AVAILABLE".equals(currentEmployee.getAvailabilityStatus())) {
                    System.out.println("Setting your status to AVAILABLE...");
                    currentEmployee.setAvailabilityStatus("AVAILABLE");
                    userService.updateUser(currentEmployee);
                }
            }

            return true;
        } else {
            System.out.println("Invalid credentials. Please try again.");
            return false;
        }
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Employee Menu ===");
        System.out.println("Current status: " + currentEmployee.getAvailabilityStatus());

        if (currentTicket != null) {
            System.out.println("Currently serving: " + currentTicket.getCode());
        }

        System.out.println("1. Get next client");
        System.out.println("2. Complete current service");
        System.out.println("3. View client information");
        System.out.println("4. Pause/Resume ticket assignment");
        System.out.println("5. View daily summary");
        System.out.println("0. Logout");
    }

    @Override
    public boolean processOption(String option) {
        switch (option) {
            case "1":
                getNextClient();
                return true;
            case "2":
                completeCurrentService();
                return true;
            case "3":
                viewClientInformation();
                return true;
            case "4":
                toggleAssignmentStatus();
                return true;
            case "5":
                viewDailySummary();
                return true;
            case "0":
                logout();
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
                return true;
        }
    }

    /**
     * Handles getting the next client from the queue
     */
    private void getNextClient() {
        // Check if employee already has a ticket in progress
        if (currentTicket != null && "IN_PROGRESS".equals(currentTicket.getStatus())) {
            System.out.println("You are already serving a client (Ticket: " + currentTicket.getCode() + ").");
            System.out.println("Please complete the current service before getting a new client.");
            return;
        }

        // Check if employee is available
        if (!"AVAILABLE".equals(currentEmployee.getAvailabilityStatus())) {
            System.out.println("You are currently " + currentEmployee.getAvailabilityStatus() + ".");
            System.out.println("Please resume ticket assignment before getting a new client.");
            return;
        }

        // Check if employee has an assigned station
        if (currentEmployee.getAssignedStation() == null) {
            System.out.println("You don't have an assigned station. Please contact an administrator.");
            return;
        }

        // Try to get the next ticket
        Ticket nextTicket = ticketService.assignNextTicket(currentEmployee);

        if (nextTicket != null) {
            currentTicket = nextTicket;
            System.out.println("New client assigned:");
            System.out.println("Ticket: " + currentTicket.getCode());
            System.out.println("Category: " + (currentTicket.getCategory() != null ?
                    currentTicket.getCategory().getName() : "N/A"));
            System.out.println("Waiting time: " + currentTicket.calculateWaitingTime() + " minutes");
        } else {
            System.out.println("There are no clients waiting in your assigned categories.");
        }
    }

    /**
     * Handles completing the current service ticket
     */
    private void completeCurrentService() {
        if (currentTicket == null || !"IN_PROGRESS".equals(currentTicket.getStatus())) {
            System.out.println("You don't have an active client to complete.");
            return;
        }

        System.out.println("Completing service for ticket: " + currentTicket.getCode());

        if (ticketService.completeTicket(currentTicket, currentEmployee)) {
            System.out.println("Service completed successfully.");
            System.out.println("Service time: " + currentTicket.calculateServiceTime() + " minutes");
            currentTicket = null; // Clear the current ticket
        } else {
            System.out.println("Failed to complete the service. Please try again.");
        }
    }

    /**
     * Displays information about the current client
     */
    private void viewClientInformation() {
        if (currentTicket == null) {
            System.out.println("You don't have an assigned client.");
            return;
        }

        System.out.println("\n=== Client Information ===");
        System.out.println(currentEmployee.getClientInformation(currentTicket));

        // Get more detailed information if needed
        Optional<Ticket> ticketDetails = ticketService.getTicketByCode(currentTicket.getCode());

        if (ticketDetails.isPresent()) {
            Ticket ticket = ticketDetails.get();

            if ("IN_PROGRESS".equals(ticket.getStatus())) {
                System.out.println("Current service time: " + ticket.calculateServiceTime() + " minutes");
            }
        }
    }

    /**
     * Toggles the ticket assignment status (pause/resume)
     */
    private void toggleAssignmentStatus() {
        if ("AVAILABLE".equals(currentEmployee.getAvailabilityStatus()) ||
                "OFFLINE".equals(currentEmployee.getAvailabilityStatus())) {

            // Pause assignment
            if (currentEmployee.pauseAssignment()) {
                System.out.println("Ticket assignment paused. You will not receive new clients.");
                userService.updateUser(currentEmployee);
            } else {
                System.out.println("Cannot pause assignment right now.");
            }

        } else if ("PAUSED".equals(currentEmployee.getAvailabilityStatus())) {

            // Resume assignment
            if (currentEmployee.resumeAttention()) {
                System.out.println("Ticket assignment resumed. You can now receive new clients.");
                userService.updateUser(currentEmployee);
            } else {
                System.out.println("Cannot resume assignment right now.");
            }

        } else {
            System.out.println("You cannot change your status while serving a client.");
        }
    }

    /**
     * Displays a summary of tickets attended during the day
     */
    private void viewDailySummary() {
        System.out.println("\n=== Daily Summary ===");
        System.out.println(currentEmployee.getAttentionSummary());

        List<Ticket> attendedTickets = currentEmployee.getAttendedTickets();

        if (attendedTickets.isEmpty()) {
            System.out.println("You haven't attended any clients today.");
            return;
        }

        System.out.println("Tickets attended today: " + attendedTickets.size());

        // Calculate average service time
        double totalServiceTime = 0;
        int completedTickets = 0;

        for (Ticket ticket : attendedTickets) {
            if ("COMPLETED".equals(ticket.getStatus())) {
                totalServiceTime += ticket.calculateServiceTime();
                completedTickets++;
            }
        }

        if (completedTickets > 0) {
            double averageServiceTime = totalServiceTime / completedTickets;
            System.out.println("Average service time: " + String.format("%.2f", averageServiceTime) + " minutes");
        }
    }

    /**
     * Handles employee logout
     */
    private void logout() {
        // Check if there's a ticket in progress
        if (currentTicket != null && "IN_PROGRESS".equals(currentTicket.getStatus())) {
            System.out.println("Warning: You have an active client. Please complete the service before logging out.");
            System.out.println("Do you really want to logout? (y/n)");

            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                System.out.println("Logout canceled.");
                return;
            }
        }

        // Set employee status to offline
        currentEmployee.setAvailabilityStatus("OFFLINE");
        userService.updateUser(currentEmployee);

        System.out.println("You have been logged out. Goodbye, " + currentEmployee.getName() + "!");
    }

    /**
     * Starts the employee menu after authentication
     */
    public void start() {
        if (authenticate()) {
            start(scanner);
        }
    }
}