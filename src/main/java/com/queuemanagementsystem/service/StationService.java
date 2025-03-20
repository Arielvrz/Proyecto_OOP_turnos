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
 * Clase de servicio para gestionar estaciones de servicio.
 */
public class StationService {
    private final StationRepository stationRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructor con dependencias de repositorios
     *
     * @param stationRepository Repositorio para datos de estaciones
     * @param userRepository Repositorio para datos de usuarios (para acceder a empleados)
     * @param categoryRepository Repositorio para datos de categorías
     */
    public StationService(StationRepository stationRepository, UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Crea una nueva estación de servicio
     *
     * @param stationNumber El número de visualización de la estación
     * @return La estación creada si fue exitoso, null en caso contrario
     */
    public Station createStation(int stationNumber) {
        // Verifica si ya existe una estación con el mismo número
        boolean numberExists = stationRepository.findAll().stream()
                .anyMatch(s -> s.getNumber() == stationNumber);

        if (numberExists) {
            return null; // El número de estación debe ser único
        }

        // Genera un nuevo ID (en un sistema real, esto podría manejarse de manera diferente)
        int newId = getNextStationId();

        Station station = new Station(newId, stationNumber);

        if (stationRepository.save(station)) {
            return station;
        }

        return null;
    }

    /**
     * Abre una estación para servicio
     *
     * @param stationId El ID de la estación
     * @return true si la estación se abrió correctamente, false en caso contrario
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
     * Cierra una estación
     *
     * @param stationId El ID de la estación
     * @return true si la estación se cerró correctamente, false en caso contrario
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
     * Asigna un empleado a una estación
     *
     * @param stationId El ID de la estación
     * @param employeeId El ID del empleado
     * @return true si la asignación fue exitosa, false en caso contrario
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

        // Verifica si el empleado ya está asignado a otra estación
        if (employee.getAssignedStation() != null &&
                employee.getAssignedStation().getId() != stationId) {
            // Designa de la estación actual
            Station currentStation = employee.getAssignedStation();
            currentStation.setAssignedEmployee(null);
            stationRepository.update(currentStation);
        }

        // Asigna a la nueva estación
        station.setAssignedEmployee(employee);

        // Esto es importante - necesitamos actualizar la estación primero
        boolean stationUpdated = stationRepository.update(station);

        // Luego actualizar el empleado por separado
        employee.setAssignedStation(station);
        boolean employeeUpdated = userRepository.update(employee);

        return stationUpdated && employeeUpdated;
    }

    /**
     * Añade una categoría de servicio a una estación
     *
     * @param stationId El ID de la estación
     * @param categoryId El ID de la categoría
     * @return true si la categoría se añadió correctamente, false en caso contrario
     */
    // En StationService.java
    public boolean addCategoryToStation(int stationId, int categoryId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (!stationOpt.isPresent() || !categoryOpt.isPresent()) {
            return false;
        }

        Station station = stationOpt.get();
        Category category = categoryOpt.get();

        // Verifica si la categoría ya está en la estación (para evitar el mensaje de error)
        if (station.getSupportedCategoryIds().contains(categoryId)) {
            System.out.println("Esta categoría ya está soportada por esta estación.");
            return true; // Devuelve true para evitar el mensaje de error
        }

        if (station.addCategory(category)) {
            return stationRepository.update(station);
        }

        return false;
    }

    /**
     * Elimina una categoría de servicio de una estación
     *
     * @param stationId El ID de la estación
     * @param categoryId El ID de la categoría
     * @return true si la categoría se eliminó correctamente, false en caso contrario
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
     * Obtiene todas las estaciones abiertas
     *
     * @return Lista de estaciones abiertas
     */
    public List<Station> getAllOpenStations() {
        return stationRepository.findAll().stream()
                .filter(s -> "OPEN".equals(s.getStatus()))
                .collect(Collectors.toList());
    }



    /**
     * Obtiene todas las estaciones
     *
     * @return Lista de todas las estaciones
     */
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    /**
     * Obtiene una estación por su ID
     *
     * @param stationId El ID de la estación
     * @return Optional que contiene la estación si se encuentra, vacío en caso contrario
     */
    public Optional<Station> getStationById(int stationId) {
        return stationRepository.findById(stationId);
    }

    /**
     * Obtiene estaciones que soportan una categoría específica
     *
     * @param categoryId El ID de la categoría
     * @return Lista de estaciones que soportan la categoría
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
     * Genera el siguiente ID de estación disponible
     *
     * @return El siguiente ID disponible
     */
    private int getNextStationId() {
        return stationRepository.findAll().stream()
                .mapToInt(Station::getId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Resuelve referencias entre objetos
     */
    public void resolveReferences() {
        List<Station> stations = stationRepository.findAll();

        for (Station station : stations) {
            // En lugar de usar getAssignedEmployeeId(), verifica si el empleado asignado es null
            Employee assignedEmployee = station.getAssignedEmployee();
            if (assignedEmployee != null) {
                // Usa el ID de empleado existente
                String employeeId = assignedEmployee.getId();
                if (employeeId != null && !employeeId.isEmpty()) {
                    userRepository.findById(employeeId).ifPresent(user -> {
                        if (user instanceof Employee) {
                            Employee employee = (Employee) user;
                            // Usa el setter existente sin actualizaciones recursivas
                            station.setAssignedEmployee(employee);
                        }
                    });
                }
            }
        }
    }
}
