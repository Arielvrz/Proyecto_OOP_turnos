package com.queuemanagementsystem.model;

import java.util.Objects;

/**
 * Clase base que representa a cualquier usuario que interactúa con el sistema.
 * Esta sirve como clase padre para Employee y Administrator.
 */
public class User {
    private String id;
    private String name;
    private String password;

    /**
     * Constructor por defecto
     */
    public User() {
    }

    /**
     * Constructor parametrizado
     *
     * @param id Identificador único del usuario
     * @param name Nombre completo del usuario
     * @param password Contraseña de autenticación del usuario
     */
    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    /**
     * Autentica a un usuario con las credenciales proporcionadas
     *
     * @param providedId El ID ingresado por el usuario
     * @param providedPassword La contraseña ingresada por el usuario
     * @return true si la autenticación es exitosa, false en caso contrario
     */
    public boolean login(String providedId, String providedPassword) {
        boolean matches = this.id.equals(providedId) && this.password.equals(providedPassword);
        System.out.println("Debug - Intento de inicio de sesión: " + providedId);
        System.out.println("Debug - Coincidencia de ID: " + this.id.equals(providedId));
        System.out.println("Debug - Coincidencia de contraseña: " + this.password.equals(providedPassword));
        return matches;
    }

    /**
     * Simula la funcionalidad de cierre de sesión del usuario
     *
     * @return true indicando cierre de sesión exitoso
     */
    public boolean logout() {
        // En un sistema real, esto invalidaría sesiones, etc.
        return true;
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Nota: No proporcionamos un getter para password por razones de seguridad
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Compara este Usuario con otro objeto para determinar igualdad
     *
     * @param o El objeto a comparar
     * @return true si los objetos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    /**
     * Genera un código hash para este Usuario
     *
     * @return El código hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Devuelve una representación en string de este Usuario
     *
     * @return Una representación en string
     */
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}