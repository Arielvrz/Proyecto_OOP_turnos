package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.User;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.Administrator;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio para operaciones de acceso a datos de Usuario.
 */
public interface UserRepository {
    /**
     * Guarda un usuario en el repositorio
     *
     * @param user El usuario a guardar
     * @return El usuario guardado
     */
    User save(User user);

    /**
     * Busca un usuario por su ID
     *
     * @param id El ID del usuario a buscar
     * @return Optional que contiene el usuario si se encuentra, vacío en caso contrario
     */
    Optional<User> findById(String id);

    /**
     * Obtiene todos los usuarios en el repositorio
     *
     * @return Lista de todos los usuarios
     */
    List<User> findAll();

    /**
     * Elimina un usuario por su ID
     *
     * @param id El ID del usuario a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteById(String id);

    /**
     * Autentica un usuario con las credenciales proporcionadas
     *
     * @param id El ID del usuario
     * @param password La contraseña del usuario
     * @return Optional que contiene el usuario autenticado si es exitoso, vacío en caso contrario
     */
    Optional<User> authenticate(String id, String password);

    /**
     * Guarda todos los usuarios en almacenamiento persistente
     *
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    boolean saveAll();

    /**
     * Carga todos los usuarios desde almacenamiento persistente
     *
     * @return true si la carga fue exitosa, false en caso contrario
     */
    boolean loadAll();

    /**
     * Actualiza un usuario existente
     *
     * @param user El usuario con información actualizada
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean update(User user);

    /**
     * Elimina un usuario
     *
     * @param id El ID del usuario a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean delete(String id);

    /**
     * Busca todos los empleados en el repositorio
     *
     * @return Una lista de todos los empleados
     */
    List<Employee> findAllEmployees();

    /**
     * Busca todos los administradores en el repositorio
     *
     * @return Una lista de todos los administradores
     */
    List<Administrator> findAllAdministrators();
}