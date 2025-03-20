package com.queuemanagementsystem.ui;

import com.queuemanagementsystem.model.Administrator;
import com.queuemanagementsystem.model.Employee;

import com.queuemanagementsystem.service.*;
import java.util.Scanner;

/**
 * Menú principal para el Sistema de Gestión de Turnos.
 * Muestra las opciones iniciales para los diferentes roles de usuario.
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
     * Constructor con dependencias
     *
     * @param scanner Scanner para leer la entrada del usuario
     * @param clientService Servicio para operaciones de cliente
     * @param categoryService Servicio para operaciones de categoría
     * @param ticketService Servicio para operaciones de turno
     * @param userService Servicio para operaciones de usuario
     * @param stationService Servicio para operaciones de estación
     * @param statisticsService Servicio para operaciones de estadísticas
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
        System.out.println("\n=== Sistema de Gestión de Turnos ===");
        System.out.println("1. Cliente");
        System.out.println("2. Empleado");
        System.out.println("3. Administrador");
        System.out.println("0. Salir");
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
                System.out.println("Gracias por usar el Sistema de Gestión de Turnos. ¡Hasta pronto!");
                return false;
            default:
                System.out.println("Opción inválida. Por favor, intente nuevamente.");
                return true;
        }
    }
}