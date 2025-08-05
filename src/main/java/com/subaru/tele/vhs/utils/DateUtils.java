package com.subaru.tele.vhs.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * @author gauraaga
 */
public class DateUtils {
    public static long toEpochDay(Instant timestamp) {
        return LocalDate.ofInstant(timestamp, ZoneOffset.UTC).toEpochDay();
    }
}
