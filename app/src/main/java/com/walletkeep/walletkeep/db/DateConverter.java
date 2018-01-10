package com.walletkeep.walletkeep.db;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    /**
     * Converts a timestamp from a long value to a date format
     * @param value Long representation of a timestamp
     * @return Date representation of a timestamp
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /**
     * Converts a timestamp from a date format to a long value
     * @param date Date representation of a timestamp
     * @return Long representation of a timestamp
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
