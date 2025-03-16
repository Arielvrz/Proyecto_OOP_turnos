package com.queuemanagementsystem.ui;

import com.queuemanagementsystem.model.Administrator;
import com.queuemanagementsystem.model.Employee;

import com.queuemanagementsystem.service.*;
import java.util.Scanner;

/**
 * Main menu for the Queue Management System.
 * Displays the initial options for different user roles.
 */
public class MainMenu implements Menu {
    private final Scanner scanner;
    private final ClientService clientService;
    private final CategoryService categoryService;
    private final TicketService ticketService;
    private final UserService userService;
    private final StationService stationService;
    private final StatisticsService statisticsService;

    /**
     * Constructor with dependencies
     *
     * @param scanner Scanner for reading user input
     * @param clientService Service for client operations
     * @param categoryService Service for category operations
     * @param ticketService Service for ticket operations
     * @param userService Service for user operations
     * @param stationService Service for station operations
     * @param statisticsService Service for statistics operations
     */
    public MainMenu(Scanner scanner, ClientService clientService, CategoryService categoryService,
                    TicketService ticketService, UserService userService, StationService stationService,
                    StatisticsService statisticsService) {
        this.scanner = scanner;
        this.clientService = clientService;
        this.categoryService = categoryService;
        this.ticketService = ticketService;
        this.userService = userService;
        this.stationService = stationService;
        this.statisticsService = statisticsService;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Queue Management System ===");
        System.out.println("1. Client");
        System.out.println("2. Employee");
        System.out.println("3. Administrator");
        System.out.println("0. Exit");
    }

    @Override
    public boolean processOption(String option) {
        switch (option) {
            case "1":
                ClientMenu clientMenu = new ClientMenu(scanner, clientService, categoryService, ticketService);
                clientMenu.start();
                return true;
            case "2":
                EmployeeMenu employeeMenu = new EmployeeMenu(scanner, userService, ticketService, stationService);
                employeeMenu.start();
                return true;
            case "3":
                AdminMenu adminMenu = new AdminMenu(scanner, userService, categoryService, stationService, statisticsService);
                adminMenu.start();
                return true;
            case "0":
                System.out.println("Thank you for using the Queue Management System. Goodbye!");
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
                return true;
        }
    }
}