package com.brokersystems.brokerapp.server.utils;

import com.brokersystems.brokerapp.setup.model.PublicHolidays;
import com.brokersystems.brokerapp.setup.repository.PublicHolidaysRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@Component
public class HolidayUtils {
    private static PublicHolidaysRepo holidaysRepo;

    @Autowired
    public HolidayUtils(PublicHolidaysRepo holidaysRepo) {
        HolidayUtils.holidaysRepo = holidaysRepo;
    }


    // Fixed holidays that occur on the same date every year
    private static final Set<MonthDay> FIXED_HOLIDAYS = new HashSet<>(Arrays.asList(
            MonthDay.of(1, 1),   // New Year's Day
            MonthDay.of(5, 1),   // Labor Day
            MonthDay.of(6, 1),   // Madaraka Day
            MonthDay.of(10, 10), // Utamaduni Day
            MonthDay.of(10, 20), // Mashujaa Day
            MonthDay.of(12, 25), // Christmas
            MonthDay.of(12, 26)  // Boxing Day
    ));

    // Map of variable holidays by year (if applicable)
    private static final Map<Integer, Set<LocalDate>> VARIABLE_HOLIDAYS = new HashMap<>();

    // Caches holidays for each year
    private static final ConcurrentHashMap<Integer, Set<LocalDate>> YEARLY_HOLIDAY_CACHE = new ConcurrentHashMap<>();

    /**
     * Gets all holidays for a specific year, including fixed, variable, Easter-based,
     * observed (if Sunday), and user-defined from DB.
     */
    public static Set<LocalDate> getHolidaysForYear(int year) {
        return YEARLY_HOLIDAY_CACHE.computeIfAbsent(year, y -> {
            Set<LocalDate> holidays = new HashSet<>();

            // 1. Add fixed holidays
            holidays.addAll(FIXED_HOLIDAYS.stream()
                    .map(md -> md.atYear(y))
                    .collect(Collectors.toSet()));

            // 2. Add variable holidays (if any for that year)
            if (VARIABLE_HOLIDAYS.containsKey(y)) {
                holidays.addAll(VARIABLE_HOLIDAYS.get(y));
            }

            // 3. Add Easter holidays
            LocalDate easterSunday = calculateEasterSunday(y);
            holidays.add(easterSunday.minusDays(2)); // Good Friday
            holidays.add(easterSunday.plusDays(1));  // Easter Monday

            // 4. Add observed Mondays if holiday falls on Sunday
            Set<LocalDate> observedMondays = holidays.stream()
                    .filter(date -> date.getDayOfWeek() == DayOfWeek.SUNDAY)
                    .map(date -> date.plusDays(1))
                    .collect(Collectors.toSet());
            holidays.addAll(observedMondays);

            // 5. Add user-defined holidays from DB
            holidays.addAll(fetchUserDefinedHolidaysFromDB(y));

            return Collections.unmodifiableSet(holidays);
        });
    }

    /**
     * Checks if a date is a weekend or a holiday.
     */
    public static boolean isHolidayOrWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) return true;

        Set<LocalDate> holidays = getHolidaysForYear(date.getYear());
        return holidays.contains(date);
    }

    /**
     * Returns the next working day after the given date.
     */
    public static LocalDate getPreviousWorkingDay(LocalDate date) {
        LocalDate previousDay = date.minusDays(1);
        while (isHolidayOrWeekend(previousDay)) {
            previousDay = previousDay.minusDays(1);
        }
        return previousDay;
    }
    public static LocalDate getNextWorkingDay(LocalDate date) {
        LocalDate nextDate = date.plusDays(1);
        while (isHolidayOrWeekend(nextDate)) {
            nextDate = nextDate.plusDays(1);
        }
        return nextDate;
    }

    /**
     * Adjusts a proposed transaction date to the next working day if needed.
     */
    public static LocalDate getAdjustedTransactionDate(LocalDate proposedDate) {
        if (!isHolidayOrWeekend(proposedDate)) {
            return proposedDate;
        }
        return getNextWorkingDay(proposedDate);
    }

    /**
     * Calculates Easter Sunday for a given year using an approximate algorithm.
     */
    private static LocalDate calculateEasterSunday(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(year, month, day);
    }
    private static Set<LocalDate> fetchUserDefinedHolidaysFromDB(int year) {
        List<PublicHolidays> holidays = holidaysRepo.findByActiveStatus("Y");

        return holidays.stream()
                .filter(holiday -> {
                    LocalDate localDate = convertToLocalDate(holiday.getHolidayDate());
                    if (localDate == null) return false;

                    if ("Y".equalsIgnoreCase(holiday.getIsRecurring())) {
                        return true; // include every year
                    }
                    return localDate.getYear() == year;
                })
                .map(holiday -> {
                    LocalDate originalDate = convertToLocalDate(holiday.getHolidayDate());
                    if (originalDate == null) return null;

                    if ("Y".equalsIgnoreCase(holiday.getIsRecurring())) {
                        // For recurring holidays, keep same month/day but use requested year
                        return LocalDate.of(year, originalDate.getMonth(), originalDate.getDayOfMonth());
                    } else {
                        // For non-recurring holidays, use exact date
                        return originalDate;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    // Helper method to safely convert various date types to LocalDate
    private static LocalDate convertToLocalDate(Object date) {
        if (date == null) return null;

        try {
            if (date instanceof java.sql.Date) {
                return ((java.sql.Date) date).toLocalDate();
            } else if (date instanceof java.util.Date) {
                return ((java.util.Date) date).toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            } else if (date instanceof LocalDate) {
                return (LocalDate) date;
            } else if (date instanceof String) {
                return LocalDate.parse((String) date);
            }
        } catch (Exception e) {
            // Log error if needed
            return null;
        }
        return null;
    }

    /**
     * Stub: Fetches user-defined holidays from the database for the given year.
     * Replace with actual repository/database access code.
     */
//    private static Set<LocalDate> fetchUserDefinedHolidaysFromDB(int year) {
//        List<PublicHolidays> holidays = holidaysRepo.findByActiveStatus("Y");
//
//        return holidays.stream()
//                .filter(holiday -> {
//                    if ("Y".equalsIgnoreCase(holiday.getIsRecurring())) {
//                        return true; // include every year
//                    }
//                    LocalDate localDate = ((java.sql.Date) holiday.getHolidayDate()).toLocalDate();
//                    return localDate.getYear() == year;
//                })
//                .map(holiday -> ((java.sql.Date) holiday.getHolidayDate()).toLocalDate())
//                .collect(Collectors.toSet());
//    }
    public static void invalidateCacheForYear(int year) {
        YEARLY_HOLIDAY_CACHE.remove(year);
    }
}