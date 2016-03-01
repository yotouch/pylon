package com.yotouch.core.entity.fv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.MetaField;
import com.yotouch.core.exception.YotouchException;

public class MultiReferenceFieldValue extends AbstractFieldValue<List<String>> implements FieldValue<List<String>> {

    public MultiReferenceFieldValue(MetaField<List<String>> mf, Object value) {
        super(mf, value);
    }
    
    @Override
    public List<String> getValue() {
        return new ArrayList<>(super.getValue());
    }

    @Override
    protected List<String> parseValue(Object v) {
        
        List<String> ret = new ArrayList<>();
        
        if (v == null) {
            return ret;
        } else if (v instanceof Collection<?>) {
            Collection<?> l = (Collection<?>) v;
            if (l.isEmpty()) {
                return ret;
            } 
            
            for (Object o: l) {
                if (o instanceof Entity) {
                    ret.add(((Entity) o).getUuid());
                } else if (o instanceof String) {
                    ret.add((String) o);
                } else {
                    throw new YotouchException("Wrong value for multi reference " + v);
                }
            }
        }
            
        
        return ret;
    }

}
