package com.queuemanagementsystem.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un ticket para el servicio del cliente.
 */
public class Ticket {
    private String code;
    private String status; // "WAITING", "IN_PROGRESS", "COMPLETED"
    private Category category;
    private String clientId;
    private LocalDateTime generationTime;
    private LocalDateTime attentionTime;
    private LocalDateTime completionTime;

    /**
     * Constructor por defecto
     */
    public Ticket() {
        this.generationTime = LocalDateTime.now();
        this.status = "WAITING";
    }

    /**
     * Constructor parametrizado con campos esenciales
     *
     * @param category La categoría de servicio
     * @param clientId El ID del cliente
     */
    public Ticket(Category category, String clientId) {
        this.category = category;
        this.clientId = clientId;
        this.generationTime = LocalDateTime.now();
        this.status = "WAITING";
        this.code = generateCode();
    }

    /**
     * Constructor completo con todos los campos
     *
     * @param code Código único del ticket
     * @param status Estado actual del ticket
     * @param category Categoría de servicio
     * @param clientId ID del cliente
     * @param generationTime Momento en que se generó el ticket
     * @param attentionTime Momento en que comenzó el servicio
     * @param completionTime Momento en que se completó el servicio
     */
    public Ticket(String code, String status, Category category, String clientId,
                  LocalDateTime generationTime, LocalDateTime attentionTime,
                  LocalDateTime completionTime) {
        this.code = code;
        this.status = status;
        this.category = category;
        this.clientId = clientId;
        this.generationTime = generationTime;
        this.attentionTime = attentionTime;
        this.completionTime = completionTime;
    }

    /**
     * Genera un código único para el ticket
     *
     * @return El código generado
     */
    private String generateCode() {
        // Formato: Prefijo de categoría + número secuencial
        // En un sistema real, esto utilizaría un enfoque más sofisticado
        return (category != null ? category.getPrefix() : "GEN") + "-" +
                System.currentTimeMillis() % 1000;
    }

    /**
     * Cambia el estado del ticket
     *
     * @param newStatus El nuevo estado
     * @return true si el estado se cambió con éxito, false en caso contrario
     */
    public boolean changeStatus(String newStatus) {
        if (isValidStatusTransition(newStatus)) {
            this.status = newStatus;

            // Actualiza las marcas de tiempo según el nuevo estado
            if ("IN_PROGRESS".equals(newStatus)) {
                this.attentionTime = LocalDateTime.now();
            } else if ("COMPLETED".equals(newStatus)) {
                this.completionTime = LocalDateTime.now();
            }

            return true;
        }
        return false;
    }

    /**
     * Verifica si una transición de estado es válida
     *
     * @param newStatus El nuevo estado
     * @return true si la transición es válida, false en caso contrario
     */
    private boolean isValidStatusTransition(String newStatus) {
        if (newStatus == null) {
            return false;
        }

        switch (this.status) {
            case "WAITING":
                return "IN_PROGRESS".equals(newStatus);
            case "IN_PROGRESS":
                return "COMPLETED".equals(newStatus);
            case "COMPLETED":
                return false; // Estado terminal
            default:
                return false;
        }
    }

    /**
     * Calcula el tiempo de espera en minutos
     *
     * @return El tiempo de espera en minutos
     */
    public long calculateWaitingTime() {
        if (attentionTime != null) {
            return Duration.between(generationTime, attentionTime).toMinutes();
        } else if ("WAITING".equals(status)) {
            return Duration.between(generationTime, LocalDateTime.now()).toMinutes();
        }
        return 0;
    }

    /**
     * Calcula el tiempo de servicio en minutos
     *
     * @return El tiempo de servicio en minutos
     */
    public long calculateServiceTime() {
        if (attentionTime != null && completionTime != null) {
            return Duration.between(attentionTime, completionTime).toMinutes();
        } else if ("IN_PROGRESS".equals(status) && attentionTime != null) {
            return Duration.between(attentionTime, LocalDateTime.now()).toMinutes();
        }
        return 0;
    }

    /**
     * Genera una alerta visual para el cliente
     *
     * @return El mensaje de alerta
     */
    public String generateVisualAlert() {
        return "ATENCIÓN: El ticket " + code + " está siendo atendido en la estación " +
                (category != null ? category.getName() : "servicio general");
    }

    // Getters y Setters

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(LocalDateTime generationTime) {
        this.generationTime = generationTime;
    }

    public LocalDateTime getAttentionTime() {
        return attentionTime;
    }

    public void setAttentionTime(LocalDateTime attentionTime) {
        this.attentionTime = attentionTime;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    /**
     * Compara este Ticket con otro objeto para determinar igualdad
     *
     * @param o El objeto a comparar
     * @return true si los objetos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(code, ticket.code);
    }

    /**
     * Genera un código hash para este Ticket
     *
     * @return El código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    /**
     * Devuelve una representación en string de este Ticket
     *
     * @return Una representación en string
     */
    @Override
    public String toString() {
        return "Ticket{" +
                "code='" + code + '\'' +
                ", status='" + status + '\'' +
                ", category=" + (category != null ? category.getName() : "None") +
                ", clientId='" + clientId + '\'' +
                ", generationTime=" + generationTime +
                ", waitingTime=" + calculateWaitingTime() + " minutes" +
                '}';
    }
}