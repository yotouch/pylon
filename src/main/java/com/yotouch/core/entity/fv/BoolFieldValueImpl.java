package com.yotouch.core.entity.fv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yotouch.core.entity.MetaField;

public class BoolFieldValueImpl extends AbstractFieldValue<Boolean> implements FieldValue<Boolean> {
    
    static final private Logger logger = LoggerFactory.getLogger(BoolFieldValueImpl.class);

    public BoolFieldValueImpl(MetaField<Boolean> mf, Object value) {
        super(mf, value);
    }

    @Override
    protected Boolean parseValue(Object v) {
        
        logger.info("Set boolean field " + v);
        
        if (v == null) {
            return null;
        } else if (v instanceof Boolean) {
            return (Boolean) v;
        } else {
            String s = v.toString();
            return "true".equalsIgnoreCase(s) || "1".equalsIgnoreCase(s);
        }

    }

}
