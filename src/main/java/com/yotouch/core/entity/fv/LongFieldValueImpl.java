package com.yotouch.core.entity.fv;

import com.yotouch.core.entity.MetaField;
import org.springframework.util.StringUtils;

public class LongFieldValueImpl extends AbstractFieldValue<Long> implements FieldValue<Long> {

    public LongFieldValueImpl(MetaField<Long> mf, Object value) {
        super(mf, value);
    }

    @Override
    protected Long parseValue(Object v) {
        if (StringUtils.isEmpty(v)) {
            return null;
        } else if (v instanceof Integer) {
            long value = ((Integer) v).longValue();
            if (value == 0) {
                this.setChanged(true); // // TODO: 16/8/11  Hack for zero value 
            }
            return (Long) v;
        } if (v instanceof Long) {
            long value = (long) v;
            if (value == 0) {
                this.setChanged(true); // // TODO: 16/8/11  Hack for zero value 
            }
            return (Long) v;
        } else if (v instanceof Double) {
            return (Long) Math.round((double) v);
        } else if (v instanceof Float) {
            return Long.valueOf(Math.round((float) v));
        } else {
            return Long.parseLong(v.toString());
        }
    }

}
