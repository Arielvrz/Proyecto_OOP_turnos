package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Client;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.repository.CategoryRepository;
import com.queuemanagementsystem.repository.ClientRepository;
import com.queuemanagementsystem.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Clase de servicio para gestionar tickets.
 */
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ClientRepository clientRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;

    /**
     * Constructor con dependencias de repositorios
     *
     * @param ticketRepository Repositorio para datos de tickets
     * @param clientRepository Repositorio para datos de clientes
     * @param categoryRepository Repositorio para datos de categorías
     * @param notificationService Servicio para enviar notificaciones
     */
    public TicketService(TicketRepository ticketRepository, ClientRepository clientRepository,
                         CategoryRepository categoryRepository, NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.clientRepository = clientRepository;
        this.categoryRepository = categoryRepository;
        this.notificationService = notificationService;
    }

    /**
     * Crea un nuevo ticket para un cliente
     *
     * @param clientId El ID del cliente
     * @param categoryId El ID de la categoría de servicio
     * @return El ticket creado si fue exitoso, null en caso contrario
     */
    public Ticket createTicket(String clientId, int categoryId) {
        Optional<Client> clientOpt = clientRepository.findById(clientId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!clientOpt.isPresent() || !categoryOpt.isPresent()) {
            return null;
        }

        Client client = clientOpt.get();
        Category category = categoryOpt.get();

        if (!category.isActive()) {
            return null;
        }

        Ticket ticket = new Ticket(category, clientId);

        if (ticketRepository.save(ticket)) {
            // Añade el ticket a la cola de la categoría
            category.addTicketToQueue(ticket);
            categoryRepository.update(category);
            return ticket;
        }

        return null;
    }

    /**
     * Asigna el siguiente ticket a un empleado
     *
     * @param employee El empleado al que asignar el ticket
     * @return El ticket asignado si fue exitoso, null en caso contrario
     */
    public Ticket assignNextTicket(Employee employee) {
        if (employee == null || !"AVAILABLE".equals(employee.getAvailabilityStatus())) {
            return null;
        }

        // Verifica si la estación del empleado soporta alguna categoría
        if (employee.getAssignedStation() == null) {
            return null;
        }

        // Obtiene los IDs de categoría de la estación
        List<Integer> categoryIds = employee.getAssignedStation().getSupportedCategoryIds();

        // Crea una lista para los objetos de categoría actuales
        List<Category> supportedCategories = new ArrayList<>();

        // Resuelve cada ID de categoría a su objeto actual
        for (Integer categoryId : categoryIds) {
            categoryRepository.findById(categoryId).ifPresent(supportedCategories::add);
        }

        // Encuentra el siguiente ticket de cualquier categoría soportada
        for (Category category : supportedCategories) {
            Ticket nextTicket = category.getNextTicket();

            if (nextTicket != null) {
                if (employee.attendNextClient(nextTicket)) {
                    // Actualiza el estado del ticket
                    nextTicket.changeStatus("IN_PROGRESS");
                    nextTicket.setAttentionTime(LocalDateTime.now());

                    // Actualiza el ticket en el repositorio
                    ticketRepository.update(nextTicket);

                    // Envía notificación
                    notificationService.notifyClientTicketInProgress(nextTicket,
                            employee.getAssignedStation().getNumber());

                    return nextTicket;
                }
            }
        }

        return null; // No se encontraron tickets en ninguna categoría
    }

    /**
     * Completa un ticket que está siendo atendido por un empleado
     *
     * @param ticket El ticket a completar
     * @param employee El empleado que atiende el ticket
     * @return true si el ticket se completó con éxito, false en caso contrario
     */
    public boolean completeTicket(Ticket ticket, Employee employee) {
        if (ticket == null || employee == null ||
                !"IN_PROGRESS".equals(ticket.getStatus())) {
            return false;
        }

        if (employee.markTicketAsCompleted(ticket)) {
            ticket.changeStatus("COMPLETED");
            ticket.setCompletionTime(LocalDateTime.now());
            return ticketRepository.update(ticket);
        }

        return false;
    }

    /**
     * Cancela un ticket
     *
     * @param ticketCode El código del ticket a cancelar
     * @return true si el ticket fue cancelado con éxito, false en caso contrario
     */
    public boolean cancelTicket(String ticketCode) {
        Optional<Ticket> ticketOpt = ticketRepository.findByCode(ticketCode);

        if (!ticketOpt.isPresent() || !"WAITING".equals(ticketOpt.get().getStatus())) {
            return false;
        }

        Ticket ticket = ticketOpt.get();
        ticket.setStatus("CANCELLED");

        // Elimina de la cola de categoría (esto necesitaría ser implementado en Category)
        Category category = ticket.getCategory();
        if (category != null) {
            // Aquí asumimos que hay un método para eliminar un ticket específico de la cola
            // En un sistema real, podrías necesitar un enfoque más sofisticado
            category.peekTicketQueue().remove(ticket);
            categoryRepository.update(category);
        }

        return ticketRepository.update(ticket);
    }

    /**
     * Obtiene todos los tickets en espera para una categoría específica
     *
     * @param categoryId El ID de la categoría
     * @return Lista de tickets en espera
     */
    public List<Ticket> getWaitingTicketsByCategory(int categoryId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getCategory() != null &&
                        ticket.getCategory().getId() == categoryId &&
                        "WAITING".equals(ticket.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los tickets para un cliente específico
     *
     * @param clientId El ID del cliente
     * @return Lista de tickets del cliente
     */
    public List<Ticket> getTicketsByClient(String clientId) {
        return ticketRepository.findByClientId(clientId);
    }

    /**
     * Obtiene todos los tickets atendidos por un empleado específico
     *
     * @param employeeId El ID del empleado
     * @return Lista de tickets atendidos por el empleado
     */
    public List<Ticket> getTicketsAttendedByEmployee(String employeeId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> (ticket.getStatus().equals("IN_PROGRESS") ||
                        ticket.getStatus().equals("COMPLETED")))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un ticket por su código
     *
     * @param code El código del ticket
     * @return Optional que contiene el ticket si se encuentra, vacío en caso contrario
     */
    public Optional<Ticket> getTicketByCode(String code) {
        return ticketRepository.findByCode(code);
    }

    /**
     * Obtiene la posición en cola de un ticket en espera
     *
     * @param ticketCode El código del ticket
     * @return La posición en la cola (empezando en 1) o -1 si no se encuentra o no está en espera
     */
    public int getTicketQueuePosition(String ticketCode) {
        Optional<Ticket> ticketOpt = ticketRepository.findByCode(ticketCode);

        if (!ticketOpt.isPresent() || !"WAITING".equals(ticketOpt.get().getStatus())) {
            return -1;
        }

        Ticket ticket = ticketOpt.get();
        Category category = ticket.getCategory();

        if (category == null) {
            return -1;
        }

        List<Ticket> queue = category.peekTicketQueue();
        for (int i = 0; i < queue.size(); i++) {
            if (ticket.equals(queue.get(i))) {
                return i + 1; // Posición basada en 1
            }
        }

        return -1; // No encontrado en la cola
    }
}