package com.queuemanagementsystem;

import com.queuemanagementsystem.model.*;
import com.queuemanagementsystem.repository.*;
import com.queuemanagementsystem.service.*;
import com.queuemanagementsystem.ui.MainMenu;
import com.queuemanagementsystem.util.DateTimeUtil;

import java.util.Scanner;

/**
 * Main class for the Queue Management System.
 * Initializes all necessary components and starts the application.
 */
public class Main {
    /**
     * Main method to start the application
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Create repositories
        UserRepository userRepository = new JsonUserRepository();
        ClientRepository clientRepository = new JsonClientRepository();
        CategoryRepository categoryRepository = new JsonCategoryRepository();
        StationRepository stationRepository = new JsonStationRepository();
        TicketRepository ticketRepository = new JsonTicketRepository();

        // Create utility classes
        DateTimeUtil dateTimeUtil = new DateTimeUtil();
        NotificationSystem notificationSystem = new NotificationSystem();

        // Create services
        UserService userService = new UserService(userRepository);
        ClientService clientService = new ClientService(clientRepository);
        NotificationService notificationService = new NotificationService(notificationSystem, clientRepository);
        CategoryService categoryService = new CategoryService(categoryRepository);
        StationService stationService = new StationService(stationRepository, userRepository, categoryRepository);
        TicketService ticketService = new TicketService(ticketRepository, clientRepository,
                categoryRepository, notificationService);
        StatisticsService statisticsService = new StatisticsService(ticketRepository, userRepository,
                categoryRepository, dateTimeUtil);

        // Create scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Create and start main menu
        MainMenu mainMenu = new MainMenu(scanner, clientService, categoryService,
                ticketService, userService, stationService, statisticsService);

        // Initialize sample data if repositories are empty
        initializeSampleData(userRepository, clientRepository, categoryRepository, stationRepository);

        // resolver referencias
        stationService.resolveReferences();

        // Start the application
        mainMenu.start(scanner);

        // Close resources
        scanner.close();
    }

    /**
     * Initializes sample data if the repositories are empty
     *
     * @param userRepository Repository for user data
     * @param clientRepository Repository for client data
     * @param categoryRepository Repository for category data
     * @param stationRepository Repository for station data
     */
    private static void initializeSampleData(UserRepository userRepository, ClientRepository clientRepository,
                                             CategoryRepository categoryRepository, StationRepository stationRepository) {
        // Initialize sample data only if repositories are empty
        if (userRepository.findAll().isEmpty() && clientRepository.findAll().isEmpty() &&
                categoryRepository.findAll().isEmpty() && stationRepository.findAll().isEmpty()) {

            System.out.println("Initializing sample data...");

            // Create sample administrator
            Administrator admin = new Administrator("admin", "System Administrator", "admin123", 3);
            userRepository.save(admin);

            // Create sample categories
            Category generalCategory = new Category(1, "General Inquiry", "General customer inquiries", "GEN", true);
            Category billingCategory = new Category(2, "Billing", "Billing and payment inquiries", "BIL", true);
            Category technicalCategory = new Category(3, "Technical Support", "Technical issues and support", "TEC", true);
            Category complaintsCategory = new Category(4, "Complaints", "Customer complaints handling", "COM", true);

            categoryRepository.save(generalCategory);
            categoryRepository.save(billingCategory);
            categoryRepository.save(technicalCategory);
            categoryRepository.save(complaintsCategory);

            // Create sample stations
            Station station1 = new Station(1, 1);
            Station station2 = new Station(2, 2);
            Station station3 = new Station(3, 3);

            stationRepository.save(station1);
            stationRepository.save(station2);
            stationRepository.save(station3);

            // Add categories to stations
            station1.addCategory(generalCategory);
            station1.addCategory(billingCategory);
            station2.addCategory(technicalCategory);
            station3.addCategory(complaintsCategory);
            station3.addCategory(generalCategory);

            stationRepository.update(station1);
            stationRepository.update(station2);
            stationRepository.update(station3);

            // Create sample employees
            Employee employee1 = new Employee("emp1", "John Smith", "pass123", "OFFLINE", station1);
            Employee employee2 = new Employee("emp2", "Maria García", "pass123", "OFFLINE", station2);
            Employee employee3 = new Employee("emp3", "Carlos Rodríguez", "pass123", "OFFLINE", station3);

            userRepository.save(employee1);
            userRepository.save(employee2);
            userRepository.save(employee3);

            // Add employees to categories
            generalCategory.assignEmployee(employee1);
            billingCategory.assignEmployee(employee1);
            technicalCategory.assignEmployee(employee2);
            complaintsCategory.assignEmployee(employee3);
            generalCategory.assignEmployee(employee3);

            categoryRepository.update(generalCategory);
            categoryRepository.update(billingCategory);
            categoryRepository.update(technicalCategory);
            categoryRepository.update(complaintsCategory);

            // Create sample clients
            Client client1 = new Client("C001", "Ana Martinez", "ana@example.com");
            Client client2 = new Client("C002", "Luis Perez", "luis@example.com");
            Client client3 = new Client("C003", "Sofia Gutierrez", "sofia@example.com");

            clientRepository.save(client1);
            clientRepository.save(client2);
            clientRepository.save(client3);

            System.out.println("Sample data initialized successfully!");
            System.out.println("Administrator Login - ID: admin, Password: admin123");
            System.out.println("Employee Logins - IDs: emp1, emp2, emp3, Password: pass123");
            System.out.println();
        }
    }
}