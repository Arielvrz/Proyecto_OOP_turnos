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
 * Clase de utilidad para manejar operaciones de archivos JSON con adaptadores de tipo adecuados.
 */
public class JsonFileHandler {
    private static final Gson gson = createGson();

    /**
     * Crea una instancia de Gson configurada con adaptadores de tipo personalizados
     *
     * @return Instancia de Gson configurada
     */
    private static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls(); // Opcional, pero útil
        gsonBuilder.disableHtmlEscaping(); // Opcional, pero ayuda con la legibilidad

        // Registra adaptador de tipo para LocalDateTime
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());

        // Registra adaptador personalizado para User
        gsonBuilder.registerTypeAdapter(User.class, new UserAdapter());

        return gsonBuilder.create();
    }

    /**
     * Guarda una lista de objetos en un archivo JSON
     *
     * @param <T> El tipo de objetos en la lista
     * @param list La lista de objetos a guardar
     * @param filePath La ruta al archivo JSON
     * @return true si la operación fue exitosa, false en caso contrario
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
            System.err.println("Error al guardar en archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Carga una lista de objetos desde un archivo JSON
     *
     * @param <T> El tipo de objetos en la lista
     * @param filePath La ruta al archivo JSON
     * @param type El tipo de la lista (ej., new TypeToken<List<User>>(){}.getType())
     * @return La lista de objetos cargada desde el archivo, o una lista vacía si el archivo no existe
     */
    public static <T> List<T> loadFromFile(String filePath, Type type) {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        } catch (IOException | JsonParseException e) {
            System.err.println("Error al cargar desde archivo: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static class UserAdapter implements JsonDeserializer<User> {
        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            // Determina el tipo basado en la presencia de campos específicos
            if (jsonObject.has("accessLevel")) {
                // Esto es un Administrator (solo los administradores tienen accessLevel)
                return context.deserialize(jsonObject, Administrator.class);
            } else if (jsonObject.has("availabilityStatus") || jsonObject.has("attendedTickets")) {
                // Esto es un Employee
                return context.deserialize(jsonObject, Employee.class);
            } else {
                // Esto es un User base
                return context.deserialize(jsonObject, User.class);
            }
        }
    }

    /**
     * Adaptador personalizado para LocalDateTimeAdapter
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