package Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final String pattern = "yyyy-MM-dd";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public static String dateToString(Date date) {
        return simpleDateFormat.format(date);
    }

    public static Date strintToDate(String dateString) {
        try{
            return simpleDateFormat.parse(dateString);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }
    }
}
