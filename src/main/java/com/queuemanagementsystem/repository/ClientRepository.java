package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.Client;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio para operaciones de acceso a datos de Cliente.
 */
public interface ClientRepository {
    /**
     * Guarda un cliente en el repositorio
     *
     * @param client El cliente a guardar
     * @return El cliente guardado
     */
    Client save(Client client);

    /**
     * Encuentra un cliente por su ID
     *
     * @param id El ID del cliente a buscar
     * @return Optional conteniendo el cliente si se encuentra, vacío en caso contrario
     */
    Optional<Client> findById(String id);

    /**
     * Obtiene todos los clientes en el repositorio
     *
     * @return Lista de todos los clientes
     */
    List<Client> findAll();

    /**
     * Elimina un cliente por su ID
     *
     * @param id El ID del cliente a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteById(String id);

    /**
     * Guarda todos los clientes en almacenamiento persistente
     *
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    boolean saveAll();

    /**
     * Carga todos los clientes desde almacenamiento persistente
     *
     * @return true si la carga fue exitosa, false en caso contrario
     */
    boolean loadAll();
}
