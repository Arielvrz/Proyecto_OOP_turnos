package com.queuemanagementsystem.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Recopila y procesa datos estadísticos sobre la operación del servicio.
 */
public class ServiceStatistics {
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private int generatedTickets;
    private int attendedTickets;
    private double averageWaitingTime;
    private double averageServiceTime;
    private Map<String, Integer> ticketsByCategory;
    private Map<String, Double> employeePerformance;

    /**
     * Constructor por defecto
     */
    public ServiceStatistics() {
        this.periodStart = LocalDateTime.now();
        this.ticketsByCategory = new HashMap<>();
        this.employeePerformance = new HashMap<>();
    }

    /**
     * Constructor con fechas de período
     *
     * @param periodStart Inicio del período de estadísticas
     * @param periodEnd Fin del período de estadísticas
     */
    public ServiceStatistics(LocalDateTime periodStart, LocalDateTime periodEnd) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.ticketsByCategory = new HashMap<>();
        this.employeePerformance = new HashMap<>();
    }

    /**
     * Genera un informe de estadísticas diarias
     *
     * @return Una cadena que contiene el informe de estadísticas diarias
     */
    public String generateDailyStatistics() {
        StringBuilder report = new StringBuilder();
        report.append("=== DAILY STATISTICS REPORT ===\n");
        report.append("Period: ").append(periodStart.toLocalDate()).append("\n");
        report.append("Total tickets generated: ").append(generatedTickets).append("\n");
        report.append("Total tickets attended: ").append(attendedTickets).append("\n");
        report.append("Average waiting time: ").append(String.format("%.2f", averageWaitingTime)).append(" minutes\n");
        report.append("Average service time: ").append(String.format("%.2f", averageServiceTime)).append(" minutes\n");

        report.append("\nTickets by Category:\n");
        for (Map.Entry<String, Integer> entry : ticketsByCategory.entrySet()) {
            report.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        return report.toString();
    }

    /**
     * Calcula la productividad de un empleado
     *
     * @param employee El empleado para calcular la productividad
     * @return Una puntuación de productividad (tickets por hora)
     */
    public double calculateEmployeeProductivity(Employee employee) {
        if (employee == null) {
            return 0.0;
        }

        String employeeId = employee.getId();
        return employeePerformance.getOrDefault(employeeId, 0.0);
    }

    /**
     * Actualiza las estadísticas con un nuevo ticket
     *
     * @param ticket El ticket para añadir a las estadísticas
     */
    public void updateWithTicket(Ticket ticket) {
        if (ticket == null) {
            return;
        }

        // Actualiza conteo de tickets
        generatedTickets++;

        if ("COMPLETED".equals(ticket.getStatus())) {
            attendedTickets++;

            // Actualiza tiempos de espera y servicio
            long waitingTime = ticket.calculateWaitingTime();
            long serviceTime = ticket.calculateServiceTime();

            // Recalcula promedios
            averageWaitingTime = ((averageWaitingTime * (attendedTickets - 1)) + waitingTime) / attendedTickets;
            averageServiceTime = ((averageServiceTime * (attendedTickets - 1)) + serviceTime) / attendedTickets;
        }

        // Actualiza estadísticas por categoría
        if (ticket.getCategory() != null) {
            String categoryName = ticket.getCategory().getName();
            ticketsByCategory.put(categoryName, ticketsByCategory.getOrDefault(categoryName, 0) + 1);
        }
    }

    /**
     * Actualiza las estadísticas de rendimiento del empleado
     *
     * @param employeeId El ID del empleado
     * @param ticketsPerHour Medida de productividad
     */
    public void updateEmployeePerformance(String employeeId, double ticketsPerHour) {
        employeePerformance.put(employeeId, ticketsPerHour);
    }

    // Getters y Setters

    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    public int getGeneratedTickets() {
        return generatedTickets;
    }

    public void setGeneratedTickets(int generatedTickets) {
        this.generatedTickets = generatedTickets;
    }

    public int getAttendedTickets() {
        return attendedTickets;
    }

    public void setAttendedTickets(int attendedTickets) {
        this.attendedTickets = attendedTickets;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public void setAverageWaitingTime(double averageWaitingTime) {
        this.averageWaitingTime = averageWaitingTime;
    }

    public double getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(double averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public Map<String, Integer> getTicketsByCategory() {
        return new HashMap<>(ticketsByCategory);  // Devuelve una copia para mantener la encapsulación
    }

    public Map<String, Double> getEmployeePerformance() {
        return new HashMap<>(employeePerformance);  // Devuelve una copia para mantener la encapsulación
    }

    /**
     * Devuelve una representación en string de estas Estadísticas de Servicio
     *
     * @return Una representación en string
     */
    @Override
    public String toString() {
        return "ServiceStatistics{" +
                "periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", generatedTickets=" + generatedTickets +
                ", attendedTickets=" + attendedTickets +
                ", averageWaitingTime=" + averageWaitingTime +
                ", averageServiceTime=" + averageServiceTime +
                '}';
    }
}