package com.queuemanagementsystem.repository;

import com.queuemanagementsystem.model.Station;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Station data access operations.
 */
public interface StationRepository {
    /**
     * Saves a station to the repository
     *
     * @param station The station to save
     * @return true if save was successful, false otherwise
     */
    boolean save(Station station);

    /**
     * Finds a station by its ID
     *
     * @param id The station ID to search for
     * @return Optional containing the station if found, empty otherwise
     */
    Optional<Station> findById(int id);

    /**
     * Finds a station by its number
     *
     * @param number The station number to search for
     * @return Optional containing the station if found, empty otherwise
     */
    Optional<Station> findByNumber(int number);

    /**
     * Gets all stations in the repository
     *
     * @return List of all stations
     */
    List<Station> findAll();

    /**
     * Gets all open stations in the repository
     *
     * @return List of all open stations
     */
    List<Station> findAllOpen();

    /**
     * Deletes a station by its ID
     *
     * @param id The ID of the station to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteById(int id);

    /**
     * Updates an existing station
     *
     * @param station The station with updated information
     * @return true if update was successful, false otherwise
     */
    boolean update(Station station);

    /**
     * Saves all stations to persistent storage
     *
     * @return true if save was successful, false otherwise
     */
    boolean saveAll();

    /**
     * Loads all stations from persistent storage
     *
     * @return true if load was successful, false otherwise
     */
    boolean loadAll();
}