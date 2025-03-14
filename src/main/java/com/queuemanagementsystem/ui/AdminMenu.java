package com.queuemanagementsystem.ui;

import com.queuemanagementsystem.model.Administrator;
import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Station;
import com.queuemanagementsystem.service.CategoryService;
import com.queuemanagementsystem.service.StationService;
import com.queuemanagementsystem.service.StatisticsService;
import com.queuemanagementsystem.service.UserService;
import com.queuemanagementsystem.model.Ticket;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menu for administrator interactions with the system.
 */
public class AdminMenu implements Menu {
    private final Scanner scanner;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StationService stationService;
    private final StatisticsService statisticsService;
    private Administrator currentAdmin;

    /**
     * Constructor with dependencies
     *
     * @param scanner           Scanner for reading user input
     * @param userService       Service for user operations
     * @param categoryService   Service for category operations
     * @param stationService    Service for station operations
     * @param statisticsService Service for statistics operations
     */
    public AdminMenu(Scanner scanner, UserService userService, CategoryService categoryService,
                     StationService stationService, StatisticsService statisticsService) {
        this.scanner = scanner;
        this.userService = userService;
        this.categoryService = categoryService;
        this.stationService = stationService;
        this.statisticsService = statisticsService;
    }

    /**
     * Prompts the user to enter their administrator credentials
     *
     * @return true if authentication was successful, false otherwise
     */
    public boolean authenticate() {
        System.out.println("\n=== Administrator Login ===");

        System.out.print("Enter your ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine().trim();

        Optional<Administrator> adminOpt = userService.getAdministratorById(id);

        if (adminOpt.isPresent() && adminOpt.get().login(id, password)) {
            currentAdmin = adminOpt.get();
            System.out.println("Login successful! Welcome, Administrator " + currentAdmin.getName() + "!");
            return true;
        } else {
            System.out.println("Invalid credentials. Please try again.");
            return false;
        }
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Administrator Menu ===");
        System.out.println("1. Manage Categories");
        System.out.println("2. Manage Stations");
        System.out.println("3. Manage Employees");
        System.out.println("4. View Statistics");
        System.out.println("5. Generate Reports");
        System.out.println("0. Logout");
    }

    @Override
    public boolean processOption(String option) {
        switch (option) {
            case "1":
                manageCategoriesMenu();
                return true;
            case "2":
                manageStationsMenu();
                return true;
            case "3":
                manageEmployeesMenu();
                return true;
            case "4":
                viewStatistics();
                return true;
            case "5":
                generateReports();
                return true;
            case "0":
                System.out.println("Logging out. Goodbye, Administrator " + currentAdmin.getName() + "!");
                return false;
            default:
                System.out.println("Invalid option. Please try again.");
                return true;
        }
    }

    /**
     * Displays and handles the categories management submenu
     */
    private void manageCategoriesMenu() {
        boolean continueMenu = true;

        while (continueMenu) {
            System.out.println("\n=== Manage Categories ===");
            System.out.println("1. View all categories");
            System.out.println("2. Create new category");
            System.out.println("3. Activate/Deactivate category");
            System.out.println("4. Update category details");
            System.out.println("0. Back to main menu");

            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    viewAllCategories();
                    break;
                case "2":
                    createCategory();
                    break;
                case "3":
                    toggleCategoryStatus();
                    break;
                case "4":
                    updateCategory();
                    break;
                case "0":
                    continueMenu = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Displays all categories
     */
    private void viewAllCategories() {
        System.out.println("\n=== All Categories ===");

        List<Category> categories = categoryService.getAllCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        for (Category category : categories) {
            System.out.println("ID: " + category.getId());
            System.out.println("Name: " + category.getName());
            System.out.println("Description: " + category.getDescription());
            System.out.println("Prefix: " + category.getPrefix());
            System.out.println("Status: " + (category.isActive() ? "Active" : "Inactive"));
            System.out.println("Pending tickets: " + category.countPendingTickets());
            System.out.println("Assigned employees: " + category.getAssignedEmployees().size());
            System.out.println("-----");
        }
    }

    /**
     * Handles creating a new category
     */
    private void createCategory() {
        System.out.println("\n=== Create New Category ===");

        System.out.print("Enter category name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter category description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Enter category prefix (e.g., GEN, FIN): ");
        String prefix = scanner.nextLine().trim().toUpperCase();

        if (name.isEmpty() || prefix.isEmpty()) {
            System.out.println("Name and prefix are required. Operation cancelled.");
            return;
        }

        // Check if a category with this prefix already exists
        if (categoryService.getCategoryByPrefix(prefix).isPresent()) {
            System.out.println("A category with this prefix already exists. Please use a different prefix.");
            return;
        }

        Category newCategory = categoryService.createCategory(name, description, prefix);

        if (newCategory != null) {
            System.out.println("Category created successfully!");
            System.out.println("ID: " + newCategory.getId());
            System.out.println("Name: " + newCategory.getName());
            System.out.println("Prefix: " + newCategory.getPrefix());
        } else {
            System.out.println("Failed to create category. Please try again.");
        }
    }

    /**
     * Handles activating or deactivating a category
     */
    private void toggleCategoryStatus() {
        System.out.println("\n=== Activate/Deactivate Category ===");

        // Display categories
        List<Category> categories = categoryService.getAllCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " [" +
                    (category.isActive() ? "Active" : "Inactive") + "]");
        }

        // Get user selection
        System.out.print("Select a category to toggle (1-" + categories.size() + ") or 0 to cancel: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > categories.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Category selectedCategory = categories.get(selection - 1);

            // Toggle status
            boolean success;
            if (selectedCategory.isActive()) {
                success = categoryService.deactivateCategory(selectedCategory.getId());
                if (success) {
                    System.out.println("Category '" + selectedCategory.getName() + "' has been deactivated.");
                }
            } else {
                success = categoryService.activateCategory(selectedCategory.getId());
                if (success) {
                    System.out.println("Category '" + selectedCategory.getName() + "' has been activated.");
                }
            }

            if (!success) {
                System.out.println("Failed to update category status. Please try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Handles updating category details
     */
    private void updateCategory() {
        System.out.println("\n=== Update Category ===");

        // Display categories
        List<Category> categories = categoryService.getAllCategories();

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " [" +
                    (category.isActive() ? "Active" : "Inactive") + "]");
        }

        // Get user selection
        System.out.print("Select a category to update (1-" + categories.size() + ") or 0 to cancel: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > categories.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Category selectedCategory = categories.get(selection - 1);

            // Update fields
            System.out.println("Updating category: " + selectedCategory.getName());
            System.out.println("Leave field empty to keep current value");

            System.out.print("New name [" + selectedCategory.getName() + "]: ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                selectedCategory.setName(name);
            }

            System.out.print("New description [" + selectedCategory.getDescription() + "]: ");
            String description = scanner.nextLine().trim();
            if (!description.isEmpty()) {
                selectedCategory.setDescription(description);
            }

            // Prefix is more critical, so confirm changes
            System.out.print("New prefix [" + selectedCategory.getPrefix() + "]: ");
            String prefix = scanner.nextLine().trim().toUpperCase();
            if (!prefix.isEmpty() && !prefix.equals(selectedCategory.getPrefix())) {
                // Check if prefix is already in use
                Optional<Category> existingCategory = categoryService.getCategoryByPrefix(prefix);
                if (existingCategory.isPresent() && existingCategory.get().getId() != selectedCategory.getId()) {
                    System.out.println("This prefix is already in use by another category. Prefix not updated.");
                } else {
                    System.out.println("Warning: Changing the prefix may affect existing tickets.");
                    System.out.println("Are you sure you want to change it? (y/n)");
                    String confirmation = scanner.nextLine().trim().toLowerCase();

                    if (confirmation.equals("y") || confirmation.equals("yes")) {
                        selectedCategory.setPrefix(prefix);
                    }
                }
            }

            // Save changes
            if (categoryService.updateCategory(selectedCategory)) {
                System.out.println("Category updated successfully!");
            } else {
                System.out.println("Failed to update category. Please try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Displays and handles the stations management submenu
     */
    private void manageStationsMenu() {
        boolean continueMenu = true;

        while (continueMenu) {
            System.out.println("\n=== Manage Stations ===");
            System.out.println("1. View all stations");
            System.out.println("2. Create new station");
            System.out.println("3. Open/Close station");
            System.out.println("4. Assign employee to station");
            System.out.println("5. Configure station categories");
            System.out.println("0. Back to main menu");

            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    viewAllStations();
                    break;
                case "2":
                    createStation();
                    break;
                case "3":
                    toggleStationStatus();
                    break;
                case "4":
                    assignEmployeeToStation();
                    break;
                case "5":
                    configureStationCategories();
                    break;
                case "0":
                    continueMenu = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Displays all stations
     */
    private void viewAllStations() {
        System.out.println("\n=== All Stations ===");

        List<Station> stations = stationService.getAllStations();

        if (stations.isEmpty()) {
            System.out.println("No stations found.");
            return;
        }

        for (Station station : stations) {
            System.out.println("ID: " + station.getId());
            System.out.println("Number: " + station.getNumber());
            System.out.println("Status: " + station.getStatus());

            Employee assignedEmployee = station.getAssignedEmployee();
            System.out.println("Assigned employee: " +
                    (assignedEmployee != null ? assignedEmployee.getName() : "None"));

            List<Category> categories = station.getSupportedCategories();
            System.out.println("Supported categories: " +
                    (categories.isEmpty() ? "None" : categories.size() + " categories"));

            if (!categories.isEmpty()) {
                System.out.println("Categories:");
                for (Category category : categories) {
                    System.out.println("  - " + category.getName() + " [" + category.getPrefix() + "]");
                }
            }

            System.out.println("-----");
        }
    }

    /**
     * Handles creating a new station
     */
    private void createStation() {
        System.out.println("\n=== Create New Station ===");

        System.out.print("Enter station number: ");

        try {
            int stationNumber = Integer.parseInt(scanner.nextLine().trim());

            if (stationNumber <= 0) {
                System.out.println("Station number must be positive. Operation cancelled.");
                return;
            }

            // Check if a station with this number already exists
            if (stationService.getAllStations().stream()
                    .anyMatch(s -> s.getNumber() == stationNumber)) {
                System.out.println("A station with this number already exists. Please use a different number.");
                return;
            }

            Station newStation = stationService.createStation(stationNumber);

            if (newStation != null) {
                System.out.println("Station created successfully!");
                System.out.println("ID: " + newStation.getId());
                System.out.println("Number: " + newStation.getNumber());
                System.out.println("Status: " + newStation.getStatus());
            } else {
                System.out.println("Failed to create station. Please try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Handles opening or closing a station
     */
    private void toggleStationStatus() {
        System.out.println("\n=== Open/Close Station ===");

        // Display stations
        List<Station> stations = stationService.getAllStations();

        if (stations.isEmpty()) {
            System.out.println("No stations found.");
            return;
        }

        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            System.out.println((i + 1) + ". Station " + station.getNumber() + " [" + station.getStatus() + "]");
        }

        // Get user selection
        System.out.print("Select a station to toggle (1-" + stations.size() + ") or 0 to cancel: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > stations.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Station selectedStation = stations.get(selection - 1);

            // Toggle status
            boolean success;
            if ("OPEN".equals(selectedStation.getStatus())) {
                success = stationService.closeStation(selectedStation.getId());
                if (success) {
                    System.out.println("Station " + selectedStation.getNumber() + " has been closed.");
                }
            } else {
                // Check if station has an employee assigned
                if (selectedStation.getAssignedEmployee() == null) {
                    System.out.println("Cannot open station without an assigned employee.");
                    return;
                }

                success = stationService.openStation(selectedStation.getId());
                if (success) {
                    System.out.println("Station " + selectedStation.getNumber() + " has been opened.");
                }
            }

            if (!success) {
                System.out.println("Failed to update station status. Please try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Handles assigning an employee to a station
     */
    private void assignEmployeeToStation() {
        System.out.println("\n=== Assign Employee to Station ===");

        // Display stations
        List<Station> stations = stationService.getAllStations();

        if (stations.isEmpty()) {
            System.out.println("No stations found.");
            return;
        }

        System.out.println("Select station:");
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            Employee currentEmployee = station.getAssignedEmployee();

            System.out.println((i + 1) + ". Station " + station.getNumber() + " - Employee: " +
                    (currentEmployee != null ? currentEmployee.getName() : "None"));
        }

        // Get station selection
        System.out.print("Select a station (1-" + stations.size() + ") or 0 to cancel: ");

        try {
            int stationSelection = Integer.parseInt(scanner.nextLine().trim());

            if (stationSelection == 0) {
                return;
            }

            if (stationSelection < 1 || stationSelection > stations.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Station selectedStation = stations.get(stationSelection - 1);

            // Get available employees
            List<Employee> employees = userService.getAllEmployees();

            if (employees.isEmpty()) {
                System.out.println("No employees found.");
                return;
            }

            System.out.println("\nSelect employee to assign:");
            System.out.println("0. Remove current assignment");

            for (int i = 0; i < employees.size(); i++) {
                Employee employee = employees.get(i);
                Station currentStation = employee.getAssignedStation();

                System.out.println((i + 1) + ". " + employee.getName() + " - Current station: " +
                        (currentStation != null ? currentStation.getNumber() : "None"));
            }

            // Get employee selection
            System.out.print("Select an employee (0-" + employees.size() + "): ");

            int employeeSelection = Integer.parseInt(scanner.nextLine().trim());

            if (employeeSelection < 0 || employeeSelection > employees.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            // Process the assignment
            boolean success;

            if (employeeSelection == 0) {
                // Remove assignment
                if (selectedStation.getAssignedEmployee() != null) {
                    Employee currentEmployee = selectedStation.getAssignedEmployee();
                    currentEmployee.setAssignedStation(null);
                    userService.updateUser(currentEmployee);

                    selectedStation.setAssignedEmployee(null);
                    success = stationService.getStationById(selectedStation.getId())
                            .map(s -> stationService.getAllStations().contains(s))
                            .orElse(false);

                    if (success) {
                        System.out.println("Employee removed from station " + selectedStation.getNumber() + ".");
                    }
                } else {
                    System.out.println("Station already has no assigned employee.");
                    return;
                }
            } else {
                // Assign employee
                Employee selectedEmployee = employees.get(employeeSelection - 1);

                success = stationService.assignEmployeeToStation(selectedStation.getId(), selectedEmployee.getId());

                if (success) {
                    System.out.println(selectedEmployee.getName() + " assigned to station " +
                            selectedStation.getNumber() + ".");
                }
            }

            if (!success) {
                System.out.println("Failed to update station assignment. Please try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Handles configuring categories supported by a station
     */
    private void configureStationCategories() {
        System.out.println("\n=== Configure Station Categories ===");

        // Display stations
        List<Station> stations = stationService.getAllStations();

        if (stations.isEmpty()) {
            System.out.println("No stations found.");
            return;
        }

        System.out.println("Select station:");
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            System.out.println((i + 1) + ". Station " + station.getNumber() + " - Categories: " +
                    station.getSupportedCategories().size());
        }

        // Get station selection
        System.out.print("Select a station (1-" + stations.size() + ") or 0 to cancel: ");

        try {
            int stationSelection = Integer.parseInt(scanner.nextLine().trim());

            if (stationSelection == 0) {
                return;
            }

            if (stationSelection < 1 || stationSelection > stations.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Station selectedStation = stations.get(stationSelection - 1);

            // Display category management options
            boolean continueConfig = true;

            while (continueConfig) {
                System.out.println("\nStation " + selectedStation.getNumber() + " - Category Configuration");
                System.out.println("1. View supported categories");
                System.out.println("2. Add category");
                System.out.println("3. Remove category");
                System.out.println("0. Back");

                System.out.print("Select an option: ");
                String option = scanner.nextLine().trim();

                switch (option) {
                    case "1":
                        viewStationCategories(selectedStation);
                        break;
                    case "2":
                        addCategoryToStation(selectedStation);
                        break;
                    case "3":
                        removeCategoryFromStation(selectedStation);
                        break;
                    case "0":
                        continueConfig = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Displays categories supported by a station
     *
     * @param station The station to display categories for
     */
    private void viewStationCategories(Station station) {
        System.out.println("\n=== Station " + station.getNumber() + " - Supported Categories ===");

        List<Category> categories = station.getSupportedCategories();

        if (categories.isEmpty()) {
            System.out.println("This station doesn't support any categories yet.");
            return;
        }

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " [" + category.getPrefix() + "]" +
                    (category.isActive() ? "" : " (Inactive)"));
        }
    }

    /**
     * Handles adding a category to a station
     *
     * @param station The station to add a category to
     */
    private void addCategoryToStation(Station station) {
        System.out.println("\n=== Add Category to Station " + station.getNumber() + " ===");

        // Get categories not already supported by this station
        List<Category> allCategories = categoryService.getAllCategories();
        List<Category> supportedCategories = station.getSupportedCategories();

        List<Category> availableCategories = allCategories.stream()
                .filter(c -> !supportedCategories.contains(c))
                .toList();

        if (availableCategories.isEmpty()) {
            System.out.println("There are no more categories available to add.");
            return;
        }

        System.out.println("Select a category to add:");
        for (int i = 0; i < availableCategories.size(); i++) {
            Category category = availableCategories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " [" + category.getPrefix() + "]" +
                    (category.isActive() ? "" : " (Inactive)"));
        }

        // Get category selection
        System.out.print("Select a category (1-" + availableCategories.size() + ") or 0 to cancel: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > availableCategories.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Category selectedCategory = availableCategories.get(selection - 1);

            if (stationService.addCategoryToStation(station.getId(), selectedCategory.getId())) {
                System.out.println("Category '" + selectedCategory.getName() +
                        "' has been added to Station " + station.getNumber() + ".");
            } else {
                System.out.println("Failed to add category to station. Please try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Handles removing a category from a station
     *
     * @param station The station to remove a category from
     */
    private void removeCategoryFromStation(Station station) {
        System.out.println("\n=== Remove Category from Station " + station.getNumber() + " ===");

        List<Category> supportedCategories = station.getSupportedCategories();

        if (supportedCategories.isEmpty()) {
            System.out.println("This station doesn't support any categories yet.");
            return;
        }

        System.out.println("Select a category to remove:");
        for (int i = 0; i < supportedCategories.size(); i++) {
            Category category = supportedCategories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " [" + category.getPrefix() + "]");
        }

        // Get category selection
        System.out.print("Select a category (1-" + supportedCategories.size() + ") or 0 to cancel: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > supportedCategories.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Category selectedCategory = supportedCategories.get(selection - 1);

            System.out.println("Are you sure you want to remove the category '" +
                    selectedCategory.getName() + "' from this station? (y/n)");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("y") || confirmation.equals("yes")) {
                if (stationService.removeCategoryFromStation(station.getId(), selectedCategory.getId())) {
                    System.out.println("Category '" + selectedCategory.getName() +
                            "' has been removed from Station " + station.getNumber() + ".");
                } else {
                    System.out.println("Failed to remove category from station. Please try again.");
                }
            } else {
                System.out.println("Operation cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Displays and handles the employees management submenu
     */
    private void manageEmployeesMenu() {
        boolean continueMenu = true;

        while (continueMenu) {
            System.out.println("\n=== Manage Employees ===");
            System.out.println("1. View all employees");
            System.out.println("2. Register new employee");
            System.out.println("3. Assign employee to category");
            System.out.println("4. Edit employee details");
            System.out.println("0. Back to main menu");

            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    viewAllEmployees();
                    break;
                case "2":
                    registerEmployee();
                    break;
                case "3":
                    assignEmployeeToCategory();
                    break;
                case "4":
                    editEmployeeDetails();
                    break;
                case "0":
                    continueMenu = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Displays all employees
     */
    private void viewAllEmployees() {
        System.out.println("\n=== All Employees ===");

        List<Employee> employees = userService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        for (Employee employee : employees) {
            System.out.println("ID: " + employee.getId());
            System.out.println("Name: " + employee.getName());
            System.out.println("Status: " + employee.getAvailabilityStatus());

            Station station = employee.getAssignedStation();
            System.out.println("Assigned station: " +
                    (station != null ? station.getNumber() : "None"));

            System.out.println("Tickets attended today: " + employee.getAttendedTickets().size());
            System.out.println("-----");
        }
    }

    /**
     * Handles registering a new employee
     */
    private void registerEmployee() {
        System.out.println("\n=== Register New Employee ===");

        System.out.print("Enter employee ID: ");
        String id = scanner.nextLine().trim();

        // Check if ID already exists
        if (userService.findUserById(id).isPresent()) {
            System.out.println("A user with this ID already exists. Please use a different ID.");
            return;
        }

        System.out.print("Enter employee name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter employee password: ");
        String password = scanner.nextLine().trim();

        if (id.isEmpty() || name.isEmpty() || password.isEmpty()) {
            System.out.println("ID, name, and password are required. Registration cancelled.");
            return;
        }

        Employee newEmployee = new Employee(id, name, password);

        if (userService.registerEmployee(newEmployee)) {
            System.out.println("Employee registered successfully!");
            System.out.println("ID: " + newEmployee.getId());
            System.out.println("Name: " + newEmployee.getName());
        } else {
            System.out.println("Failed to register employee. Please try again.");
        }
    }

    /**
     * Handles assigning an employee to a category
     */
    private void assignEmployeeToCategory() {
        System.out.println("\n=== Assign Employee to Category ===");

        // Display employees
        List<Employee> employees = userService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("Select employee:");
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            System.out.println((i + 1) + ". " + employee.getName());
        }

        // Get employee selection
        System.out.print("Select an employee (1-" + employees.size() + ") or 0 to cancel: ");

        try {
            int employeeSelection = Integer.parseInt(scanner.nextLine().trim());

            if (employeeSelection == 0) {
                return;
            }

            if (employeeSelection < 1 || employeeSelection > employees.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Employee selectedEmployee = employees.get(employeeSelection - 1);

            // Display categories
            List<Category> categories = categoryService.getAllCategories();

            if (categories.isEmpty()) {
                System.out.println("No categories found.");
                return;
            }

            System.out.println("\nSelect category to assign " + selectedEmployee.getName() + " to:");
            for (int i = 0; i < categories.size(); i++) {
                Category category = categories.get(i);
                boolean isAssigned = category.getAssignedEmployees().contains(selectedEmployee);

                System.out.println((i + 1) + ". " + category.getName() +
                        (isAssigned ? " [Already assigned]" : ""));
            }

            // Get category selection
            System.out.print("Select a category (1-" + categories.size() + ") or 0 to cancel: ");

            int categorySelection = Integer.parseInt(scanner.nextLine().trim());

            if (categorySelection == 0) {
                return;
            }

            if (categorySelection < 1 || categorySelection > categories.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Category selectedCategory = categories.get(categorySelection - 1);

            // Check if already assigned
            if (selectedCategory.getAssignedEmployees().contains(selectedEmployee)) {
                System.out.println("This employee is already assigned to this category.");
                System.out.println("Do you want to remove the assignment instead? (y/n)");

                String confirmation = scanner.nextLine().trim().toLowerCase();

                if (confirmation.equals("y") || confirmation.equals("yes")) {
                    if (categoryService.removeEmployeeFromCategory(selectedCategory.getId(), selectedEmployee)) {
                        System.out.println(selectedEmployee.getName() + " has been removed from category '" +
                                selectedCategory.getName() + "'.");
                    } else {
                        System.out.println("Failed to remove employee from category. Please try again.");
                    }
                } else {
                    System.out.println("Operation cancelled.");
                }
            } else {
                // Assign employee to category
                if (categoryService.assignEmployeeToCategory(selectedCategory.getId(), selectedEmployee)) {
                    System.out.println(selectedEmployee.getName() + " has been assigned to category '" +
                            selectedCategory.getName() + "'.");
                } else {
                    System.out.println("Failed to assign employee to category. Please try again.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Handles editing employee details
     */
    private void editEmployeeDetails() {
        System.out.println("\n=== Edit Employee Details ===");

        // Display employees
        List<Employee> employees = userService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("Select employee:");
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            System.out.println((i + 1) + ". " + employee.getName() + " (ID: " + employee.getId() + ")");
        }

        // Get employee selection
        System.out.print("Select an employee (1-" + employees.size() + ") or 0 to cancel: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > employees.size()) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            Employee selectedEmployee = employees.get(selection - 1);

            // Update fields
            System.out.println("Editing employee: " + selectedEmployee.getName());
            System.out.println("Leave field empty to keep current value");

            System.out.print("New name [" + selectedEmployee.getName() + "]: ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                selectedEmployee.setName(name);
            }

            System.out.print("New password (enter to skip): ");
            String password = scanner.nextLine().trim();
            if (!password.isEmpty()) {
                selectedEmployee.setPassword(password);
            }

            // Save changes
            if (userService.updateUser(selectedEmployee)) {
                System.out.println("Employee updated successfully!");
            } else {
                System.out.println("Failed to update employee. Please try again.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Displays system statistics
     */
    private void viewStatistics() {
        System.out.println("\n=== System Statistics ===");
        System.out.println(statisticsService.getCurrentStatistics().generateDailyStatistics());

        // Get additional statistics
        Map<String, Double> employeeProductivity = statisticsService.getEmployeeProductivityStatistics();

        if (!employeeProductivity.isEmpty()) {
            System.out.println("\nEmployee Productivity (tickets per hour):");

            for (Map.Entry<String, Double> entry : employeeProductivity.entrySet()) {
                Optional<Employee> employeeOpt = userService.getEmployeeById(entry.getKey());

                if (employeeOpt.isPresent()) {
                    System.out.println(employeeOpt.get().getName() + ": " +
                            String.format("%.2f", entry.getValue()) + " tickets/hour");
                }
            }
        }

        // Display category statistics
        List<Category> categories = categoryService.getAllCategories();

        if (!categories.isEmpty()) {
            System.out.println("\nCategory Statistics:");

            for (Category category : categories) {
                System.out.println(category.getName() + ":");
                System.out.println("  Active: " + (category.isActive() ? "Yes" : "No"));
                System.out.println("  Pending tickets: " + category.countPendingTickets());

                double avgWaitTime = statisticsService.getAverageWaitingTimeByCategory(category.getId());
                System.out.println("  Average waiting time: " + String.format("%.2f", avgWaitTime) + " minutes");
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Generates system reports
     */
    private void generateReports() {
        boolean continueMenu = true;

        while (continueMenu) {
            System.out.println("\n=== Generate Reports ===");
            System.out.println("1. Daily productivity report");
            System.out.println("2. Weekly productivity report");
            System.out.println("3. Monthly productivity report");
            System.out.println("4. Employee performance report");
            System.out.println("0. Back to main menu");

            System.out.print("Select an option: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    System.out.println("\n=== Daily Productivity Report ===");
                    System.out.println(statisticsService.generateDailyStatistics());
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    break;
                case "2":
                    System.out.println("\n=== Weekly Productivity Report ===");
                    System.out.println(statisticsService.generateWeeklyStatistics());
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    break;
                case "3":
                    System.out.println("\n=== Monthly Productivity Report ===");
                    System.out.println(statisticsService.generateMonthlyStatistics());
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    break;
                case "4":
                    generateEmployeePerformanceReport();
                    break;
                case "0":
                    continueMenu = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Generates a report on employee performance
     */
    private void generateEmployeePerformanceReport() {
        System.out.println("\n=== Employee Performance Report ===");

        List<Employee> employees = userService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No employees found.");
            return;
        }

        System.out.println("Performance metrics for today:");

        for (Employee employee : employees) {
            System.out.println("\nEmployee: " + employee.getName() + " (ID: " + employee.getId() + ")");
            System.out.println("Status: " + employee.getAvailabilityStatus());

            Station station = employee.getAssignedStation();
            System.out.println("Assigned station: " + (station != null ? station.getNumber() : "None"));

            List<Ticket> tickets = employee.getAttendedTickets();
            System.out.println("Tickets attended: " + tickets.size());

            if (!tickets.isEmpty()) {
                // Calculate metrics
                double totalServiceTime = 0;
                int completedTickets = 0;

                for (Ticket ticket : tickets) {
                    if ("COMPLETED".equals(ticket.getStatus())) {
                        totalServiceTime += ticket.calculateServiceTime();
                        completedTickets++;
                    }
                }

                if (completedTickets > 0) {
                    double averageServiceTime = totalServiceTime / completedTickets;
                    System.out.println("Average service time: " +
                            String.format("%.2f", averageServiceTime) + " minutes");

                    // Get productivity from statistics service
                    double productivity = statisticsService.getEmployeeProductivityStatistics()
                            .getOrDefault(employee.getId(), 0.0);

                    System.out.println("Productivity: " + String.format("%.2f", productivity) + " tickets per hour");
                } else {
                    System.out.println("No completed tickets to calculate metrics.");
                }
            }
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Starts the admin menu after authentication
     */
    public void start() {
        if (authenticate()) {
            start(scanner);
        }
    }
}