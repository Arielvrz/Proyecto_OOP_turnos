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
 * Menú para las interacciones del empleado con el sistema.
 */
public class EmployeeMenu implements Menu {
    private final Scanner scanner;
    private final UserService userService;
    private final TicketService ticketService;
    private final StationService stationService;
    private Employee currentEmployee;
    private Ticket currentTicket;

    /**
     * Constructor con dependencias
     *
     * @param scanner Scanner para leer la entrada del usuario
     * @param userService Servicio para operaciones de usuario
     * @param ticketService Servicio para operaciones de turno
     * @param stationService Servicio para operaciones de estación
     */
    public EmployeeMenu(Scanner scanner, UserService userService,
                        TicketService ticketService, StationService stationService) {
        this.scanner = scanner;
        this.userService = userService;
        this.ticketService = ticketService;
        this.stationService = stationService;
    }

    /**
     * Solicita al usuario que ingrese sus credenciales de empleado
     *
     * @return true si la autenticación fue exitosa, false en caso contrario
     */
    public boolean authenticate() {
        System.out.println("\n=== Inicio de Sesión de Empleado ===");

        System.out.print("Ingrese su ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Ingrese su contraseña: ");
        String password = scanner.nextLine().trim();

        // Agregar depuración
        System.out.println("Buscando empleado con ID: " + id);

        Optional<Employee> employeeOpt = userService.getEmployeeById(id);

        if (!employeeOpt.isPresent()) {
            System.out.println("Debug: No se encontró empleado con ID: " + id);
            System.out.println("Credenciales inválidas. Por favor, intente nuevamente.");
            return false;
        }

        Employee employee = employeeOpt.get();
        boolean loginSuccess = employee.login(id, password);

        if (!loginSuccess) {
            System.out.println("Debug: Se encontró empleado pero la contraseña no coincide");
            System.out.println("Credenciales inválidas. Por favor, intente nuevamente.");
            return false;
        }

        currentEmployee = employee;
        System.out.println("¡Inicio de sesión exitoso! Bienvenido, " + currentEmployee.getName() + "!");

        // Verificar si el empleado tiene una estación asignada
        if (currentEmployee.getAssignedStation() == null) {
            System.out.println("Advertencia: No tiene una estación asignada. Por favor contacte a un administrador.");
        } else {
            // Asegurarse de que el empleado esté disponible
            if (!"AVAILABLE".equals(currentEmployee.getAvailabilityStatus())) {
                System.out.println("Configurando su estado a DISPONIBLE...");
                currentEmployee.setAvailabilityStatus("AVAILABLE");
                userService.updateUser(currentEmployee);
            }
        }

        return true;
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Menú de Empleado ===");
        System.out.println("Estado actual: " + translateEmployeeStatus(currentEmployee.getAvailabilityStatus()));

        if (currentTicket != null) {
            System.out.println("Atendiendo actualmente: " + currentTicket.getCode());
        }

        System.out.println("1. Obtener siguiente cliente");
        System.out.println("2. Completar servicio actual");
        System.out.println("3. Ver información del cliente");
        System.out.println("4. Pausar/Reanudar asignación de turnos");
        System.out.println("5. Ver resumen diario");
        System.out.println("0. Cerrar sesión");
    }

    /**
     * Traduce los estados del empleado a español
     *
     * @param status El estado del empleado en inglés
     * @return El estado del empleado traducido al español
     */
    private String translateEmployeeStatus(String status) {
        switch (status) {
            case "AVAILABLE": return "Disponible";
            case "BUSY": return "Ocupado";
            case "PAUSED": return "Pausado";
            case "OFFLINE": return "Desconectado";
            default: return status;
        }
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
                System.out.println("Opción inválida. Por favor, intente nuevamente.");
                return true;
        }
    }

    /**
     * Maneja la obtención del siguiente cliente de la cola
     */
    private void getNextClient() {
        // Verificar si el empleado ya tiene un turno en progreso
        if (currentTicket != null && "IN_PROGRESS".equals(currentTicket.getStatus())) {
            System.out.println("Ya está atendiendo a un cliente (Turno: " + currentTicket.getCode() + ").");
            System.out.println("Por favor complete el servicio actual antes de obtener un nuevo cliente.");
            return;
        }

        // Verificar si el empleado está disponible
        if (!"AVAILABLE".equals(currentEmployee.getAvailabilityStatus())) {
            System.out.println("Actualmente está " + translateEmployeeStatus(currentEmployee.getAvailabilityStatus()) + ".");
            System.out.println("Por favor reanude la asignación de turnos antes de obtener un nuevo cliente.");
            return;
        }

        // Verificar si el empleado tiene una estación asignada
        if (currentEmployee.getAssignedStation() == null) {
            System.out.println("No tiene una estación asignada. Por favor contacte a un administrador.");
            return;
        }

        // Intentar obtener el siguiente turno
        Ticket nextTicket = ticketService.assignNextTicket(currentEmployee);

        if (nextTicket != null) {
            currentTicket = nextTicket;
            System.out.println("Nuevo cliente asignado:");
            System.out.println("Turno: " + currentTicket.getCode());
            System.out.println("Categoría: " + (currentTicket.getCategory() != null ?
                    currentTicket.getCategory().getName() : "N/A"));
            System.out.println("Tiempo de espera: " + currentTicket.calculateWaitingTime() + " minutos");
        } else {
            System.out.println("No hay clientes esperando en sus categorías asignadas.");
        }
    }

    /**
     * Maneja la finalización del turno de servicio actual
     */
    private void completeCurrentService() {
        if (currentTicket == null || !"IN_PROGRESS".equals(currentTicket.getStatus())) {
            System.out.println("No tiene un cliente activo para completar.");
            return;
        }

        System.out.println("Completando servicio para turno: " + currentTicket.getCode());

        if (ticketService.completeTicket(currentTicket, currentEmployee)) {
            System.out.println("Servicio completado exitosamente.");
            System.out.println("Tiempo de servicio: " + currentTicket.calculateServiceTime() + " minutos");
            currentTicket = null; // Limpiar el turno actual
        } else {
            System.out.println("Error al completar el servicio. Por favor, intente nuevamente.");
        }
    }

    /**
     * Muestra información sobre el cliente actual
     */
    private void viewClientInformation() {
        if (currentTicket == null) {
            System.out.println("No tiene un cliente asignado.");
            return;
        }

        System.out.println("\n=== Información del Cliente ===");
        System.out.println(currentEmployee.getClientInformation(currentTicket));

        // Obtener información más detallada si es necesario
        Optional<Ticket> ticketDetails = ticketService.getTicketByCode(currentTicket.getCode());

        if (ticketDetails.isPresent()) {
            Ticket ticket = ticketDetails.get();

            if ("IN_PROGRESS".equals(ticket.getStatus())) {
                System.out.println("Tiempo de servicio actual: " + ticket.calculateServiceTime() + " minutos");
            }
        }
    }

    /**
     * Alterna el estado de asignación de turnos (pausar/reanudar)
     */
    private void toggleAssignmentStatus() {
        if ("AVAILABLE".equals(currentEmployee.getAvailabilityStatus()) ||
                "OFFLINE".equals(currentEmployee.getAvailabilityStatus())) {

            // Pausar asignación
            if (currentEmployee.pauseAssignment()) {
                System.out.println("Asignación de turnos pausada. No recibirá nuevos clientes.");
                userService.updateUser(currentEmployee);
            } else {
                System.out.println("No se puede pausar la asignación en este momento.");
            }

        } else if ("PAUSED".equals(currentEmployee.getAvailabilityStatus())) {

            // Reanudar asignación
            if (currentEmployee.resumeAttention()) {
                System.out.println("Asignación de turnos reanudada. Ahora puede recibir nuevos clientes.");
                userService.updateUser(currentEmployee);
            } else {
                System.out.println("No se puede reanudar la asignación en este momento.");
            }

        } else {
            System.out.println("No puede cambiar su estado mientras atiende a un cliente.");
        }
    }

    /**
     * Muestra un resumen de los turnos atendidos durante el día
     */
    private void viewDailySummary() {
        System.out.println("\n=== Resumen Diario ===");
        System.out.println(currentEmployee.getAttentionSummary());

        List<Ticket> attendedTickets = currentEmployee.getAttendedTickets();

        if (attendedTickets.isEmpty()) {
            System.out.println("No ha atendido a ningún cliente hoy.");
            return;
        }

        System.out.println("Turnos atendidos hoy: " + attendedTickets.size());

        // Calcular tiempo promedio de servicio
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
            System.out.println("Tiempo promedio de servicio: " + String.format("%.2f", averageServiceTime) + " minutos");
        }
    }

    /**
     * Maneja el cierre de sesión del empleado
     */
    private void logout() {
        // Verificar si hay un turno en progreso
        if (currentTicket != null && "IN_PROGRESS".equals(currentTicket.getStatus())) {
            System.out.println("Advertencia: Tiene un cliente activo. Por favor complete el servicio antes de cerrar sesión.");
            System.out.println("¿Realmente desea cerrar sesión? (s/n)");

            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!confirmation.equals("s") && !confirmation.equals("si")) {
                System.out.println("Cierre de sesión cancelado.");
                return;
            }
        }

        // Establecer el estado del empleado como desconectado
        currentEmployee.setAvailabilityStatus("OFFLINE");
        userService.updateUser(currentEmployee);

        System.out.println("Ha cerrado sesión. ¡Hasta pronto, " + currentEmployee.getName() + "!");
    }

    /**
     * Inicia el menú del empleado después de la autenticación
     */
    public void start() {
        if (authenticate()) {
            start(scanner);
        }
    }
}