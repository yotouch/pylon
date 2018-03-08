package com.yotouch.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);

    private static final SimpleDateFormat[] getDateTimeFormatList() {
        return new SimpleDateFormat[]{
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                new SimpleDateFormat("yyyy-MM-dd HH:mm"),
                new SimpleDateFormat("yyyyMMddHHmmss"),
                new SimpleDateFormat("yyyy-MM-dd"),
        };
    }

    private static final SimpleDateFormat getSdfDate() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    private static final SimpleDateFormat getSdfDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static final Date parseString(String dt) {
        for (SimpleDateFormat sdf : getDateTimeFormatList()) {
            try {
                return sdf.parse(dt);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return null;
    }

    public static final String formatDate(Date d) {
        return getSdfDate().format(d);
    }

    public static final String formatDate(Calendar cal) {
        return formatDate(cal.getTime());
    }

    public static final String formatDateTime(Date d) {
        return getSdfDateTime().format(d);
    }

    public static final String formatDateTime(Calendar cal) {
        return formatDateTime(cal.getTime());
    }


}
