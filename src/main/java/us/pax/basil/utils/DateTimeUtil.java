package us.pax.basil.utils;

import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Log4j2
public class DateTimeUtil {

    public final static String PATTERN_YYYYMMDD_WITH_SLASH = "yyyy/MM/dd";
    public final static String PATTERN_YYYYMMDD_WITH_DASH = "yyyy-MM-dd";

    public static String[] getStartEnd(@NotNull String dateTimeRange, @NotNull String pattern, @NotNull String regex)
            throws DateTimeParseException {
        DateTimeFormatter dtf = getDateTimeFormatter(pattern);

        //
        // remove white spaces
        //
        String[] dates = dateTimeRange.replaceAll("\\s", "").split(regex);

        if (dates.length != 2) {
            log.warn("Invalid dateTimeRange String: {}", dateTimeRange);
            throw new DateTimeParseException("Invalid Date Range", dateTimeRange, 0);
        }

        dtf.parse(dates[0]);    // Start date time of the range
        dtf.parse(dates[1]);    // End date time of the rage
        if (dates[0].compareTo(dates[1]) > 0) {
            log.warn("Invalid dateTimeRange String: {}", dateTimeRange);
            throw new DateTimeParseException("Invalid Date Range", dateTimeRange, 0);
        }

        return dates;
    }

    public static DateTimeFormatter getDateTimeFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }
    
    public static void dateTimeToMap(Map<String, Object> map, String key, LocalDateTime dateTime, String dateTimeFormat) {
        DateTimeFormatter dtf = getDateTimeFormatter(dateTimeFormat);
    	if (dateTime == null)
    		map.put(key, null);
    	else
    		map.put(key, dtf.format(dateTime));
    }
}
