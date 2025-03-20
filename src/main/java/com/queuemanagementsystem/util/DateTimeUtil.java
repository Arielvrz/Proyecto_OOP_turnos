package com.queuemanagementsystem.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

/**
 * Clase de utilidad para operaciones de fecha y hora.
 */
public class DateTimeUtil {

    /**
     * Obtiene el inicio del día actual (medianoche)
     *
     * @param dateTime La fecha y hora de referencia
     * @return LocalDateTime que representa el inicio del día (00:00:00)
     */
    public LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    /**
     * Obtiene el final del día actual (23:59:59.999999999)
     *
     * @param dateTime La fecha y hora de referencia
     * @return LocalDateTime que representa el final del día
     */
    public LocalDateTime getEndOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * Obtiene el inicio de la semana actual (Lunes a medianoche)
     *
     * @param dateTime La fecha y hora de referencia
     * @return LocalDateTime que representa el inicio de la semana
     */
    public LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay();
    }

    /**
     * Obtiene el final de la semana actual (Domingo a las 23:59:59.999999999)
     *
     * @param dateTime La fecha y hora de referencia
     * @return LocalDateTime que representa el final de la semana
     */
    public LocalDateTime getEndOfWeek(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * Obtiene el inicio del mes actual (día 1 a medianoche)
     *
     * @param dateTime La fecha y hora de referencia
     * @return LocalDateTime que representa el inicio del mes
     */
    public LocalDateTime getStartOfMonth(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.firstDayOfMonth())
                .toLocalDate().atStartOfDay();
    }

    /**
     * Obtiene el final del mes actual (último día a las 23:59:59.999999999)
     *
     * @param dateTime La fecha y hora de referencia
     * @return LocalDateTime que representa el final del mes
     */
    public LocalDateTime getEndOfMonth(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.lastDayOfMonth())
                .toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * Formatea una duración en minutos a una cadena legible (ej. "2h 30m")
     *
     * @param minutes La duración en minutos
     * @return Una representación formateada en cadena
     */
    public String formatDuration(long minutes) {
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (hours > 0) {
            return hours + "h " + remainingMinutes + "m";
        } else {
            return remainingMinutes + "m";
        }
    }

    /**
     * Formatea un LocalDateTime a una cadena legible de fecha y hora
     *
     * @param dateTime El LocalDateTime a formatear
     * @return Una representación formateada en cadena
     */
    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }

        return dateTime.toLocalDate() + " " +
                String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
    }
}