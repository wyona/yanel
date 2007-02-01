package org.wyona.yanel.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class provides some static methods for date conversion, in order
 * to ensure a consistent date format (ISO-8601) throughout yanel. 
 */
public class DateUtil {
    protected static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    
    public static synchronized String format(Date date) {
        return dateFormat.format(date);
    }
    
    public static synchronized Date parse(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }

}