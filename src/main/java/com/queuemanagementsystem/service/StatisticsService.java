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
 * Clase de servicio para generar y gestionar estadísticas de servicio.
 */
public class StatisticsService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DateTimeUtil dateTimeUtil;
    private  ServiceStatistics currentDayStatistics;

    /**
     * Constructor con dependencias de repositorios
     *
     * @param ticketRepository Repositorio para datos de tickets
     * @param userRepository Repositorio para datos de usuarios
     * @param categoryRepository Repositorio para datos de categorías
     * @param dateTimeUtil Utilidad para operaciones de fecha y hora
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

        // Inicializa estadísticas con datos existentes
        initializeCurrentDayStatistics();
    }

    /**
     * Inicializa las estadísticas del día actual a partir de datos existentes
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

        // Actualiza estadísticas de rendimiento de empleados
        updateEmployeePerformanceStatistics();
    }

    /**
     * Actualiza estadísticas de rendimiento de empleados
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
     * Calcula el número de tickets procesados por hora por un empleado
     *
     * @param employee El empleado
     * @return Tickets por hora
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

        // Calcula el tiempo total de servicio en horas
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
     * Actualiza estadísticas con un nuevo ticket
     *
     * @param ticket El ticket para añadir a las estadísticas
     */
    public void updateStatistics(Ticket ticket) {
        if (ticket == null) {
            return;
        }

        currentDayStatistics.updateWithTicket(ticket);

        // Si el ticket está completado, actualiza el rendimiento del empleado
        if ("COMPLETED".equals(ticket.getStatus())) {
            updateEmployeePerformanceStatistics();
        }
    }

    /**
     * Genera informe de estadísticas diarias
     *
     * @return El informe de estadísticas diarias
     */
    public String generateDailyStatistics() {
        return currentDayStatistics.generateDailyStatistics();
    }

    /**
     * Genera informe de estadísticas semanales
     *
     * @return El informe de estadísticas semanales
     */
    public String generateWeeklyStatistics() {
        LocalDateTime weekStart = dateTimeUtil.getStartOfWeek(LocalDateTime.now());
        LocalDateTime weekEnd = dateTimeUtil.getEndOfWeek(LocalDateTime.now());

        return generateStatisticsForPeriod(weekStart, weekEnd, "WEEKLY");
    }

    /**
     * Genera informe de estadísticas mensuales
     *
     * @return El informe de estadísticas mensuales
     */
    public String generateMonthlyStatistics() {
        LocalDateTime monthStart = dateTimeUtil.getStartOfMonth(LocalDateTime.now());
        LocalDateTime monthEnd = dateTimeUtil.getEndOfMonth(LocalDateTime.now());

        return generateStatisticsForPeriod(monthStart, monthEnd, "MONTHLY");
    }

    /**
     * Genera estadísticas para un período específico
     *
     * @param startDate Inicio del período
     * @param endDate Fin del período
     * @param periodType El tipo de período (p.ej., "WEEKLY", "MONTHLY")
     * @return El informe de estadísticas
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

        // Calcula el rendimiento de los empleados para el período
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

            // Calcula tickets por hora para este período
            double totalServiceHours = 0.0;
            for (Ticket ticket : employeeTickets) {
                double serviceTimeMinutes = ticket.calculateServiceTime();
                totalServiceHours += serviceTimeMinutes / 60.0;
            }

            double ticketsPerHour = totalServiceHours > 0 ?
                    employeeTickets.size() / totalServiceHours : 0.0;

            periodStats.updateEmployeePerformance(employee.getId(), ticketsPerHour);
        }

        // Construye un informe personalizado para el período
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
     * Calcula el tiempo medio de espera para una categoría específica
     *
     * @param categoryId El ID de la categoría
     * @return El tiempo medio de espera en minutos
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
     * Obtiene estadísticas de productividad para cada empleado
     *
     * @return Mapa de IDs de empleados con sus estadísticas de productividad
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
     * Obtiene las estadísticas de servicio actuales
     *
     * @return Las estadísticas de servicio actuales
     */
    public ServiceStatistics getCurrentStatistics() {
        return currentDayStatistics;
    }
}