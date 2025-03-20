package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Station;
import com.queuemanagementsystem.repository.CategoryRepository;
import com.queuemanagementsystem.repository.StationRepository;
import com.queuemanagementsystem.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing service stations.
 */
public class StationService {
    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructor with repository dependencies
     *
     * @param stationRepository Repository for station data
     * @param userRepository Repository for user data (to access employees)
     * @param categoryRepository Repository for category data
     */
    public StationService(StationRepository stationRepository, UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new service station
     *
     * @param stationNumber The station's display number
     * @return The created station if successful, null otherwise
     */
    public Station createStation(int stationNumber) {
        // Check if a station with the same number already exists
        boolean numberExists = stationRepository.findAll().stream()
                .anyMatch(s -> s.getNumber() == stationNumber);

        if (numberExists) {
            return null; // Station number must be unique
        }

        // Generate a new ID (in a real system, this might be handled differently)
        int newId = getNextStationId();

        Station station = new Station(newId, stationNumber);

        if (stationRepository.save(station)) {
            return station;
        }

        return null;
    }

    /**
     * Opens a station for service
     *
     * @param stationId The station ID
     * @return true if the station was opened successfully, false otherwise
     */
    public boolean openStation(int stationId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);

        if (!stationOpt.isPresent() || stationOpt.get().getAssignedEmployee() == null) {
            return false;
        }

        Station station = stationOpt.get();
        if (station.openStation()) {
            return stationRepository.update(station);
        }

        return false;
    }

    /**
     * Closes a station
     *
     * @param stationId The station ID
     * @return true if the station was closed successfully, false otherwise
     */
    public boolean closeStation(int stationId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);

        if (!stationOpt.isPresent()) {
            return false;
        }

        Station station = stationOpt.get();
        if (station.closeStation()) {
            return stationRepository.update(station);
        }

        return false;
    }

    /**
     * Assigns an employee to a station
     *
     * @param stationId The station ID
     * @param employeeId The employee ID
     * @return true if the assignment was successful, false otherwise
     */
    public boolean assignEmployeeToStation(int stationId, String employeeId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);

        if (!stationOpt.isPresent()) {
            return false;
        }

        Optional<Employee> employeeOpt = userRepository.findById(employeeId)
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user);

        if (!employeeOpt.isPresent()) {
            return false;
        }

        Station station = stationOpt.get();
        Employee employee = employeeOpt.get();

        // Check if the employee is already assigned to another station
        if (employee.getAssignedStation() != null &&
                employee.getAssignedStation().getId() != stationId) {
            // Unassign from the current station
            Station currentStation = employee.getAssignedStation();
            currentStation.setAssignedEmployee(null);
            stationRepository.update(currentStation);
        }

        // Assign to the new station
        station.setAssignedEmployee(employee);

        // This is important - we need to update the station first
        boolean stationUpdated = stationRepository.update(station);

        // Then update the employee separately
        employee.setAssignedStation(station);
        boolean employeeUpdated = userRepository.update(employee);

        return stationUpdated && employeeUpdated;
    }

    /**
     * Adds a service category to a station
     *
     * @param stationId The station ID
     * @param categoryId The category ID
     * @return true if the category was added successfully, false otherwise
     */
    // In StationService.java
    public boolean addCategoryToStation(int stationId, int categoryId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!stationOpt.isPresent() || !categoryOpt.isPresent()) {
            return false;
        }

        Station station = stationOpt.get();
        Category category = categoryOpt.get();

        // Check if the category is already in the station (to avoid the error message)
        if (station.getSupportedCategoryIds().contains(categoryId)) {
            System.out.println("This category is already supported by this station.");
            return true; // Return true to avoid the error message
        }

        if (station.addCategory(category)) {
            return stationRepository.update(station);
        }

        return false;
    }

    /**
     * Removes a service category from a station
     *
     * @param stationId The station ID
     * @param categoryId The category ID
     * @return true if the category was removed successfully, false otherwise
     */
    public boolean removeCategoryFromStation(int stationId, int categoryId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!stationOpt.isPresent() || !categoryOpt.isPresent()) {
            return false;
        }

        Station station = stationOpt.get();
        Category category = categoryOpt.get();

        if (station.removeCategory(category)) {
            return stationRepository.update(station);
        }

        return false;
    }

    /**
     * Gets all open stations
     *
     * @return List of open stations
     */
    public List<Station> getAllOpenStations() {
        return stationRepository.findAll().stream()
                .filter(s -> "OPEN".equals(s.getStatus()))
                .collect(Collectors.toList());
    }



    /**
     * Gets all stations
     *
     * @return List of all stations
     */
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    /**
     * Gets a station by ID
     *
     * @param stationId The station ID
     * @return Optional containing the station if found, empty otherwise
     */
    public Optional<Station> getStationById(int stationId) {
        return stationRepository.findById(stationId);
    }

    /**
     * Gets stations that support a specific category
     *
     * @param categoryId The category ID
     * @return List of stations supporting the category
     */

    public List<Station> getStationsBySupportedCategory(int categoryId) {
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!categoryOpt.isPresent()) {
            return List.of();
        }

        return stationRepository.findAll().stream()
                .filter(station -> station.getSupportedCategoryIds().contains(categoryId))
                .collect(Collectors.toList());
    }
    /**
     * Generates the next available station ID
     *
     * @return The next available ID
     */
    private int getNextStationId() {
        return stationRepository.findAll().stream()
                .mapToInt(Station::getId)
                .max()
                .orElse(0) + 1;
    }

    public void resolveReferences() {
        List<Station> stations = stationRepository.findAll();

        for (Station station : stations) {
            // Instead of using getAssignedEmployeeId(), check if the assigned employee is null
            Employee assignedEmployee = station.getAssignedEmployee();
            if (assignedEmployee != null) {
                // Use the existing employee ID
                String employeeId = assignedEmployee.getId();
                if (employeeId != null && !employeeId.isEmpty()) {
                    userRepository.findById(employeeId).ifPresent(user -> {
                        if (user instanceof Employee) {
                            Employee employee = (Employee) user;
                            // Use existing setter without recursive updates
                            station.setAssignedEmployee(employee);
                        }
                    });
                }
            }
        }
    }
}

