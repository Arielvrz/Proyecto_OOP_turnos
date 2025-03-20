package com.queuemanagementsystem.repository;

import com.google.gson.reflect.TypeToken;
import com.queuemanagementsystem.model.Station;
import com.queuemanagementsystem.util.JsonFileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de StationRepository usando archivo JSON para persistencia.
 */
public class JsonStationRepository implements StationRepository {
    private static final String FILE_PATH = "data/stations.json";
    private List<Station> stations;

    /**
     * Constructor por defecto. Carga estaciones desde el archivo.
     */
    public JsonStationRepository() {
        this.stations = new ArrayList<>();
        loadAll();
    }

    @Override
    public boolean save(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("La estación no puede ser nula");
        }

        // Verifica si la estación ya existe
        Optional<Station> existingStation = findById(station.getId());
        if (existingStation.isPresent()) {
            // Actualiza la estación existente
            stations.remove(existingStation.get());
        }

        stations.add(station);
        return saveAll();
    }

    @Override
    public Optional<Station> findById(int id) {
        return stations.stream()
                .filter(station -> station.getId() == id)
                .findFirst();
    }

    @Override
    public Optional<Station> findByNumber(int number) {
        return stations.stream()
                .filter(station -> station.getNumber() == number)
                .findFirst();
    }

    @Override
    public List<Station> findAll() {
        return new ArrayList<>(stations);
    }

    @Override
    public List<Station> findAllOpen() {
        return stations.stream()
                .filter(station -> "OPEN".equals(station.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Station> station = findById(id);
        if (station.isPresent()) {
            boolean removed = stations.remove(station.get());
            if (removed) {
                saveAll();
            }
            return removed;
        }
        return false;
    }

    @Override
    public boolean update(Station station) {
        if (station == null) {
            return false;
        }

        Optional<Station> existingStation = findById(station.getId());
        if (!existingStation.isPresent()) {
            return false;
        }

        stations.remove(existingStation.get());
        stations.add(station);
        return saveAll();
    }

    @Override
    public boolean saveAll() {
        return JsonFileHandler.saveToFile(stations, FILE_PATH);
    }

    @Override
    public boolean loadAll() {
        TypeToken<List<Station>> typeToken = new TypeToken<List<Station>>() {};
        this.stations = JsonFileHandler.loadFromFile(FILE_PATH, typeToken.getType());
        return true;
    }
}