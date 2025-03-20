package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Client;
import com.queuemanagementsystem.model.NotificationSystem;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.repository.ClientRepository;

import java.util.Optional;

/**
 * Clase de servicio para manejar notificaciones a clientes y pantallas.
 */
public class NotificationService {
    private final NotificationSystem notificationSystem;
    private final ClientRepository clientRepository;

    /**
     * Constructor con dependencias
     *
     * @param notificationSystem El sistema de notificaciones para pantallas
     * @param clientRepository Repositorio para datos de clientes
     */
    public NotificationService(NotificationSystem notificationSystem, ClientRepository clientRepository) {
        this.notificationSystem = notificationSystem;
        this.clientRepository = clientRepository;
    }

    /**
     * Notifica a un cliente que su ticket está siendo procesado
     *
     * @param ticket El ticket que ahora está en progreso
     * @param stationNumber El número de estación donde debe dirigirse el cliente
     * @return true si la notificación se envió correctamente, false en caso contrario
     */
    public boolean notifyClientTicketInProgress(Ticket ticket, int stationNumber) {
        if (ticket == null) {
            return false;
        }

        // Actualiza la pantalla
        boolean displayUpdated = notificationSystem.displayTicket(ticket, stationNumber);

        // Genera una alerta visual
        boolean alertGenerated = notificationSystem.generateVisualAlert(ticket);

        // También notifica al cliente personalmente si es posible
        Optional<Client> clientOpt = clientRepository.findById(ticket.getClientId());
        boolean clientNotified = false;

        if (clientOpt.isPresent()) {
            String message = "Su ticket " + ticket.getCode() + " está siendo atendido en la estación " + stationNumber;
            clientNotified = clientOpt.get().receiveAlert(message);
        }

        return displayUpdated && alertGenerated;
    }

    /**
     * Notifica a un cliente que su ticket ha sido creado
     *
     * @param ticket El ticket recién creado
     * @return true si la notificación se envió correctamente, false en caso contrario
     */
    public boolean notifyClientTicketCreated(Ticket ticket) {
        if (ticket == null) {
            return false;
        }

        Optional<Client> clientOpt = clientRepository.findById(ticket.getClientId());

        if (!clientOpt.isPresent()) {
            return false;
        }

        String message = "Su ticket " + ticket.getCode() + " ha sido creado para " +
                (ticket.getCategory() != null ? ticket.getCategory().getName() : "servicio general");

        return clientOpt.get().receiveAlert(message);
    }

    /**
     * Notifica a un cliente sobre su posición en la cola
     *
     * @param ticket El ticket
     * @param position La posición en la cola (basada en 1)
     * @return true si la notificación se envió correctamente, false en caso contrario
     */
    public boolean notifyClientQueuePosition(Ticket ticket, int position) {
        if (ticket == null || position < 1) {
            return false;
        }

        Optional<Client> clientOpt = clientRepository.findById(ticket.getClientId());

        if (!clientOpt.isPresent()) {
            return false;
        }

        String message = "Su ticket " + ticket.getCode() + " está actualmente en la posición " + position + " en la cola";

        return clientOpt.get().receiveAlert(message);
    }

    /**
     * Actualiza la pantalla principal con un mensaje personalizado
     *
     * @param message El mensaje a mostrar
     * @return true si la pantalla se actualizó correctamente, false en caso contrario
     */
    public boolean updateMainDisplay(String message) {
        return notificationSystem.updateDisplay(message);
    }

    /**
     * Limpia la pantalla de notificaciones
     *
     * @return true si la pantalla se limpió correctamente, false en caso contrario
     */
    public boolean clearDisplay() {
        return notificationSystem.clearDisplay();
    }

    /**
     * Obtiene el mensaje actual de la pantalla
     *
     * @return El mensaje actual de la pantalla
     */
    public String getCurrentDisplayMessage() {
        return notificationSystem.getDisplayMessage();
    }

    /**
     * Obtiene el ticket que se muestra actualmente
     *
     * @return El código del ticket que se muestra actualmente
     */
    public String getCurrentTicket() {
        return notificationSystem.getCurrentTicket();
    }

    /**
     * Obtiene la estación que se muestra actualmente
     *
     * @return El número de la estación que se muestra actualmente
     */
    public int getCurrentStation() {
        return notificationSystem.getCurrentStation();
    }
}