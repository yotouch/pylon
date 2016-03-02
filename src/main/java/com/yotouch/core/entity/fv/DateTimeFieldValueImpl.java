package com.yotouch.core.entity.fv;

import java.util.Date;

import org.springframework.util.StringUtils;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaField;
import com.yotouch.core.util.DateTimeUtil;

public class DateTimeFieldValueImpl extends AbstractFieldValue<Date> implements FieldValue<Date> {

    public DateTimeFieldValueImpl(MetaField<Date> mf, Object value) {
        super(mf, value);
    }

    @Override
    protected Date parseValue(Object v) {
        if (StringUtils.isEmpty(v)) {
            return null;
        } else if (v instanceof Date){
            return (Date) v;
        } else {
            String s = (String) v.toString();
            if (Consts.FIELD_VARIABLE_NOW.equalsIgnoreCase(s)) {
                return null;
            } else {
                return DateTimeUtil.parseString(s);
            }
        }

    }

}
