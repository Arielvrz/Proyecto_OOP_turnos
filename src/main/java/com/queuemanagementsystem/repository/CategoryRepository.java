package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.Category;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de repositorio para operaciones de acceso a datos de Categoría.
 */
public interface CategoryRepository {
    /**
     * Guarda una categoría en el repositorio
     *
     * @param category La categoría a guardar
     * @return La categoría guardada
     */
    Category save(Category category);

    /**
     * Encuentra una categoría por su ID
     *
     * @param id El ID de categoría a buscar
     * @return Optional conteniendo la categoría si se encuentra, vacío en caso contrario
     */
    Optional<Category> findById(int id);

    /**
     * Obtiene todas las categorías en el repositorio
     *
     * @return Lista de todas las categorías
     */
    List<Category> findAll();

    /**
     * Obtiene todas las categorías activas en el repositorio
     *
     * @return Lista de categorías activas
     */
    List<Category> findAllActive();

    /**
     * Elimina una categoría por su ID
     *
     * @param id El ID de la categoría a eliminar
     * @return true si la eliminación fue exitosa, false en caso contrario
     */
    boolean deleteById(int id);

    /**
     * Encuentra una categoría por su prefijo
     *
     * @param prefix El prefijo de categoría a buscar
     * @return Optional conteniendo la categoría si se encuentra, vacío en caso contrario
     */
    Optional<Category> findByPrefix(String prefix);

    /**
     * Actualiza una categoría existente
     *
     * @param category La categoría con información actualizada
     * @return true si la actualización fue exitosa, false en caso contrario
     */
    boolean update(Category category);

    /**
     * Guarda todas las categorías en almacenamiento persistente
     *
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    boolean saveAll();

    /**
     * Carga todas las categorías desde almacenamiento persistente
     *
     * @return true si la carga fue exitosa, false en caso contrario
     */
    boolean loadAll();
}