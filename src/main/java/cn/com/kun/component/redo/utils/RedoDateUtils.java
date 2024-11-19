package cn.com.kun.component.redo.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class RedoDateUtils {

    public static Date addDays(Date currentTime, long number) {

        LocalDate localDate = currentTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate newDate = localDate.plus(number, ChronoUnit.DAYS);
        return Date.from(newDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date addDays(Date currentTime, int number) {

        Calendar c = Calendar.getInstance();
        c.setTime(currentTime);
        c.add(Calendar.DAY_OF_MONTH, number);
        return c.getTime();
    }

}
