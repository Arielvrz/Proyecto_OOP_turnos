package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.ServiceStatistics;
import com.queuemanagementsystem.model.Ticket;
import com.queuemanagementsystem.repository.CategoryRepository;
import com.queuemanagementsystem.repository.TicketRepository;
import com.queuemanagementsystem.repository.UserRepository;
import com.queuemanagementsystem.util.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for generating and managing service statistics.
 */
public class StatisticsService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DateTimeUtil dateTimeUtil;
    private  ServiceStatistics currentDayStatistics;

    /**
     * Constructor with repository dependencies
     *
     * @param ticketRepository Repository for ticket data
     * @param userRepository Repository for user data
     * @param categoryRepository Repository for category data
     * @param dateTimeUtil Utility for date and time operations
     */
    public StatisticsService(TicketRepository ticketRepository, UserRepository userRepository,
                             CategoryRepository categoryRepository, DateTimeUtil dateTimeUtil) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.dateTimeUtil = dateTimeUtil;
        this.currentDayStatistics = new ServiceStatistics(
                dateTimeUtil.getStartOfDay(LocalDateTime.now()),
                dateTimeUtil.getEndOfDay(LocalDateTime.now())
        );

        // Initialize statistics with existing data
        initializeCurrentDayStatistics();
    }

    /**
     * Initializes current day statistics from existing data
     */
    private void initializeCurrentDayStatistics() {
        LocalDateTime startOfDay = dateTimeUtil.getStartOfDay(LocalDateTime.now());
        LocalDateTime endOfDay = dateTimeUtil.getEndOfDay(LocalDateTime.now());

        List<Ticket> todayTickets = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getGenerationTime() != null &&
                        !ticket.getGenerationTime().isBefore(startOfDay) &&
                        !ticket.getGenerationTime().isAfter(endOfDay))
                .collect(Collectors.toList());

        for (Ticket ticket : todayTickets) {
            currentDayStatistics.updateWithTicket(ticket);
        }

        // Update employee performance statistics
        updateEmployeePerformanceStatistics();
    }

    /**
     * Updates employee performance statistics
     */
    private void updateEmployeePerformanceStatistics() {
        List<Employee> employees = userRepository.findAll().stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());

        for (Employee employee : employees) {
            double ticketsPerHour = calculateEmployeeTicketsPerHour(employee);
            currentDayStatistics.updateEmployeePerformance(employee.getId(), ticketsPerHour);
        }
    }

    /**
     * Calculates the number of tickets processed per hour by an employee
     *
     * @param employee The employee
     * @return Tickets per hour
     */
    private double calculateEmployeeTicketsPerHour(Employee employee) {
        if (employee == null) {
            return 0.0;
        }

        List<Ticket> completedTickets = employee.getAttendedTickets().stream()
                .filter(ticket -> "COMPLETED".equals(ticket.getStatus()))
                .collect(Collectors.toList());

        if (completedTickets.isEmpty()) {
            return 0.0;
        }

        // Calculate total service time in hours
        double totalServiceHours = 0.0;
        for (Ticket ticket : completedTickets) {
            if (ticket.getAttentionTime() != null && ticket.getCompletionTime() != null) {
                double serviceTimeMinutes = ticket.calculateServiceTime();
                totalServiceHours += serviceTimeMinutes / 60.0;
            }
        }

        if (totalServiceHours <= 0) {
            return 0.0;
        }

        return completedTickets.size() / totalServiceHours;
    }

    /**
     * Updates statistics with a new ticket
     *
     * @param ticket The ticket to add to statistics
     */
    public void updateStatistics(Ticket ticket) {
        if (ticket == null) {
            return;
        }

        currentDayStatistics.updateWithTicket(ticket);

        // If the ticket is completed, update employee performance
        if ("COMPLETED".equals(ticket.getStatus())) {
            updateEmployeePerformanceStatistics();
        }
    }

    /**
     * Generates daily statistics report
     *
     * @return The daily statistics report
     */
    public String generateDailyStatistics() {
        return currentDayStatistics.generateDailyStatistics();
    }

    /**
     * Generates weekly statistics report
     *
     * @return The weekly statistics report
     */
    public String generateWeeklyStatistics() {
        LocalDateTime weekStart = dateTimeUtil.getStartOfWeek(LocalDateTime.now());
        LocalDateTime weekEnd = dateTimeUtil.getEndOfWeek(LocalDateTime.now());

        return generateStatisticsForPeriod(weekStart, weekEnd, "WEEKLY");
    }

    /**
     * Generates monthly statistics report
     *
     * @return The monthly statistics report
     */
    public String generateMonthlyStatistics() {
        LocalDateTime monthStart = dateTimeUtil.getStartOfMonth(LocalDateTime.now());
        LocalDateTime monthEnd = dateTimeUtil.getEndOfMonth(LocalDateTime.now());

        return generateStatisticsForPeriod(monthStart, monthEnd, "MONTHLY");
    }

    /**
     * Generates statistics for a specific period
     *
     * @param startDate Start of the period
     * @param endDate End of the period
     * @param periodType The type of period (e.g., "WEEKLY", "MONTHLY")
     * @return The statistics report
     */
    private String generateStatisticsForPeriod(LocalDateTime startDate, LocalDateTime endDate, String periodType) {
        ServiceStatistics periodStats = new ServiceStatistics(startDate, endDate);

        List<Ticket> periodTickets = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getGenerationTime() != null &&
                        !ticket.getGenerationTime().isBefore(startDate) &&
                        !ticket.getGenerationTime().isAfter(endDate))
                .collect(Collectors.toList());

        for (Ticket ticket : periodTickets) {
            periodStats.updateWithTicket(ticket);
        }

        // Calculate employee performance for the period
        List<Employee> employees = userRepository.findAll().stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());

        for (Employee employee : employees) {
            List<Ticket> employeeTickets = employee.getAttendedTickets().stream()
                    .filter(ticket -> "COMPLETED".equals(ticket.getStatus()) &&
                            ticket.getCompletionTime() != null &&
                            !ticket.getCompletionTime().isBefore(startDate) &&
                            !ticket.getCompletionTime().isAfter(endDate))
                    .collect(Collectors.toList());

            // Calculate tickets per hour for this period
            double totalServiceHours = 0.0;
            for (Ticket ticket : employeeTickets) {
                double serviceTimeMinutes = ticket.calculateServiceTime();
                totalServiceHours += serviceTimeMinutes / 60.0;
            }

            double ticketsPerHour = totalServiceHours > 0 ?
                    employeeTickets.size() / totalServiceHours : 0.0;

            periodStats.updateEmployeePerformance(employee.getId(), ticketsPerHour);
        }

        // Build a custom report for the period
        StringBuilder report = new StringBuilder();
        report.append("=== ").append(periodType).append(" STATISTICS REPORT ===\n");
        report.append("Period: ").append(startDate.toLocalDate()).append(" to ")
                .append(endDate.toLocalDate()).append("\n");
        report.append("Total tickets generated: ").append(periodStats.getGeneratedTickets()).append("\n");
        report.append("Total tickets attended: ").append(periodStats.getAttendedTickets()).append("\n");
        report.append("Average waiting time: ").append(String.format("%.2f", periodStats.getAverageWaitingTime()))
                .append(" minutes\n");
        report.append("Average service time: ").append(String.format("%.2f", periodStats.getAverageServiceTime()))
                .append(" minutes\n");

        report.append("\nTickets by Category:\n");
        for (Map.Entry<String, Integer> entry : periodStats.getTicketsByCategory().entrySet()) {
            report.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        report.append("\nEmployee Performance (tickets per hour):\n");
        for (Map.Entry<String, Double> entry : periodStats.getEmployeePerformance().entrySet()) {
            Optional<Employee> employeeOpt = userRepository.findById(entry.getKey())
                    .filter(user -> user instanceof Employee)
                    .map(user -> (Employee) user);

            if (employeeOpt.isPresent()) {
                report.append("- ").append(employeeOpt.get().getName()).append(": ")
                        .append(String.format("%.2f", entry.getValue())).append("\n");
            }
        }

        return report.toString();
    }

    /**
     * Calculates the average waiting time for a specific category
     *
     * @param categoryId The category ID
     * @return The average waiting time in minutes
     */
    public double getAverageWaitingTimeByCategory(int categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return 0.0;
        }

        Category category = categoryOpt.get();

        List<Ticket> categoryTickets = ticketRepository.findAll().stream()
                .filter(ticket -> ticket.getCategory() != null &&
                        ticket.getCategory().getId() == category.getId() &&
                        ticket.getAttentionTime() != null)
                .collect(Collectors.toList());

        if (categoryTickets.isEmpty()) {
            return 0.0;
        }

        double totalWaitingTime = categoryTickets.stream()
                .mapToLong(Ticket::calculateWaitingTime)
                .sum();

        return totalWaitingTime / categoryTickets.size();
    }

    /**
     * Gets productivity statistics for each employee
     *
     * @return Map of employee IDs to productivity statistics
     */
    public Map<String, Double> getEmployeeProductivityStatistics() {
        Map<String, Double> productivityStats = new HashMap<>();

        List<Employee> employees = userRepository.findAll().stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());

        for (Employee employee : employees) {
            double productivity = currentDayStatistics.calculateEmployeeProductivity(employee);
            productivityStats.put(employee.getId(), productivity);
        }

        return productivityStats;
    }

    /**
     * Gets the current service statistics
     *
     * @return The current service statistics
     */
    public ServiceStatistics getCurrentStatistics() {
        return currentDayStatistics;
    }
}