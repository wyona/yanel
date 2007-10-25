package org.wyona.yanel.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class provides some static methods for date conversion, in order
 * to ensure a consistent date format (ISO-8601) throughout yanel. 
 */
public class DateUtil {
    protected static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    protected static DateFormat rfc822DateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss zzz");
    
    public static synchronized String format(Date date) {
        return dateFormat.format(date);
    }
    
    public static synchronized Date parse(String dateString) throws ParseException {
        return dateFormat.parse(dateString);
    }
    
    public static synchronized String formatRFC822(Date date) {
        return rfc822DateFormat.format(date);
    }
    
    public static synchronized Date parseRFC822(String dateString) throws ParseException {
        return rfc822DateFormat.parse(dateString);
    }
    
    public static synchronized String formatRFC822GMT(Date date) {
        DateFormat format = (DateFormat)rfc822DateFormat.clone();
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(date);
    }

}