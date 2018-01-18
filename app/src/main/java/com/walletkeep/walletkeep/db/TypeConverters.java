package com.walletkeep.walletkeep.db;

import android.arch.persistence.room.TypeConverter;

import java.math.BigDecimal;
import java.util.Date;

public class TypeConverters {
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

    @TypeConverter
    public BigDecimal fromString(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    @TypeConverter
    public String toString(BigDecimal bigDecimal) {
        return bigDecimal == null ? null : bigDecimal.toString();
    }
}
