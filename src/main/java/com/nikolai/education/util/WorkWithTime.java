package com.nikolai.education.util;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Component
public class WorkWithTime {

    public String dateNow() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(calendar.getTime());
    }

    public String dateFinish(Integer expirationTime, Integer time) {
        String finishDate;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        calendar.add(time, expirationTime);
        finishDate = formatter.format(calendar.getTime());

        return finishDate;
    }
}
