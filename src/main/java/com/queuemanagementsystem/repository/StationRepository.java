package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.Station;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio para operaciones de acceso a datos de Estación.
 */
public interface StationRepository {
    /**
     * Guarda una estación en el repositorio
     *
     * @param station La estación a guardar
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    boolean save(Station station);

    /**
     * Busca una estación por su ID
     *
     * @param id El ID de la estación a buscar
     * @return Optional que contiene la estación si se encuentra, vacío en caso contrario
     */
    Optional<Station> findById(int id);

    /**
     * Busca una estación por su número
     *
     * @param number El número de estación a buscar
     * @return Optional que contiene la estación si se encuentra, vacío en caso contrario
     */
    Optional<Station> findByNumber(int number);

    /**
     * Obtiene todas las estaciones en el repositorio
     *
     * @return Lista de todas las estaciones
     */
    List<Station> findAll();

    /**
     * Obtiene todas las estaciones abiertas en el repositorio
     *
     * @return Lista de todas las estaciones abiertas
     */
    List<Station> findAllOpen();

    /**
     * Elimina una estación por su ID
     *
     * @param id El ID de la estación a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteById(int id);

    /**
     * Actualiza una estación existente
     *
     * @param station La estación con información actualizada
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean update(Station station);

    /**
     * Guarda todas las estaciones en almacenamiento persistente
     *
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    boolean saveAll();

    /**
     * Carga todas las estaciones desde almacenamiento persistente
     *
     * @return true si la carga fue exitosa, false en caso contrario
     */
    boolean loadAll();
}