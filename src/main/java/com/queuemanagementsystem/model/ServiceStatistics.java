package com.queuemanagementsystem.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Collects and processes statistical data about the service operation.
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
     * Default constructor
     */
    public ServiceStatistics() {
        this.periodStart = LocalDateTime.now();
        this.ticketsByCategory = new HashMap<>();
        this.employeePerformance = new HashMap<>();
    }

    /**
     * Constructor with period dates
     *
     * @param periodStart Start of the statistics period
     * @param periodEnd End of the statistics period
     */
    public ServiceStatistics(LocalDateTime periodStart, LocalDateTime periodEnd) {
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.ticketsByCategory = new HashMap<>();
        this.employeePerformance = new HashMap<>();
    }

    /**
     * Generates daily statistics report
     *
     * @return A string containing the daily statistics report
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
     * Calculates an employee's productivity
     *
     * @param employee The employee to calculate productivity for
     * @return A productivity score (tickets per hour)
     */
    public double calculateEmployeeProductivity(Employee employee) {
        if (employee == null) {
            return 0.0;
        }

        String employeeId = employee.getId();
        return employeePerformance.getOrDefault(employeeId, 0.0);
    }

    /**
     * Updates statistics with a new ticket
     *
     * @param ticket The ticket to add to statistics
     */
    public void updateWithTicket(Ticket ticket) {
        if (ticket == null) {
            return;
        }

        // Update ticket counts
        generatedTickets++;

        if ("COMPLETED".equals(ticket.getStatus())) {
            attendedTickets++;

            // Update waiting and service times
            long waitingTime = ticket.calculateWaitingTime();
            long serviceTime = ticket.calculateServiceTime();

            // Recalculate averages
            averageWaitingTime = ((averageWaitingTime * (attendedTickets - 1)) + waitingTime) / attendedTickets;
            averageServiceTime = ((averageServiceTime * (attendedTickets - 1)) + serviceTime) / attendedTickets;
        }

        // Update category statistics
        if (ticket.getCategory() != null) {
            String categoryName = ticket.getCategory().getName();
            ticketsByCategory.put(categoryName, ticketsByCategory.getOrDefault(categoryName, 0) + 1);
        }
    }

    /**
     * Updates employee performance statistics
     *
     * @param employeeId The employee's ID
     * @param ticketsPerHour Productivity measure
     */
    public void updateEmployeePerformance(String employeeId, double ticketsPerHour) {
        employeePerformance.put(employeeId, ticketsPerHour);
    }

    // Getters and Setters

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
        return new HashMap<>(ticketsByCategory);  // Return a copy to maintain encapsulation
    }

    public Map<String, Double> getEmployeePerformance() {
        return new HashMap<>(employeePerformance);  // Return a copy to maintain encapsulation
    }

    /**
     * Returns a string representation of this ServiceStatistics
     *
     * @return A string representation
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