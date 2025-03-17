package com.queuemanagementsystem.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

/**
 * Utility class for date and time operations.
 */
public class DateTimeUtil {

    /**
     * Gets the start of the current day (midnight)
     *
     * @param dateTime The reference date and time
     * @return LocalDateTime representing the start of the day (00:00:00)
     */
    public LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    /**
     * Gets the end of the current day (23:59:59.999999999)
     *
     * @param dateTime The reference date and time
     * @return LocalDateTime representing the end of the day
     */
    public LocalDateTime getEndOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * Gets the start of the current week (Monday at midnight)
     *
     * @param dateTime The reference date and time
     * @return LocalDateTime representing the start of the week
     */
    public LocalDateTime getStartOfWeek(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toLocalDate().atStartOfDay();
    }

    /**
     * Gets the end of the current week (Sunday at 23:59:59.999999999)
     *
     * @param dateTime The reference date and time
     * @return LocalDateTime representing the end of the week
     */
    public LocalDateTime getEndOfWeek(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * Gets the start of the current month (1st day at midnight)
     *
     * @param dateTime The reference date and time
     * @return LocalDateTime representing the start of the month
     */
    public LocalDateTime getStartOfMonth(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.firstDayOfMonth())
                .toLocalDate().atStartOfDay();
    }

    /**
     * Gets the end of the current month (last day at 23:59:59.999999999)
     *
     * @param dateTime The reference date and time
     * @return LocalDateTime representing the end of the month
     */
    public LocalDateTime getEndOfMonth(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.lastDayOfMonth())
                .toLocalDate().atTime(LocalTime.MAX);
    }

    /**
     * Formats a duration in minutes to a readable string (e.g., "2h 30m")
     *
     * @param minutes The duration in minutes
     * @return A formatted string representation
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
     * Formats a LocalDateTime to a readable date and time string
     *
     * @param dateTime The LocalDateTime to format
     * @return A formatted string representation
     */
    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }

        return dateTime.toLocalDate() + " " +
                String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
    }
}