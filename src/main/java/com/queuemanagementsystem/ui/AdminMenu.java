package com.queuemanagementsystem.ui;

import com.queuemanagementsystem.model.Administrator;
import com.queuemanagementsystem.model.Category;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Station;
import com.queuemanagementsystem.service.CategoryService;
import com.queuemanagementsystem.service.StationService;
import com.queuemanagementsystem.service.StatisticsService;
import com.queuemanagementsystem.service.UserService;
import com.queuemanagementsystem.model.Ticket;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menú para las interacciones del administrador con el sistema.
 */
public class AdminMenu implements Menu {
    private final Scanner scanner;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StationService stationService;
    private final StatisticsService statisticsService;
    private Administrator currentAdmin;

    /**
     * Constructor con dependencias
     *
     * @param scanner           Scanner para leer la entrada del usuario
     * @param userService       Servicio para operaciones de usuario
     * @param categoryService   Servicio para operaciones de categoría
     * @param stationService    Servicio para operaciones de estación
     * @param statisticsService Servicio para operaciones de estadísticas
     */
    public AdminMenu(Scanner scanner, UserService userService, CategoryService categoryService,
                     StationService stationService, StatisticsService statisticsService) {
        this.scanner = scanner;
        this.userService = userService;
        this.categoryService = categoryService;
        this.stationService = stationService;
        this.statisticsService = statisticsService;
    }

    /**
     * Solicita al usuario que ingrese sus credenciales de administrador
     *
     * @return true si la autenticación fue exitosa, false en caso contrario
     */
    public boolean authenticate() {
        System.out.println("\n=== Inicio de Sesión de Administrador ===");

        System.out.print("Ingrese su ID: ");
        String id = scanner.nextLine().trim();

        System.out.print("Ingrese su contraseña: ");
        String password = scanner.nextLine().trim();

        Optional<Administrator> adminOpt = userService.getAdministratorById(id);

        if (adminOpt.isPresent() && adminOpt.get().login(id, password)) {
            currentAdmin = adminOpt.get();
            System.out.println("¡Inicio de sesión exitoso! Bienvenido, Administrador " + currentAdmin.getName() + "!");
            return true;
        } else {
            System.out.println("Credenciales inválidas. Por favor, intente nuevamente.");
            return false;
        }
    }

    @Override
    public void displayMenu() {
        System.out.println("\n=== Menú de Administrador ===");
        System.out.println("1. Gestionar Categorías");
        System.out.println("2. Gestionar Estaciones");
        System.out.println("3. Gestionar Empleados");
        System.out.println("4. Ver Estadísticas");
        System.out.println("5. Generar Informes");
        System.out.println("0. Cerrar Sesión");
    }

    @Override
    public boolean processOption(String option) {
        switch (option) {
            case "1":
                manageCategoriesMenu();
                return true;
            case "2":
                manageStationsMenu();
                return true;
            case "3":
                manageEmployeesMenu();
                return true;
            case "4":
                viewStatistics();
                return true;
            case "5":
                generateReports();
                return true;
            case "0":
                System.out.println("Cerrando sesión. ¡Hasta pronto, Administrador " + currentAdmin.getName() + "!");
                return false;
            default:
                System.out.println("Opción inválida. Por favor, intente nuevamente.");
                return true;
        }
    }

    /**
     * Muestra y maneja el submenú de gestión de categorías
     */
    private void manageCategoriesMenu() {
        boolean continueMenu = true;

        while (continueMenu) {
            System.out.println("\n=== Gestionar Categorías ===");
            System.out.println("1. Ver todas las categorías");
            System.out.println("2. Crear nueva categoría");
            System.out.println("3. Activar/Desactivar categoría");
            System.out.println("4. Actualizar detalles de categoría");
            System.out.println("0. Volver al menú principal");

            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    viewAllCategories();
                    break;
                case "2":
                    createCategory();
                    break;
                case "3":
                    toggleCategoryStatus();
                    break;
                case "4":
                    updateCategory();
                    break;
                case "0":
                    continueMenu = false;
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, intente nuevamente.");
            }
        }
    }

    /**
     * Muestra todas las categorías
     */
    private void viewAllCategories() {
        System.out.println("\n=== Todas las Categorías ===");

        List<Category> categories = categoryService.getAllCategories();

        if (categories.isEmpty()) {
            System.out.println("No se encontraron categorías.");
            return;
        }

        for (Category category : categories) {
            System.out.println("ID: " + category.getId());
            System.out.println("Nombre: " + category.getName());
            System.out.println("Descripción: " + category.getDescription());
            System.out.println("Prefijo: " + category.getPrefix());
            System.out.println("Estado: " + (category.isActive() ? "Activo" : "Inactivo"));
            System.out.println("Turnos pendientes: " + category.countPendingTickets());
            System.out.println("Empleados asignados: " + category.getAssignedEmployees().size());
            System.out.println("-----");
        }
    }

    /**
     * Maneja la creación de una nueva categoría
     */
    private void createCategory() {
        System.out.println("\n=== Crear Nueva Categoría ===");

        System.out.print("Ingrese nombre de la categoría: ");
        String name = scanner.nextLine().trim();

        System.out.print("Ingrese descripción de la categoría: ");
        String description = scanner.nextLine().trim();

        System.out.print("Ingrese prefijo de la categoría (ej. GEN, FIN): ");
        String prefix = scanner.nextLine().trim().toUpperCase();

        if (name.isEmpty() || prefix.isEmpty()) {
            System.out.println("Nombre y prefijo son obligatorios. Operación cancelada.");
            return;
        }

        // Verificar si ya existe una categoría con este prefijo
        if (categoryService.getCategoryByPrefix(prefix).isPresent()) {
            System.out.println("Ya existe una categoría con este prefijo. Por favor use un prefijo diferente.");
            return;
        }

        Category newCategory = categoryService.createCategory(name, description, prefix);

        if (newCategory != null) {
            System.out.println("¡Categoría creada exitosamente!");
            System.out.println("ID: " + newCategory.getId());
            System.out.println("Nombre: " + newCategory.getName());
            System.out.println("Prefijo: " + newCategory.getPrefix());
        } else {
            System.out.println("Error al crear la categoría. Por favor, intente nuevamente.");
        }
    }

    /**
     * Maneja la activación o desactivación de una categoría
     */
    private void toggleCategoryStatus() {
        System.out.println("\n=== Activar/Desactivar Categoría ===");

        // Mostrar categorías
        List<Category> categories = categoryService.getAllCategories();

        if (categories.isEmpty()) {
            System.out.println("No se encontraron categorías.");
            return;
        }

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " [" +
                    (category.isActive() ? "Activo" : "Inactivo") + "]");
        }

        // Obtener selección del usuario
        System.out.print("Seleccione una categoría para cambiar su estado (1-" + categories.size() + ") o 0 para cancelar: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > categories.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Category selectedCategory = categories.get(selection - 1);

            // Cambiar estado
            boolean success;
            if (selectedCategory.isActive()) {
                success = categoryService.deactivateCategory(selectedCategory.getId());
                if (success) {
                    System.out.println("La categoría '" + selectedCategory.getName() + "' ha sido desactivada.");
                }
            } else {
                success = categoryService.activateCategory(selectedCategory.getId());
                if (success) {
                    System.out.println("La categoría '" + selectedCategory.getName() + "' ha sido activada.");
                }
            }

            if (!success) {
                System.out.println("Error al actualizar el estado de la categoría. Por favor, intente nuevamente.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Maneja la actualización de detalles de una categoría
     */
    private void updateCategory() {
        System.out.println("\n=== Actualizar Categoría ===");

        // Mostrar categorías
        List<Category> categories = categoryService.getAllCategories();

        if (categories.isEmpty()) {
            System.out.println("No se encontraron categorías.");
            return;
        }

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " [" +
                    (category.isActive() ? "Activo" : "Inactivo") + "]");
        }

        // Obtener selección del usuario
        System.out.print("Seleccione una categoría para actualizar (1-" + categories.size() + ") o 0 para cancelar: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > categories.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Category selectedCategory = categories.get(selection - 1);

            // Actualizar campos
            System.out.println("Actualizando categoría: " + selectedCategory.getName());
            System.out.println("Deje el campo vacío para mantener el valor actual");

            System.out.print("Nuevo nombre [" + selectedCategory.getName() + "]: ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                selectedCategory.setName(name);
            }

            System.out.print("Nueva descripción [" + selectedCategory.getDescription() + "]: ");
            String description = scanner.nextLine().trim();
            if (!description.isEmpty()) {
                selectedCategory.setDescription(description);
            }

            // El prefijo es más crítico, así que confirmar cambios
            System.out.print("Nuevo prefijo [" + selectedCategory.getPrefix() + "]: ");
            String prefix = scanner.nextLine().trim().toUpperCase();
            if (!prefix.isEmpty() && !prefix.equals(selectedCategory.getPrefix())) {
                // Verificar si el prefijo ya está en uso
                Optional<Category> existingCategory = categoryService.getCategoryByPrefix(prefix);
                if (existingCategory.isPresent() && existingCategory.get().getId() != selectedCategory.getId()) {
                    System.out.println("Este prefijo ya está en uso por otra categoría. Prefijo no actualizado.");
                } else {
                    System.out.println("Advertencia: Cambiar el prefijo puede afectar a los turnos existentes.");
                    System.out.println("¿Está seguro de que desea cambiarlo? (s/n)");
                    String confirmation = scanner.nextLine().trim().toLowerCase();

                    if (confirmation.equals("s") || confirmation.equals("si")) {
                        selectedCategory.setPrefix(prefix);
                    }
                }
            }

            // Guardar cambios
            if (categoryService.updateCategory(selectedCategory)) {
                System.out.println("¡Categoría actualizada exitosamente!");
            } else {
                System.out.println("Error al actualizar la categoría. Por favor, intente nuevamente.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Muestra y maneja el submenú de gestión de estaciones
     */
    private void manageStationsMenu() {
        boolean continueMenu = true;

        while (continueMenu) {
            System.out.println("\n=== Gestionar Estaciones ===");
            System.out.println("1. Ver todas las estaciones");
            System.out.println("2. Crear nueva estación");
            System.out.println("3. Abrir/Cerrar estación");
            System.out.println("4. Asignar empleado a estación");
            System.out.println("5. Configurar categorías de estación");
            System.out.println("0. Volver al menú principal");

            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    viewAllStations();
                    break;
                case "2":
                    createStation();
                    break;
                case "3":
                    toggleStationStatus();
                    break;
                case "4":
                    assignEmployeeToStation();
                    break;
                case "5":
                    configureStationCategories();
                    break;
                case "0":
                    continueMenu = false;
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, intente nuevamente.");
            }
        }
    }

    /**
     * Muestra todas las estaciones
     */
    private void viewAllStations() {
        System.out.println("\n=== Todas las Estaciones ===");

        List<Station> stations = stationService.getAllStations();

        if (stations.isEmpty()) {
            System.out.println("No se encontraron estaciones.");
            return;
        }

        for (Station station : stations) {
            System.out.println("ID: " + station.getId());
            System.out.println("Número: " + station.getNumber());
            System.out.println("Estado: " + translateStationStatus(station.getStatus()));

            Employee assignedEmployee = station.getAssignedEmployee();
            System.out.println("Empleado asignado: " +
                    (assignedEmployee != null ? assignedEmployee.getName() : "Ninguno"));

            List<Category> categories = station.getSupportedCategories();
            System.out.println("Categorías soportadas: " +
                    (categories.isEmpty() ? "Ninguna" : categories.size() + " categorías"));

            if (!categories.isEmpty()) {
                System.out.println("Categorías:");
                for (Category category : categories) {
                    System.out.println("  - " + category.getName() + " [" + category.getPrefix() + "]");
                }
            }

            System.out.println("-----");
        }
    }

    /**
     * Traduce los estados de estación a español
     *
     * @param status El estado de la estación en inglés
     * @return El estado de la estación traducido al español
     */
    private String translateStationStatus(String status) {
        switch (status) {
            case "OPEN": return "Abierta";
            case "CLOSED": return "Cerrada";
            default: return status;
        }
    }

    /**
     * Maneja la creación de una nueva estación
     */
    private void createStation() {
        System.out.println("\n=== Crear Nueva Estación ===");

        System.out.print("Ingrese número de estación: ");

        try {
            int stationNumber = Integer.parseInt(scanner.nextLine().trim());

            if (stationNumber <= 0) {
                System.out.println("El número de estación debe ser positivo. Operación cancelada.");
                return;
            }

            // Verificar si ya existe una estación con este número
            if (stationService.getAllStations().stream()
                    .anyMatch(s -> s.getNumber() == stationNumber)) {
                System.out.println("Ya existe una estación con este número. Por favor utilice un número diferente.");
                return;
            }

            Station newStation = stationService.createStation(stationNumber);

            if (newStation != null) {
                System.out.println("¡Estación creada exitosamente!");
                System.out.println("ID: " + newStation.getId());
                System.out.println("Número: " + newStation.getNumber());
                System.out.println("Estado: " + translateStationStatus(newStation.getStatus()));
            } else {
                System.out.println("Error al crear la estación. Por favor, intente nuevamente.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Maneja la apertura o cierre de una estación
     */
    private void toggleStationStatus() {
        System.out.println("\n=== Abrir/Cerrar Estación ===");

        // Mostrar estaciones
        List<Station> stations = stationService.getAllStations();

        if (stations.isEmpty()) {
            System.out.println("No se encontraron estaciones.");
            return;
        }

        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            System.out.println((i + 1) + ". Estación " + station.getNumber() + " [" + translateStationStatus(station.getStatus()) + "]");
        }

        // Obtener selección del usuario
        System.out.print("Seleccione una estación para cambiar su estado (1-" + stations.size() + ") o 0 para cancelar: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > stations.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Station selectedStation = stations.get(selection - 1);

            // Cambiar estado
            boolean success;
            if ("OPEN".equals(selectedStation.getStatus())) {
                success = stationService.closeStation(selectedStation.getId());
                if (success) {
                    System.out.println("La estación " + selectedStation.getNumber() + " ha sido cerrada.");
                }
            } else {
                // Verificar si la estación tiene un empleado asignado
                if (selectedStation.getAssignedEmployee() == null) {
                    System.out.println("No se puede abrir la estación sin un empleado asignado.");
                    return;
                }

                success = stationService.openStation(selectedStation.getId());
                if (success) {
                    System.out.println("La estación " + selectedStation.getNumber() + " ha sido abierta.");
                }
            }

            if (!success) {
                System.out.println("Error al actualizar el estado de la estación. Por favor, intente nuevamente.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Maneja la asignación de un empleado a una estación
     */
    private void assignEmployeeToStation() {
        System.out.println("\n=== Asignar Empleado a Estación ===");

        // Mostrar estaciones
        List<Station> stations = stationService.getAllStations();

        if (stations.isEmpty()) {
            System.out.println("No se encontraron estaciones.");
            return;
        }

        System.out.println("Seleccione estación:");
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            Employee currentEmployee = station.getAssignedEmployee();

            System.out.println((i + 1) + ". Estación " + station.getNumber() + " - Empleado: " +
                    (currentEmployee != null ? currentEmployee.getName() : "Ninguno"));
        }

        // Obtener selección de estación
        System.out.print("Seleccione una estación (1-" + stations.size() + ") o 0 para cancelar: ");

        try {
            int stationSelection = Integer.parseInt(scanner.nextLine().trim());

            if (stationSelection == 0) {
                return;
            }

            if (stationSelection < 1 || stationSelection > stations.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Station selectedStation = stations.get(stationSelection - 1);

            // Obtener empleados disponibles
            List<Employee> employees = userService.getAllEmployees();

            if (employees.isEmpty()) {
                System.out.println("No se encontraron empleados.");
                return;
            }

            System.out.println("\nSeleccione empleado para asignar:");
            System.out.println("0. Eliminar asignación actual");

            for (int i = 0; i < employees.size(); i++) {
                Employee employee = employees.get(i);
                Station currentStation = employee.getAssignedStation();

                System.out.println((i + 1) + ". " + employee.getName() + " - Estación actual: " +
                        (currentStation != null ? currentStation.getNumber() : "Ninguna"));
            }

            // Obtener selección de empleado
            System.out.print("Seleccione un empleado (0-" + employees.size() + "): ");

            int employeeSelection = Integer.parseInt(scanner.nextLine().trim());

            if (employeeSelection < 0 || employeeSelection > employees.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            // Procesar la asignación
            boolean success;

            if (employeeSelection == 0) {
                // Eliminar asignación
                if (selectedStation.getAssignedEmployee() != null) {
                    Employee currentEmployee = selectedStation.getAssignedEmployee();
                    currentEmployee.setAssignedStation(null);
                    userService.updateUser(currentEmployee);

                    selectedStation.setAssignedEmployee(null);
                    success = stationService.getStationById(selectedStation.getId())
                            .map(s -> stationService.getAllStations().contains(s))
                            .orElse(false);

                    if (success) {
                        System.out.println("Empleado removido de la estación " + selectedStation.getNumber() + ".");
                    }
                } else {
                    System.out.println("La estación ya no tiene ningún empleado asignado.");
                    return;
                }
            } else {
                // Asignar empleado
                Employee selectedEmployee = employees.get(employeeSelection - 1);

                success = stationService.assignEmployeeToStation(selectedStation.getId(), selectedEmployee.getId());

                if (success) {
                    System.out.println(selectedEmployee.getName() + " asignado a la estación " +
                            selectedStation.getNumber() + ".");
                }
            }

            if (!success) {
                System.out.println("Error al actualizar la asignación de la estación. Por favor, intente nuevamente.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Maneja la configuración de categorías soportadas por una estación
     */
    private void configureStationCategories() {
        System.out.println("\n=== Configurar Categorías de Estación ===");

        // Mostrar estaciones
        List<Station> stations = stationService.getAllStations();

        if (stations.isEmpty()) {
            System.out.println("No se encontraron estaciones.");
            return;
        }

        System.out.println("Seleccione estación:");
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            System.out.println((i + 1) + ". Estación " + station.getNumber() + " - Categorías: " +
                    station.getSupportedCategories().size());
        }

        // Obtener selección de estación
        System.out.print("Seleccione una estación (1-" + stations.size() + ") o 0 para cancelar: ");

        try {
            int stationSelection = Integer.parseInt(scanner.nextLine().trim());

            if (stationSelection == 0) {
                return;
            }

            if (stationSelection < 1 || stationSelection > stations.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Station selectedStation = stations.get(stationSelection - 1);

            // Mostrar opciones de gestión de categorías
            boolean continueConfig = true;

            while (continueConfig) {
                System.out.println("\nEstación " + selectedStation.getNumber() + " - Configuración de Categorías");
                System.out.println("1. Ver categorías soportadas");
                System.out.println("2. Agregar categoría");
                System.out.println("3. Eliminar categoría");
                System.out.println("0. Volver");

                System.out.print("Seleccione una opción: ");
                String option = scanner.nextLine().trim();

                switch (option) {
                    case "1":
                        viewStationCategories(selectedStation);
                        break;
                    case "2":
                        addCategoryToStation(selectedStation);
                        break;
                    case "3":
                        removeCategoryFromStation(selectedStation);
                        break;
                    case "0":
                        continueConfig = false;
                        break;
                    default:
                        System.out.println("Opción inválida. Por favor, intente nuevamente.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Muestra las categorías soportadas por una estación
     *
     * @param station La estación para la que mostrar categorías
     */
    private void viewStationCategories(Station station) {
        System.out.println("\n=== Estación " + station.getNumber() + " - Categorías Soportadas ===");

        List<Category> categories = station.getSupportedCategories();

        if (categories.isEmpty()) {
            System.out.println("Esta estación aún no soporta ninguna categoría.");
            return;
        }

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " [" + category.getPrefix() + "]" +
                    (category.isActive() ? "" : " (Inactiva)"));
        }
    }

    /**
     * Maneja la adición de una categoría a una estación
     *
     * @param station La estación a la que agregar una categoría
     */
    private void addCategoryToStation(Station station) {
        System.out.println("\n=== Agregar Categoría a Estación " + station.getNumber() + " ===");

        // Obtener categorías que aún no son soportadas por esta estación
        List<Category> allCategories = categoryService.getAllCategories();
        List<Category> supportedCategories = station.getSupportedCategories();

        List<Category> availableCategories = allCategories.stream()
                .filter(c -> !supportedCategories.contains(c))
                .toList();

        if (availableCategories.isEmpty()) {
            System.out.println("No hay más categorías disponibles para agregar.");
            return;
        }

        System.out.println("Seleccione una categoría para agregar:");
        for (int i = 0; i < availableCategories.size(); i++) {
            Category category = availableCategories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " [" + category.getPrefix() + "]" +
                    (category.isActive() ? "" : " (Inactiva)"));
        }

        // Obtener selección de categoría
        System.out.print("Seleccione una categoría (1-" + availableCategories.size() + ") o 0 para cancelar: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > availableCategories.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Category selectedCategory = availableCategories.get(selection - 1);

            if (stationService.addCategoryToStation(station.getId(), selectedCategory.getId())) {
                System.out.println("Categoría '" + selectedCategory.getName() +
                        "' ha sido agregada a la Estación " + station.getNumber() + ".");
            } else {
                System.out.println("Error al agregar la categoría a la estación. Por favor, intente nuevamente.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Maneja la eliminación de una categoría de una estación
     *
     * @param station La estación de la que eliminar una categoría
     */
    private void removeCategoryFromStation(Station station) {
        System.out.println("\n=== Eliminar Categoría de Estación " + station.getNumber() + " ===");

        List<Category> supportedCategories = station.getSupportedCategories();

        if (supportedCategories.isEmpty()) {
            System.out.println("Esta estación aún no soporta ninguna categoría.");
            return;
        }

        System.out.println("Seleccione una categoría para eliminar:");
        for (int i = 0; i < supportedCategories.size(); i++) {
            Category category = supportedCategories.get(i);
            System.out.println((i + 1) + ". " + category.getName() + " [" + category.getPrefix() + "]");
        }

        // Obtener selección de categoría
        System.out.print("Seleccione una categoría (1-" + supportedCategories.size() + ") o 0 para cancelar: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > supportedCategories.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Category selectedCategory = supportedCategories.get(selection - 1);

            System.out.println("¿Está seguro de que desea eliminar la categoría '" +
                    selectedCategory.getName() + "' de esta estación? (s/n)");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("s") || confirmation.equals("si")) {
                if (stationService.removeCategoryFromStation(station.getId(), selectedCategory.getId())) {
                    System.out.println("Categoría '" + selectedCategory.getName() +
                            "' ha sido eliminada de la Estación " + station.getNumber() + ".");
                } else {
                    System.out.println("Error al eliminar la categoría de la estación. Por favor, intente nuevamente.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Muestra y maneja el submenú de gestión de empleados
     */
    private void manageEmployeesMenu() {
        boolean continueMenu = true;

        while (continueMenu) {
            System.out.println("\n=== Gestionar Empleados ===");
            System.out.println("1. Ver todos los empleados");
            System.out.println("2. Registrar nuevo empleado");
            System.out.println("3. Asignar empleado a categoría");
            System.out.println("4. Editar detalles de empleado");
            System.out.println("0. Volver al menú principal");

            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    viewAllEmployees();
                    break;
                case "2":
                    registerEmployee();
                    break;
                case "3":
                    assignEmployeeToCategory();
                    break;
                case "4":
                    editEmployeeDetails();
                    break;
                case "0":
                    continueMenu = false;
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, intente nuevamente.");
            }
        }
    }

    /**
     * Muestra todos los empleados
     */
    private void viewAllEmployees() {
        System.out.println("\n=== Todos los Empleados ===");

        List<Employee> employees = userService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No se encontraron empleados.");
            return;
        }

        for (Employee employee : employees) {
            System.out.println("ID: " + employee.getId());
            System.out.println("Nombre: " + employee.getName());
            System.out.println("Estado: " + translateEmployeeStatus(employee.getAvailabilityStatus()));

            Station station = employee.getAssignedStation();
            System.out.println("Estación asignada: " +
                    (station != null ? station.getNumber() : "Ninguna"));

            System.out.println("Turnos atendidos hoy: " + employee.getAttendedTickets().size());
            System.out.println("-----");
        }
    }

    /**
     * Traduce los estados del empleado a español
     *
     * @param status El estado del empleado en inglés
     * @return El estado del empleado traducido al español
     */
    private String translateEmployeeStatus(String status) {
        switch (status) {
            case "AVAILABLE": return "Disponible";
            case "BUSY": return "Ocupado";
            case "PAUSED": return "Pausado";
            case "OFFLINE": return "Desconectado";
            default: return status;
        }
    }

    /**
     * Maneja el registro de un nuevo empleado
     */
    private void registerEmployee() {
        System.out.println("\n=== Registrar Nuevo Empleado ===");

        System.out.print("Ingrese ID de empleado: ");
        String id = scanner.nextLine().trim();

        // Verificar si el ID ya existe
        if (userService.findUserById(id).isPresent()) {
            System.out.println("Ya existe un usuario con este ID. Por favor use un ID diferente.");
            return;
        }

        System.out.print("Ingrese nombre del empleado: ");
        String name = scanner.nextLine().trim();

        System.out.print("Ingrese contraseña del empleado: ");
        String password = scanner.nextLine().trim();

        if (id.isEmpty() || name.isEmpty() || password.isEmpty()) {
            System.out.println("ID, nombre y contraseña son obligatorios. Registro cancelado.");
            return;
        }

        Employee newEmployee = new Employee(id, name, password);

        if (userService.registerEmployee(newEmployee)) {
            System.out.println("¡Empleado registrado exitosamente!");
            System.out.println("ID: " + newEmployee.getId());
            System.out.println("Nombre: " + newEmployee.getName());
        } else {
            System.out.println("Error al registrar el empleado. Por favor, intente nuevamente.");
        }
    }

    /**
     * Maneja la asignación de un empleado a una categoría
     */
    private void assignEmployeeToCategory() {
        System.out.println("\n=== Asignar Empleado a Categoría ===");

        // Mostrar empleados
        List<Employee> employees = userService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No se encontraron empleados.");
            return;
        }

        System.out.println("Seleccione empleado:");
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            System.out.println((i + 1) + ". " + employee.getName());
        }

        // Obtener selección de empleado
        System.out.print("Seleccione un empleado (1-" + employees.size() + ") o 0 para cancelar: ");

        try {
            int employeeSelection = Integer.parseInt(scanner.nextLine().trim());

            if (employeeSelection == 0) {
                return;
            }

            if (employeeSelection < 1 || employeeSelection > employees.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Employee selectedEmployee = employees.get(employeeSelection - 1);

            // Mostrar categorías
            List<Category> categories = categoryService.getAllCategories();

            if (categories.isEmpty()) {
                System.out.println("No se encontraron categorías.");
                return;
            }

            System.out.println("\nSeleccione categoría para asignar a " + selectedEmployee.getName() + ":");
            for (int i = 0; i < categories.size(); i++) {
                Category category = categories.get(i);
                boolean isAssigned = category.getAssignedEmployees().contains(selectedEmployee);

                System.out.println((i + 1) + ". " + category.getName() +
                        (isAssigned ? " [Ya asignado]" : ""));
            }

            // Obtener selección de categoría
            System.out.print("Seleccione una categoría (1-" + categories.size() + ") o 0 para cancelar: ");

            int categorySelection = Integer.parseInt(scanner.nextLine().trim());

            if (categorySelection == 0) {
                return;
            }

            if (categorySelection < 1 || categorySelection > categories.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Category selectedCategory = categories.get(categorySelection - 1);

            // Verificar si ya está asignado
            if (selectedCategory.getAssignedEmployees().contains(selectedEmployee)) {
                System.out.println("Este empleado ya está asignado a esta categoría.");
                System.out.println("¿Desea eliminar la asignación en su lugar? (s/n)");

                String confirmation = scanner.nextLine().trim().toLowerCase();

                if (confirmation.equals("s") || confirmation.equals("si")) {
                    if (categoryService.removeEmployeeFromCategory(selectedCategory.getId(), selectedEmployee)) {
                        System.out.println(selectedEmployee.getName() + " ha sido removido de la categoría '" +
                                selectedCategory.getName() + "'.");
                    } else {
                        System.out.println("Error al remover el empleado de la categoría. Por favor, intente nuevamente.");
                    }
                } else {
                    System.out.println("Operación cancelada.");
                }
            } else {
                // Asignar empleado a categoría
                if (categoryService.assignEmployeeToCategory(selectedCategory.getId(), selectedEmployee)) {
                    System.out.println(selectedEmployee.getName() + " ha sido asignado a la categoría '" +
                            selectedCategory.getName() + "'.");
                } else {
                    System.out.println("Error al asignar el empleado a la categoría. Por favor, intente nuevamente.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Maneja la edición de detalles de un empleado
     */
    private void editEmployeeDetails() {
        System.out.println("\n=== Editar Detalles de Empleado ===");

        // Mostrar empleados
        List<Employee> employees = userService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No se encontraron empleados.");
            return;
        }

        System.out.println("Seleccione empleado:");
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            System.out.println((i + 1) + ". " + employee.getName() + " (ID: " + employee.getId() + ")");
        }

        // Obtener selección de empleado
        System.out.print("Seleccione un empleado (1-" + employees.size() + ") o 0 para cancelar: ");

        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > employees.size()) {
                System.out.println("Selección inválida. Por favor, intente nuevamente.");
                return;
            }

            Employee selectedEmployee = employees.get(selection - 1);

            // Actualizar campos
            System.out.println("Editando empleado: " + selectedEmployee.getName());
            System.out.println("Deje el campo vacío para mantener el valor actual");

            System.out.print("Nuevo nombre [" + selectedEmployee.getName() + "]: ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) {
                selectedEmployee.setName(name);
            }

            System.out.print("Nueva contraseña (presione Enter para omitir): ");
            String password = scanner.nextLine().trim();
            if (!password.isEmpty()) {
                selectedEmployee.setPassword(password);
            }

            // Guardar cambios
            if (userService.updateUser(selectedEmployee)) {
                System.out.println("¡Empleado actualizado exitosamente!");
            } else {
                System.out.println("Error al actualizar el empleado. Por favor, intente nuevamente.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor ingrese un número.");
        }
    }

    /**
     * Muestra estadísticas del sistema
     */
    private void viewStatistics() {
        System.out.println("\n=== Estadísticas del Sistema ===");
        System.out.println(statisticsService.getCurrentStatistics().generateDailyStatistics());

        // Obtener estadísticas adicionales
        Map<String, Double> employeeProductivity = statisticsService.getEmployeeProductivityStatistics();

        if (!employeeProductivity.isEmpty()) {
            System.out.println("\nProductividad de Empleados (turnos por hora):");

            for (Map.Entry<String, Double> entry : employeeProductivity.entrySet()) {
                Optional<Employee> employeeOpt = userService.getEmployeeById(entry.getKey());

                if (employeeOpt.isPresent()) {
                    System.out.println(employeeOpt.get().getName() + ": " +
                            String.format("%.2f", entry.getValue()) + " turnos/hora");
                }
            }
        }

        // Mostrar estadísticas por categoría
        List<Category> categories = categoryService.getAllCategories();

        if (!categories.isEmpty()) {
            System.out.println("\nEstadísticas por Categoría:");

            for (Category category : categories) {
                System.out.println(category.getName() + ":");
                System.out.println("  Activa: " + (category.isActive() ? "Sí" : "No"));
                System.out.println("  Turnos pendientes: " + category.countPendingTickets());

                double avgWaitTime = statisticsService.getAverageWaitingTimeByCategory(category.getId());
                System.out.println("  Tiempo promedio de espera: " + String.format("%.2f", avgWaitTime) + " minutos");
            }
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Genera informes del sistema
     */
    private void generateReports() {
        boolean continueMenu = true;

        while (continueMenu) {
            System.out.println("\n=== Generar Informes ===");
            System.out.println("1. Informe de productividad diaria");
            System.out.println("2. Informe de productividad semanal");
            System.out.println("3. Informe de productividad mensual");
            System.out.println("4. Informe de rendimiento de empleados");
            System.out.println("0. Volver al menú principal");

            System.out.print("Seleccione una opción: ");
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    System.out.println("\n=== Informe de Productividad Diaria ===");
                    System.out.println(statisticsService.generateDailyStatistics());
                    System.out.println("\nPresione Enter para continuar...");
                    scanner.nextLine();
                    break;
                case "2":
                    System.out.println("\n=== Informe de Productividad Semanal ===");
                    System.out.println(statisticsService.generateWeeklyStatistics());
                    System.out.println("\nPresione Enter para continuar...");
                    scanner.nextLine();
                    break;
                case "3":
                    System.out.println("\n=== Informe de Productividad Mensual ===");
                    System.out.println(statisticsService.generateMonthlyStatistics());
                    System.out.println("\nPresione Enter para continuar...");
                    scanner.nextLine();
                    break;
                case "4":
                    generateEmployeePerformanceReport();
                    break;
                case "0":
                    continueMenu = false;
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, intente nuevamente.");
            }
        }
    }

    /**
     * Genera un informe de rendimiento de empleados
     */
    private void generateEmployeePerformanceReport() {
        System.out.println("\n=== Informe de Rendimiento de Empleados ===");

        List<Employee> employees = userService.getAllEmployees();

        if (employees.isEmpty()) {
            System.out.println("No se encontraron empleados.");
            return;
        }

        System.out.println("Métricas de rendimiento para hoy:");

        for (Employee employee : employees) {
            System.out.println("\nEmpleado: " + employee.getName() + " (ID: " + employee.getId() + ")");
            System.out.println("Estado: " + translateEmployeeStatus(employee.getAvailabilityStatus()));

            Station station = employee.getAssignedStation();
            System.out.println("Estación asignada: " + (station != null ? station.getNumber() : "Ninguna"));

            List<Ticket> tickets = employee.getAttendedTickets();
            System.out.println("Turnos atendidos: " + tickets.size());

            if (!tickets.isEmpty()) {
                // Calcular métricas
                double totalServiceTime = 0;
                int completedTickets = 0;

                for (Ticket ticket : tickets) {
                    if ("COMPLETED".equals(ticket.getStatus())) {
                        totalServiceTime += ticket.calculateServiceTime();
                        completedTickets++;
                    }
                }

                if (completedTickets > 0) {
                    double averageServiceTime = totalServiceTime / completedTickets;
                    System.out.println("Tiempo promedio de servicio: " +
                            String.format("%.2f", averageServiceTime) + " minutos");

                    // Obtener productividad del servicio de estadísticas
                    double productivity = statisticsService.getEmployeeProductivityStatistics()
                            .getOrDefault(employee.getId(), 0.0);

                    System.out.println("Productividad: " + String.format("%.2f", productivity) + " turnos por hora");
                } else {
                    System.out.println("No hay turnos completados para calcular métricas.");
                }
            }
        }

        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Inicia el menú de administrador después de la autenticación
     */
    public void start() {
        if (authenticate()) {
            start(scanner);
        }
    }
}