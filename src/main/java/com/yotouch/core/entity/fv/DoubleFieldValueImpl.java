package com.yotouch.core.entity.fv;

import com.yotouch.core.entity.MetaField;
import org.springframework.util.StringUtils;

public class DoubleFieldValueImpl extends AbstractFieldValue<Double> implements FieldValue<Double> {

    public DoubleFieldValueImpl(MetaField<Double> mf, Object value) {
        super(mf, value);
    }

    @Override
    protected Double parseValue(Object v) {
        if (StringUtils.isEmpty(v)) {
            return null;
        } else if (
                v instanceof Double
                || v instanceof Float
                || v instanceof Integer
                || v instanceof Long
                ) {
            return Double.parseDouble(v.toString());
        } else {
            return Double.parseDouble(v.toString());
        }

    }

}
