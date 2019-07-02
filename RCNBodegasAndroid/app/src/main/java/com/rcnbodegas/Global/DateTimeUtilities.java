package com.rcnbodegas.Global;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtilities {

    private final GlobalClass globalVariable;
    private Context _Context;
    private Calendar calendar;
    private int year, month, day;

    public DateTimeUtilities(Context _context) {
        _Context = _context;
        globalVariable = (GlobalClass) _Context.getApplicationContext();
    }

    public String parseDateTurno() {

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date result = cal.getTime();
        String dateTime = dateFormat.format(result);

        return dateTime;
    }
    public String parseDateTurno(int year, int month, int day) {

        calendar = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date result = cal.getTime();
        String dateTime = dateFormat.format(result);

        return dateTime;
    }
}
