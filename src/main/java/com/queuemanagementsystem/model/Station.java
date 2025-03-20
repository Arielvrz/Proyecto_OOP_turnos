package com.queuemanagementsystem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa una estación física donde los empleados atienden a los clientes.
 */
public class Station {
    private int id;
    private int number;
    private String status; // "OPEN", "CLOSED"
    private Employee assignedEmployee;
    private List<Integer> supportedCategoryIds; // Cambiado de List<Category> a List<Integer>

    /**
     * Constructor por defecto
     */
    public Station() {
        this.status = "CLOSED";
        this.supportedCategoryIds = new ArrayList<>();
    }

    /**
     * Constructor parametrizado con campos esenciales
     *
     * @param id Identificador único de la estación
     * @param number Número de visualización de la estación
     */
    public Station(int id, int number) {
        this.id = id;
        this.number = number;
        this.status = "CLOSED";
        this.supportedCategoryIds = new ArrayList<>();
    }

    /**
     * Constructor completo con todos los campos
     *
     * @param id Identificador único de la estación
     * @param number Número de visualización de la estación
     * @param status Estado operacional de la estación
     * @param assignedEmployee Empleado asignado a esta estación
     */
    public Station(int id, int number, String status, Employee assignedEmployee) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.assignedEmployee = assignedEmployee;
        this.supportedCategoryIds = new ArrayList<>();
    }

    /**
     * Abre la estación para el servicio
     *
     * @return true si la estación se abrió con éxito, false en caso contrario
     */
    public boolean openStation() {
        if (assignedEmployee != null) {
            this.status = "OPEN";
            return true;
        }
        return false;
    }

    /**
     * Cierra la estación
     *
     * @return true indicando que la estación fue cerrada
     */
    public boolean closeStation() {
        this.status = "CLOSED";
        return true;
    }

    /**
     * Añade una categoría de servicio a esta estación
     *
     * @param category La categoría a añadir
     * @return true si la categoría fue añadida con éxito, false en caso contrario
     */
    public boolean addCategory(Category category) {
        if (category != null && !supportedCategoryIds.contains(category.getId())) {
            return supportedCategoryIds.add(category.getId());
        }
        return false;
    }

    /**
     * Elimina una categoría de servicio de esta estación
     *
     * @param category La categoría a eliminar
     * @return true si la categoría fue eliminada con éxito, false en caso contrario
     */
    public boolean removeCategory(Category category) {
        if (category != null) {
            return supportedCategoryIds.remove(Integer.valueOf(category.getId()));
        }
        return false;
    }

    /**
     * Verifica si esta estación soporta una categoría específica
     *
     * @param category La categoría a verificar
     * @return true si la estación soporta la categoría, false en caso contrario
     */
    public boolean supportsCategory(Category category) {
        return category != null && supportedCategoryIds.contains(category.getId());
    }

    /**
     * Obtiene la lista de IDs de categorías soportadas
     *
     * @return La lista de IDs de categorías soportadas por esta estación
     */
    public List<Integer> getSupportedCategoryIds() {
        return new ArrayList<>(supportedCategoryIds);
    }

    /**
     * Obtiene la lista de categorías soportadas
     *
     * @param categoryRepository El repositorio para buscar categorías
     * @return La lista de categorías soportadas por esta estación
     */
    public List<Category> getSupportedCategories(com.queuemanagementsystem.repository.CategoryRepository categoryRepository) {
        List<Category> categories = new ArrayList<>();
        for (Integer id : supportedCategoryIds) {
            categoryRepository.findById(id).ifPresent(categories::add);
        }
        return categories;
    }

    /**
     * Obtiene la lista de categorías soportadas (para compatibilidad hacia atrás)
     *
     * @return Una lista de categorías de marcador de posición (Nota: use getSupportedCategories(repository) en su lugar)
     */
    public List<Category> getSupportedCategories() {
        // Este es un método de marcador de posición para compatibilidad hacia atrás
        // Devuelve una lista vacía ya que no podemos buscar las categorías reales sin un repositorio
        return new ArrayList<>();
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(Employee employee) {
        this.assignedEmployee = employee;
        if (employee != null) {
            employee.setAssignedStation(this);
        }
    }

    public void setSupportedCategoryIds(List<Integer> supportedCategoryIds) {
        this.supportedCategoryIds = supportedCategoryIds != null ?
                new ArrayList<>(supportedCategoryIds) : new ArrayList<>();
    }

    /**
     * Compara esta Estación con otro objeto para determinar igualdad
     *
     * @param o El objeto a comparar
     * @return true si los objetos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id == station.id;
    }

    /**
     * Genera un código hash para esta Estación
     *
     * @return El código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Devuelve una representación en string de esta Estación
     *
     * @return Una representación en string
     */
    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", number=" + number +
                ", status='" + status + '\'' +
                ", employee=" + (assignedEmployee != null ? assignedEmployee.getName() : "None") +
                ", supportedCategories=" + supportedCategoryIds.size() +
                '}';
    }
}