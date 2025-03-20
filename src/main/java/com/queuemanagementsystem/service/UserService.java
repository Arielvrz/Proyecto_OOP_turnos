package com.queuemanagementsystem.service;

import com.queuemanagementsystem.model.Administrator;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.User;
import com.queuemanagementsystem.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Clase de servicio para gestionar usuarios (empleados y administradores).
 */
public class UserService {
    private final UserRepository userRepository;

    /**
     * Constructor con dependencia del repositorio
     *
     * @param userRepository El repositorio para datos de usuario
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Autentica a un usuario con las credenciales proporcionadas
     *
     * @param id ID del usuario
     * @param password Contraseña del usuario
     * @return El objeto User autenticado si tiene éxito, null en caso contrario
     */
    public User authenticate(String id, String password) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent() && user.get().login(id, password)) {
            return user.get();
        }

        return null;
    }

    /**
     * Busca un usuario por ID sin autenticación
     *
     * @param id ID del usuario
     * @return Optional que contiene el usuario si se encuentra
     */
    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }

    /**
     * Registra un nuevo empleado en el sistema
     *
     * @param employee El empleado a registrar
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean registerEmployee(Employee employee) {
        if (employee == null || employee.getId() == null || userRepository.findById(employee.getId()).isPresent()) {
            return false;
        }

        return userRepository.save(employee).logout();
    }

    /**
     * Registra un nuevo administrador en el sistema
     *
     * @param administrator El administrador a registrar
     * @return true si el registro fue exitoso, false en caso contrario
     */
    public boolean registerAdministrator(Administrator administrator) {
        if (administrator == null || administrator.getId() == null ||
                userRepository.findById(administrator.getId()).isPresent()) {
            return false;
        }

        return userRepository.save(administrator).logout();
    }

    /**
     * Actualiza la información de un usuario existente
     *
     * @param user El usuario con información actualizada
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null || !userRepository.findById(user.getId()).isPresent()) {
            return false;
        }

        return userRepository.update(user);
    }

    /**
     * Elimina un usuario del sistema
     *
     * @param userId El ID del usuario a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    public boolean deleteUser(String userId) {
        return userRepository.delete(userId);
    }

    /**
     * Obtiene todos los empleados registrados en el sistema
     *
     * @return Lista de todos los empleados
     */
    public List<Employee> getAllEmployees() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Employee)
                .map(user -> (Employee) user)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los administradores registrados en el sistema
     *
     * @return Lista de todos los administradores
     */
    public List<Administrator> getAllAdministrators() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Administrator)
                .map(user -> (Administrator) user)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un empleado por ID
     *
     * @param employeeId El ID del empleado
     * @return Optional que contiene el empleado si se encuentra, vacío en caso contrario
     */
    public Optional<Employee> getEmployeeById(String employeeId) {
        Optional<User> user = userRepository.findById(employeeId);

        if (user.isPresent() && user.get() instanceof Employee) {
            return Optional.of((Employee) user.get());
        }

        return Optional.empty();
    }

    /**
     * Obtiene un administrador por ID
     *
     * @param adminId El ID del administrador
     * @return Optional que contiene el administrador si se encuentra, vacío en caso contrario
     */
    public Optional<Administrator> getAdministratorById(String adminId) {
        Optional<User> user = userRepository.findById(adminId);

        if (user.isPresent() && user.get() instanceof Administrator) {
            return Optional.of((Administrator) user.get());
        }

        return Optional.empty();
    }
}