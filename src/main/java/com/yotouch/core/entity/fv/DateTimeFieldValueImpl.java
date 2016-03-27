package com.yotouch.core.entity.fv;

import java.util.Calendar;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaField;
import com.yotouch.core.util.DateTimeUtil;

public class DateTimeFieldValueImpl extends AbstractFieldValue<Calendar> implements FieldValue<Calendar> {

    public DateTimeFieldValueImpl(MetaField<Calendar> mf, Object value) {
        super(mf, value);
    }

    @Override
    protected Calendar parseValue(Object v) {
        if (StringUtils.isEmpty(v)) {
            return null;
        } else if (v instanceof Calendar) {
            return (Calendar) v;
        } else if (v instanceof Date){
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) v);
            return cal;
        } else {
            String s = (String) v.toString();
            if (Consts.FIELD_VARIABLE_NOW.equalsIgnoreCase(s)) {
                return null;
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(DateTimeUtil.parseString(s));
                return cal;
            }
        }

    }

}
