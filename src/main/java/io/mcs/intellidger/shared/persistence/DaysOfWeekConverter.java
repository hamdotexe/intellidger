package io.mcs.intellidger.shared.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Days of the week kept in one column, as their names in week order: "MONDAY,THURSDAY". A preference that applies on
 * some days keeps them this way rather than in a table of its own.
 */
@Converter
public class DaysOfWeekConverter implements AttributeConverter<Set<DayOfWeek>, String> {

    @Override
    public String convertToDatabaseColumn(Set<DayOfWeek> days) {
        if (days == null) {
            return null;
        }
        return days.stream().sorted().map(day -> day.name()).collect(Collectors.joining(","));
    }

    @Override
    public Set<DayOfWeek> convertToEntityAttribute(String column) {
        Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        if (column == null) {
            return days;
        }
        for (String name : column.split(",")) {
            if (!name.isBlank()) {
                days.add(DayOfWeek.valueOf(name));
            }
        }
        return days;
    }
}
