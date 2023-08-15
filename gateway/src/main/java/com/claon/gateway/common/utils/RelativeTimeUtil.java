package com.claon.gateway.common.utils;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class RelativeTimeUtil {
    // const timezone for absolute time string fallback output
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
    
    // datetime to compare with. usually current datetime (now)
    private final ZonedDateTime baseZonedDatetime;

    /**
     * Create a new relative time string converter with current datetime.
     */
    public RelativeTimeUtil() {
        this(null);
    }
    
    /**
     * Create a new relative time string converter with given base datetime.
     *
     * @param  baseDatetime   Base UTC datetime to compare with,
     *                        when converting a datetime with this object.
     *                        If null, current datetime is used by default.
     */
    public RelativeTimeUtil(OffsetDateTime baseDatetime) {
        if (baseDatetime == null) {
            this.baseZonedDatetime = ZonedDateTime.now(ZONE_ID);
        } else {
            this.baseZonedDatetime = baseDatetime.atZoneSameInstant(ZONE_ID);
        }
    }

    /**
     * Get a relative time string from base datetime of the object.
     *
     * @param  inputDatetime  UTC datetime to convert
     * @return                Relative time string in Korean
     */
    public String convert(OffsetDateTime inputDatetime) {
        return convertZoned(
                inputDatetime.atZoneSameInstant(ZONE_ID),
                this.baseZonedDatetime
        );
    }
    
    /**
     * Get a relative time string from current datetime,
     * without generating any class instance.
     *
     * @param  inputDatetime  UTC datetime to convert
     * @return                Relative time string in Korean
     */
    public static String convertNow(OffsetDateTime inputDatetime) {
        return convertZoned(
                inputDatetime.atZoneSameInstant(ZONE_ID),
                null
        );
    }
    
    /**
     * Get a relative time string from given base datetime
     * without generating any class instance.
     * 
     * If 'inputDatetime' is later than 'baseDatetime',
     * calculated time difference is assumed to be '0'.
     *
     * @param  inputDatetime  UTC datetime to convert
     * @param  baseDatetime   Base UTC datetime to compare with.
     *                        If null, current datetime is used by default.
     * @return                Relative time string in Korean
     */
    public static String convertNow(
            OffsetDateTime inputDatetime,
            OffsetDateTime baseDatetime
    ) {
        return convertZoned(
                inputDatetime.atZoneSameInstant(ZONE_ID),
                baseDatetime.atZoneSameInstant(ZONE_ID)
        );
    }

    // convert datetime -> relative time string,
    // in the form of ZONE_ID timezone datetime
    private static String convertZoned(
            ZonedDateTime inputZonedDatetime,
            ZonedDateTime baseZonedDatetime
    ) {
        final long MIN_IN_SECS = 60;
        final long HOUR_IN_SECS = MIN_IN_SECS * 60;
        final long DAY_IN_SECS = HOUR_IN_SECS * 24;
        final long WEEK_IN_SECS = DAY_IN_SECS * 7;
        final long MONTH_IN_SECS = WEEK_IN_SECS * 4;

        // baseDatetime default: current datetime
        if (baseZonedDatetime == null) {
            baseZonedDatetime = ZonedDateTime.now(ZONE_ID);
        }
        
        // get a time difference (in seconds)
        long secDiff = ChronoUnit.SECONDS.between(inputZonedDatetime, baseZonedDatetime);

        if (secDiff < 0) {
            secDiff = 0;
        }
        
        // convert to relative time string
        if (secDiff == 0) {
            return "방금 전";
        } else if (secDiff < MIN_IN_SECS) {
            return secDiff + "초 전";
        } else if (secDiff < HOUR_IN_SECS) {
            return secDiff / MIN_IN_SECS + "분 전";
        } else if (secDiff < DAY_IN_SECS) {
            return secDiff / HOUR_IN_SECS + "시간 전";
        } else if (secDiff < WEEK_IN_SECS) {
            return secDiff / DAY_IN_SECS + "일 전";
        } else if (secDiff < MONTH_IN_SECS) {
            return secDiff / WEEK_IN_SECS + "주 전";
        } else {
            // absolute time string fallbacks
            if (inputZonedDatetime.getYear() == baseZonedDatetime.getYear()) {
                // current year
                return inputZonedDatetime.getMonthValue() + "월 "
                    + inputZonedDatetime.getDayOfMonth() + "일";
            } else {
                // other year
                return inputZonedDatetime.getYear() + "년 "
                    + inputZonedDatetime.getMonthValue() + "월 "
                    + inputZonedDatetime.getDayOfMonth() + "일";
            }
        }
    }
}
