package com.danielsharp01.taskstopwatch;

import org.threeten.bp.Duration;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

public class TimeUtils {
    public static String durationAsLongString(Duration duration)
    {
        int hours = (int)(duration.getSeconds() / 3600);
        int minutes = (int)((duration.getSeconds() / 60) % 60);
        int seconds = (int)(duration.getSeconds() % 60);

        return "" + (hours > 0 ? doubleDigitConditionally(hours, false) + ":" : "")
                + doubleDigitConditionally(minutes, hours > 0) + ":" + doubleDigitConditionally(seconds, true);
    }

    public static String timeAsString(LocalDateTime time)
    {
        return "" + doubleDigitConditionally(time.getHour(), false)
                + ":" + doubleDigitConditionally(time.getMinute(), true)
                + ":" + doubleDigitConditionally(time.getSecond(), true);
    }

    public static String doubleDigitConditionally(int number, boolean condition)
    {
        if (!condition) return "" + number;

        if (number < 10) {
            return "0" + number;
        }
        else {
            return "" + number;
        }
    }
}
