package com.yotouch.core.entity.mf;

import java.util.Calendar;
import java.util.Date;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.MetaFieldImpl;
import com.yotouch.core.entity.fv.DateTimeFieldValueImpl;
import com.yotouch.core.entity.fv.FieldValue;

public class DateTimeMetaFieldImpl extends MetaFieldImpl<Calendar> {
    
    private boolean defaultNow = false;

    @Override
    public String getDataType() {
        return Consts.META_FIELD_DATA_TYPE_DATETIME;
    }

    @Override
    protected void setDefaultValue(Object dv) {
        boolean processed = false;
        if (dv instanceof String) {
            String s = (String) dv;
            if (Consts.FIELD_VARIABLE_NOW.equalsIgnoreCase(s)) {
                this.defaultNow = true;
                this.defaultValue = null;
                processed = true;
            }
        }
        
        if (!processed) {
            this.defaultValue = this.newFieldValue(dv);
        }
    }
    
    @Override
    public Calendar getDefaultValue() {
        if (this.defaultNow) {
            return Calendar.getInstance();
        } else {
            return this.defaultValue.getValue();
        }
    }

    @Override
    public FieldValue<Calendar> newFieldValue(Object value) {
        return new DateTimeFieldValueImpl(this, value);
    }

}
