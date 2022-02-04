package com.monterdev.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class AppTime {

    public static LocalDateTime now(){
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime;
    }

    public static int getYear(){
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static String getMonth(){
        Calendar calendar = Calendar.getInstance();
        return new SimpleDateFormat("MMM").format(calendar.getTime());
    }
}
