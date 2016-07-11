package com.yotouch.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    
    //new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss"),
    static final SimpleDateFormat[] DATETIME_FORMAT_LIST = new SimpleDateFormat[] {
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
            new SimpleDateFormat("yyyyMMddHHmmss"),
    };

    static final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    
    
    public static final Date parseString(String dt) {
        for (SimpleDateFormat sdf: DATETIME_FORMAT_LIST) {
            try {
                return sdf.parse(dt);
            } catch (Exception e) {
            }
        }
        
        return null;
    }

    public static final String formatDate(Date d) {
        return sdfDate.format(d);
    }



}
