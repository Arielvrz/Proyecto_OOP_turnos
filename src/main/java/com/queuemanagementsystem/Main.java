package com.queuemanagementsystem;

import com.queuemanagementsystem.model.*;
import com.queuemanagementsystem.repository.*;
import com.queuemanagementsystem.service.*;
import com.queuemanagementsystem.ui.MainMenu;
import com.queuemanagementsystem.util.DateTimeUtil;

import java.util.Scanner;

/**
 * Clase principal para el Sistema de Gestión de Colas.
 * Inicializa todos los componentes necesarios y arranca la aplicación.
 */
public class Main {
    /**
     * Método principal para iniciar la aplicación
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Crear repositorios
        UserRepository userRepository = new JsonUserRepository();
        ClientRepository clientRepository = new JsonClientRepository();
        CategoryRepository categoryRepository = new JsonCategoryRepository();
        StationRepository stationRepository = new JsonStationRepository();
        TicketRepository ticketRepository = new JsonTicketRepository();

        // Crear clases de utilidad
        DateTimeUtil dateTimeUtil = new DateTimeUtil();
        NotificationSystem notificationSystem = new NotificationSystem();

        // Crear servicios
        UserService userService = new UserService(userRepository);
        ClientService clientService = new ClientService(clientRepository);
        NotificationService notificationService = new NotificationService(notificationSystem, clientRepository);
        CategoryService categoryService = new CategoryService(categoryRepository);
        StationService stationService = new StationService(stationRepository, userRepository, categoryRepository);
        TicketService ticketService = new TicketService(ticketRepository, clientRepository,
                categoryRepository, notificationService);
        StatisticsService statisticsService = new StatisticsService(ticketRepository, userRepository,
                categoryRepository, dateTimeUtil);

        // Crear scanner para entrada de usuario
        Scanner scanner = new Scanner(System.in);

        // Crear e iniciar menú principal
        MainMenu mainMenu = new MainMenu(scanner, clientService, categoryService,
                ticketService, userService, stationService, statisticsService);

        // Inicializar datos de ejemplo si los repositorios están vacíos
        initializeSampleData(userRepository, clientRepository, categoryRepository, stationRepository);

        // resolver referencias
        stationService.resolveReferences();

        // Iniciar la aplicación
        mainMenu.start(scanner);

        // Cerrar recursos
        scanner.close();
    }

    /**
     * Inicializa datos de ejemplo si los repositorios están vacíos
     *
     * @param userRepository Repositorio para datos de usuarios
     * @param clientRepository Repositorio para datos de clientes
     * @param categoryRepository Repositorio para datos de categorías
     * @param stationRepository Repositorio para datos de estaciones
     */
    private static void initializeSampleData(UserRepository userRepository, ClientRepository clientRepository,
                                             CategoryRepository categoryRepository, StationRepository stationRepository) {
        // Inicializar datos de ejemplo sólo si los repositorios están vacíos
        if (userRepository.findAll().isEmpty() && clientRepository.findAll().isEmpty() &&
                categoryRepository.findAll().isEmpty() && stationRepository.findAll().isEmpty()) {

            System.out.println("Inicializando datos de ejemplo...");

            // Crear administrador de ejemplo
            Administrator admin = new Administrator("admin", "Administrador del Sistema", "admin123", 3);
            userRepository.save(admin);

            // Crear categorías de ejemplo
            Category generalCategory = new Category(1, "Consulta General", "Consultas generales de clientes", "GEN", true);
            Category billingCategory = new Category(2, "Facturación", "Consultas de facturación y pagos", "BIL", true);
            Category technicalCategory = new Category(3, "Soporte Técnico", "Problemas técnicos y soporte", "TEC", true);
            Category complaintsCategory = new Category(4, "Reclamos", "Gestión de reclamos de clientes", "COM", true);

            categoryRepository.save(generalCategory);
            categoryRepository.save(billingCategory);
            categoryRepository.save(technicalCategory);
            categoryRepository.save(complaintsCategory);

            // Crear estaciones de ejemplo
            Station station1 = new Station(1, 1);
            Station station2 = new Station(2, 2);
            Station station3 = new Station(3, 3);

            stationRepository.save(station1);
            stationRepository.save(station2);
            stationRepository.save(station3);

            // Añadir categorías a las estaciones
            station1.addCategory(generalCategory);
            station1.addCategory(billingCategory);
            station2.addCategory(technicalCategory);
            station3.addCategory(complaintsCategory);
            station3.addCategory(generalCategory);

            stationRepository.update(station1);
            stationRepository.update(station2);
            stationRepository.update(station3);

            // Crear empleados de ejemplo
            Employee employee1 = new Employee("emp1", "Juan Pérez", "pass123", "OFFLINE", station1);
            Employee employee2 = new Employee("emp2", "María García", "pass123", "OFFLINE", station2);
            Employee employee3 = new Employee("emp3", "Carlos Rodríguez", "pass123", "OFFLINE", station3);

            userRepository.save(employee1);
            userRepository.save(employee2);
            userRepository.save(employee3);

            // Añadir empleados a categorías
            generalCategory.assignEmployee(employee1);
            billingCategory.assignEmployee(employee1);
            technicalCategory.assignEmployee(employee2);
            complaintsCategory.assignEmployee(employee3);
            generalCategory.assignEmployee(employee3);

            categoryRepository.update(generalCategory);
            categoryRepository.update(billingCategory);
            categoryRepository.update(technicalCategory);
            categoryRepository.update(complaintsCategory);

            // Crear clientes de ejemplo
            Client client1 = new Client("C001", "Ana Martínez", "ana@example.com");
            Client client2 = new Client("C002", "Luis Pérez", "luis@example.com");
            Client client3 = new Client("C003", "Sofía Gutiérrez", "sofia@example.com");

            clientRepository.save(client1);
            clientRepository.save(client2);
            clientRepository.save(client3);

            System.out.println("Datos de ejemplo inicializados correctamente!");
            System.out.println("Login de Administrador - ID: admin, Contraseña: admin123");
            System.out.println("Logins de Empleados - IDs: emp1, emp2, emp3, Contraseña: pass123");
            System.out.println();
        }
    }
}