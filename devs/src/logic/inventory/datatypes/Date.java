package logic.inventory.datatypes;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

/* Date Data type */
public class Date {

    private int day;
    private int month;
    private int year;
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String toString()
    {
        return String.format("%d-%s-%s", year, (month / 10 == 0) ? "0"+month : month+"", (day / 10 == 0) ? "0"+day : day+"");
    }

    public static Date getCurrentDate()
    {
        LocalDateTime now = LocalDateTime.now();
        return parseDate(formatter.format(now));
    }

    public static Date javaDateToDate(java.util.Date d)
    {
        LocalDateTime datetime = javaDateToLocalDateTime(d);
        return parseDate((formatter.format(datetime)));
    }

    public static LocalDateTime javaDateToLocalDateTime(java.util.Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static String getCurrentDay()
    {
        LocalDateTime now = LocalDateTime.now();
        String day = now.getDayOfWeek().toString();
        return day.charAt(0)+day.substring(1).toLowerCase();
    }

    public static int getDayValue(String day)
    {
        switch (day){
            case "Sunday":
                return 1;
            case "Monday":
                return 2;
            case "Tuesday":
                return 3;
            case "Wednesday":
                return 4;
            case "Thursday":
                return 5;
            case "Friday":
                return 6;
            case "Saturday":
                return 7;
            default:
                return 0;
        }
    }

    public static String getValueDay(int val)
    {
        switch (val){
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return null;
        }
    }

    public static boolean isOneDayDiffrence(String day1, String day2)
    {
        int diff = getDayValue(day1) - getDayValue(day2);
        return diff == 6 || diff == 1;
    }

    public static Date parseDate(String date)
    {
        if(date == null)
            return null;
        int d, y, m;
        String[] split_date = date.split(" ");
        StringTokenizer tokenizer = new StringTokenizer(split_date[0], "-");
        try {
            y = Integer.parseInt(tokenizer.nextToken());
            m = Integer.parseInt(tokenizer.nextToken());
            d = Integer.parseInt(tokenizer.nextToken());
        } catch (Exception e) {
            return null;
        }
        return new Date(d, m, y);
    }

    public static boolean checkLegalDate(Date date)
    {
        return (date.getDay() >= 1 && date.getDay() <=31) &&
                (date.getMonth() >= 1 && date.getMonth() <=12) &&
                date.getYear() >= Year.now().getValue();
    }

    public static boolean compareDateToToday (Date dd)
    {
        Date today = Date.getCurrentDate();
        return dd.getMonth() < today.getMonth() || (dd.getMonth() == today.getMonth() && dd.getDay() <= today.getDay());
    }

    public static Date getNextYear(Date date)
    {
        return new Date(date.getDay(), date.getMonth(), date.getYear()+1);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
