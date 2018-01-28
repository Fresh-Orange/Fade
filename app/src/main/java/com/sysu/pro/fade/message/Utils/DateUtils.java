package com.sysu.pro.fade.message.Utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static String changeToDate(String day) {
        Log.d("day", "day: " + day);
        String year = day.substring(0, 5);
        String monthAndDay = day.substring(5, 11);
        Log.d("day", "year: " + year);
        Log.d("day", "monthAndDay: " + monthAndDay);
        String result = null;
        String time = day.substring(11, 16);

        Log.d("day", "time: " + time);

        try {
            if (IsToday(day)) {
                //今天
                year = "";
                monthAndDay = "今天 ";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            if (IsYesterday(day)) {
                //昨天
                year = "";
                monthAndDay = "昨天 ";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            if (IsSameYear(day)) {
                //同一年
                year = "";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        result = year + monthAndDay + time;
        return result;
    }
    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(String day) throws ParseException {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为昨天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsYesterday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    public static boolean IsSameYear(String day) throws ParseException {
        Date date = getDateFormat().parse(day);
        int datecompareAfter = compareDate(new Date(), date);
        int daecompareBefore = compareDate(date, getOneYear());

        if (datecompareAfter == -1 && daecompareBefore == -1) {
            //如果不是在一年以内,则弹出提示
            return false;
        } else {
            //在一年以内做的逻辑
            return true;
        }

    }

    // 比较时间
    public static int compareDate(Date d1, Date d2) {
        if (d1.getTime() > d2.getTime()) {
            return 1;
        } else if (d1.getTime() < d2.getTime()) {
            return -1;
        } else {// 相等
            return 0;
        }
    }

    //當前時間加1年
    public static Date getOneYear() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, 1);
        return c.getTime();
    }

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();
}
