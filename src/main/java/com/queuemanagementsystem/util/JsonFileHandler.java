package com.queuemanagementsystem.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.queuemanagementsystem.model.Administrator;
import com.queuemanagementsystem.model.Employee;
import com.queuemanagementsystem.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for handling JSON file operations with proper type adapters.
 */
public class JsonFileHandler {
    private static final Gson gson = createGson();

    /**
     * Creates a configured Gson instance with custom type adapters
     *
     * @return Configured Gson instance
     */
    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls(); // Optional, but helpful
        gsonBuilder.disableHtmlEscaping(); // Optional, but helps with readability

        // Register type adapter for LocalDateTime
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());

        // Register custom user adapter
        gsonBuilder.registerTypeAdapter(User.class, new UserAdapter());

        return gsonBuilder.create();
    }

    /**
     * Saves a list of objects to a JSON file
     *
     * @param <T> The type of objects in the list
     * @param list The list of objects to save
     * @param filePath The path to the JSON file
     * @return true if the operation was successful, false otherwise
     */
    public static <T> boolean saveToFile(List<T> list, String filePath) {
        try {
            // crear directorios principales si no existen
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(list, writer);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error saving to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads a list of objects from a JSON file
     *
     * @param <T> The type of objects in the list
     * @param filePath The path to the JSON file
     * @param type The type of the list (e.g., new TypeToken<List<User>>(){}.getType())
     * @return The list of objects loaded from the file, or an empty list if the file doesn't exist
     */
    public static <T> List<T> loadFromFile(String filePath, Type type) {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        } catch (IOException | JsonParseException e) {
            System.err.println("Error loading from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static class UserAdapter implements JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            // Determine the type based on the presence of specific fields
            if (jsonObject.has("accessLevel")) {
                // This is an Administrator (only administrators have accessLevel)
                return context.deserialize(jsonObject, Administrator.class);
            } else if (jsonObject.has("availabilityStatus") || jsonObject.has("attendedTickets")) {
                // This is an Employee
                return context.deserialize(jsonObject, Employee.class);
            } else {
                // This is a base User
                return context.deserialize(jsonObject, User.class);
            }
        }
    }

    /**
     * adaptador personalizado para LocalDateTimeAdapter
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatter.format(src));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
}