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

        // Add debugging
        System.out.println("Looking for employee with ID: " + id);

        Optional<Employee> employeeOpt = userService.getEmployeeById(id);

        if (!employeeOpt.isPresent()) {
            System.out.println("Debug: No employee found with ID: " + id);
            System.out.println("Invalid credentials. Please try again.");
            return false;
        }

        Employee employee = employeeOpt.get();
        boolean loginSuccess = employee.login(id, password);

        if (!loginSuccess) {
            System.out.println("Debug: Found employee but password doesn't match");
            System.out.println("Invalid credentials. Please try again.");
            return false;
        }

        currentEmployee = employee;
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