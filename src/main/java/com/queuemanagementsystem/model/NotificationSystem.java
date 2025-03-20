package com.queuemanagementsystem.model;

/**
 * Gestiona las notificaciones visuales para los clientes.
 */
public class NotificationSystem {
    private String displayMessage;
    private String currentTicket;
    private int currentStation;

    /**
     * Constructor por defecto
     */
    public NotificationSystem() {
        this.displayMessage = "Welcome to the Queue Management System";
    }

    /**
     * Muestra un ticket en la pantalla de notificación
     *
     * @param ticket El ticket a mostrar
     * @param stationNumber El número de la estación donde el ticket debe ser atendido
     * @return true si la pantalla se actualizó con éxito, false en caso contrario
     */
    public boolean displayTicket(Ticket ticket, int stationNumber) {
        if (ticket == null) {
            return false;
        }

        this.currentTicket = ticket.getCode();
        this.currentStation = stationNumber;
        this.displayMessage = "Ticket " + currentTicket + " please proceed to station " + currentStation;

        // En un sistema real, esto actualizaría una pantalla física o enviaría notificaciones
        System.out.println("DISPLAY UPDATE: " + displayMessage);

        return true;
    }

    /**
     * Genera una alerta visual para un ticket específico
     *
     * @param ticket El ticket para alertar
     * @return true si la alerta se generó con éxito, false en caso contrario
     */
    public boolean generateVisualAlert(Ticket ticket) {
        if (ticket == null) {
            return false;
        }

        // Hacer parpadear la pantalla o usar otras señales visuales
        this.displayMessage = "**ALERT** Ticket " + ticket.getCode() + " is now being called!";

        // En un sistema real, esto activaría efectos visuales especiales
        System.out.println("VISUAL ALERT: " + displayMessage);

        return true;
    }

    /**
     * Actualiza la pantalla con un mensaje personalizado
     *
     * @param message El mensaje a mostrar
     * @return true si la pantalla se actualizó con éxito
     */
    public boolean updateDisplay(String message) {
        if (message != null && !message.isEmpty()) {
            this.displayMessage = message;

            // En un sistema real, esto actualizaría una pantalla física
            System.out.println("DISPLAY UPDATE: " + displayMessage);

            return true;
        }
        return false;
    }

    /**
     * Limpia la pantalla
     *
     * @return true indicando que la pantalla fue limpiada
     */
    public boolean clearDisplay() {
        this.displayMessage = "";
        this.currentTicket = null;
        this.currentStation = 0;

        // En un sistema real, esto limpiaría una pantalla física
        System.out.println("DISPLAY CLEARED");

        return true;
    }

    // Getters y Setters

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public String getCurrentTicket() {
        return currentTicket;
    }

    public int getCurrentStation() {
        return currentStation;
    }

    /**
     * Devuelve una representación en string de este Sistema de Notificación
     *
     * @return Una representación en string
     */
    @Override
    public String toString() {
        return "NotificationSystem{" +
                "displayMessage='" + displayMessage + '\'' +
                ", currentTicket='" + currentTicket + '\'' +
                ", currentStation=" + currentStation +
                '}';
    }
}