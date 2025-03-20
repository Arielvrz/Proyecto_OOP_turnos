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
 * Menú para las interacciones del cliente con el sistema.
 */
public class ClientMenu implements Menu {
    private final Scanner scanner;
    private final ClientService clientService;
    private final CategoryService categoryService;
    private final TicketService ticketService;
    private Client currentClient;

    /**
     * Constructor con dependencias
     *
     * @param scanner Scanner para leer la entrada del usuario
     * @param clientService Servicio para operaciones de cliente
     * @param categoryService Servicio para operaciones de categoría
     * @param ticketService Servicio para operaciones de turno
     */
    public ClientMenu(Scanner scanner, ClientService clientService,
                      CategoryService categoryService, TicketService ticketService) {
        this.scanner = scanner;
        this.clientService = clientService;
        this.categoryService = categoryService;
        this.ticketService = ticketService;
    }

    /**
     * Solicita al usuario que ingrese su ID de cliente o cree un nuevo perfil de cliente
     *
     * @return true si se identificó o creó un cliente, false en caso contrario
     */
    public boolean identifyClient() {
        System.out.println("\n=== Identificación de Cliente ===");
        System.out.println("1. Ingresar ID de cliente existente");
        System.out.println("2. Registrarse como nuevo cliente");
        System.out.println("0. Volver al menú principal");

        System.out.print("Seleccione una opción: ");
        String option = scanner.nextLine().trim();

        switch (option) {
            case "1":
                return loginExistingClient();
            case "2":
                return registerNewClient();
            case "0":
                return false;
            default:
                System.out.println("Opción inválida. Por favor intente nuevamente.");
                return identifyClient();
        }
    }

    /**
     * Maneja el inicio de sesión de un cliente existente
     *
     * @return true si el inicio de sesión fue exitoso, false en caso contrario
     */
    private boolean loginExistingClient() {
        System.out.print("Ingrese su ID de cliente: ");
        String clientId = scanner.nextLine().trim();

        Optional<Client> clientOpt = clientService.getClientById(clientId);

        if (clientOpt.isPresent()) {
            this.currentClient = clientOpt.get();
            System.out.println("¡Bienvenido de nuevo, " + currentClient.getName() + "!");
            return true;
        } else {
            System.out.println("Cliente no encontrado. ¿Desea registrarse como nuevo cliente? (s/n)");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("s") || response.equals("si")) {
                return registerNewClient();
            } else {
                return false;
            }
        }
    }

    /**
     * Maneja el registro de un nuevo cliente
     *
     * @return true si el registro fue exitoso, false en caso contrario
     */
    private boolean registerNewClient() {
        System.out.println("\n=== Registro de Nuevo Cliente ===");

        System.out.print("Ingrese su ID (ej. DUI, pasaporte): ");
        String id = scanner.nextLine().trim();

        // Verificar si el cliente ya existe
        if (clientService.getClientById(id).isPresent()) {
            System.out.println("Ya existe un cliente con este ID. Por favor inicie sesión en su lugar.");
            return loginExistingClient();
        }

        System.out.print("Ingrese su nombre completo: ");
        String name = scanner.nextLine().trim();

        System.out.print("Ingrese información de contacto (teléfono o correo): ");
        String contactInfo = scanner.nextLine().trim();

        Client newClient = new Client(id, name, contactInfo);

        if (clientService.registerClient(newClient)) {
            this.currentClient = newClient;
            System.out.println("¡Registro exitoso! Bienvenido, " + name + "!");
            return true;
        } else {
            System.out.println("Error en el registro. Por favor intente más tarde.");
            return false;
        }
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Menú de Cliente ===");
        System.out.println("1. Solicitar un nuevo turno");
        System.out.println("2. Verificar estado de la cola");
        System.out.println("3. Ver mis turnos");
        System.out.println("4. Cancelar un turno");
        System.out.println("0. Salir");
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
                System.out.println("Gracias por usar nuestro servicio. ¡Hasta pronto!");
                return false;
            default:
                System.out.println("Opción inválida. Por favor, intente nuevamente.");
                return true;
        }
    }

    /**
     * Maneja el proceso de solicitar un nuevo turno
     */
    private void requestTicket() {
        System.out.println("\n=== Solicitar un Turno ===");

        // Obtener categorías activas
        List<Category> activeCategories = categoryService.getAllActiveCategories();

        if (activeCategories.isEmpty()) {
            System.out.println("Lo sentimos, no hay categorías de servicio activas en este momento.");
            return;
        }

        // Mostrar categorías
        System.out.println("Categorías de servicio disponibles:");
        for (int i = 0; i < activeCategories.size(); i++) {
            Category category = activeCategories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " - " + category.getDescription());
        }

        // Obtener selección del usuario
        System.out.print("Seleccione una categoría (1-" + activeCategories.size() + "): ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection < 1 || selection > activeCategories.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Category selectedCategory = activeCategories.get(selection - 1);

            // Crear el turno
            Ticket ticket = ticketService.createTicket(currentClient.getId(), selectedCategory.getId());

            if (ticket != null) {
                System.out.println("¡Turno creado exitosamente!");
                System.out.println("Su código de turno es: " + ticket.getCode());
                System.out.println("Categoría: " + selectedCategory.getName());
                System.out.println("Posición actual en cola: " + ticketService.getTicketQueuePosition(ticket.getCode()));
            } else {
                System.out.println("Error al crear el turno. Por favor intente más tarde.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Maneja el proceso de verificar el estado de una cola
     */
    private void checkQueueStatus() {
        System.out.println("\n=== Estado de la Cola ===");

        // Obtener categorías activas
        List<Category> categories = categoryService.getAllActiveCategories();

        if (categories.isEmpty()) {
            System.out.println("No hay categorías de servicio activas en este momento.");
            return;
        }

        // Mostrar estado de cola para cada categoría
        for (Category category : categories) {
            int pendingTickets = category.countPendingTickets();
            System.out.println(category.getName() + ": " + pendingTickets + " turnos en espera");

            // Opción para ver más detalles
            if (pendingTickets > 0) {
                System.out.println("  * Tiempo estimado de espera: ~" + (pendingTickets * 10) + " minutos");
            }
        }
    }

    /**
     * Muestra los turnos del cliente y su estado
     */
    private void viewMyTickets() {
        System.out.println("\n=== Mis Turnos ===");

        List<Ticket> clientTickets = ticketService.getTicketsByClient(currentClient.getId());

        if (clientTickets.isEmpty()) {
            System.out.println("No tiene turnos.");
            return;
        }

        for (Ticket ticket : clientTickets) {
            System.out.println("Código: " + ticket.getCode());
            System.out.println("  Categoría: " + (ticket.getCategory() != null ? ticket.getCategory().getName() : "N/A"));
            System.out.println("  Estado: " + translateStatus(ticket.getStatus()));
            System.out.println("  Generado: " + ticket.getGenerationTime());

            if ("WAITING".equals(ticket.getStatus())) {
                int position = ticketService.getTicketQueuePosition(ticket.getCode());
                System.out.println("  Posición en cola: " + (position > 0 ? position : "Desconocida"));
            }

            if (ticket.getAttentionTime() != null) {
                System.out.println("  Inicio de atención: " + ticket.getAttentionTime());
            }

            if (ticket.getCompletionTime() != null) {
                System.out.println("  Completado: " + ticket.getCompletionTime());
            }

            System.out.println("  Tiempo de espera: " + ticket.calculateWaitingTime() + " minutos");

            if ("IN_PROGRESS".equals(ticket.getStatus()) || "COMPLETED".equals(ticket.getStatus())) {
                System.out.println("  Tiempo de servicio: " + ticket.calculateServiceTime() + " minutos");
            }

            System.out.println("-----");
        }
    }

    /**
     * Traduce los estados del turno a español
     *
     * @param status El estado del turno en inglés
     * @return El estado del turno traducido al español
     */
    private String translateStatus(String status) {
        switch (status) {
            case "WAITING": return "En espera";
            case "IN_PROGRESS": return "En atención";
            case "COMPLETED": return "Completado";
            case "CANCELLED": return "Cancelado";
            default: return status;
        }
    }

    /**
     * Maneja el proceso de cancelar un turno
     */
    private void cancelTicket() {
        System.out.println("\n=== Cancelar un Turno ===");

        List<Ticket> waitingTickets = ticketService.getTicketsByClient(currentClient.getId()).stream()
                .filter(ticket -> "WAITING".equals(ticket.getStatus()))
                .toList();

        if (waitingTickets.isEmpty()) {
            System.out.println("No tiene turnos en espera que puedan ser cancelados.");
            return;
        }

        System.out.println("Sus turnos en espera:");
        for (int i = 0; i < waitingTickets.size(); i++) {
            Ticket ticket = waitingTickets.get(i);
            System.out.println((i + 1) + ". " + ticket.getCode() + " - " +
                    (ticket.getCategory() != null ? ticket.getCategory().getName() : "N/A"));
        }

        System.out.print("Seleccione un turno para cancelar (1-" + waitingTickets.size() + ") o 0 para volver: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > waitingTickets.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Ticket selectedTicket = waitingTickets.get(selection - 1);

            System.out.println("¿Está seguro que desea cancelar el turno " + selectedTicket.getCode() + "? (s/n)");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("s") || confirmation.equals("si")) {
                if (ticketService.cancelTicket(selectedTicket.getCode())) {
                    System.out.println("Turno cancelado exitosamente.");
                } else {
                    System.out.println("Error al cancelar el turno. Por favor intente más tarde.");
                }
            } else {
                System.out.println("Cancelación abortada.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Inicia el menú del cliente después de identificar al cliente
     */
    public void start() {
        if (identifyClient()) {
            start(scanner);
        }
    }
}