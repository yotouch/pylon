package com.yotouch.core.entity.fv;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;
import com.yotouch.core.entity.MetaField;

public class BinaryFieldValueImpl extends AbstractFieldValue<byte[]> implements FieldValue<byte[]> {

    public BinaryFieldValueImpl(MetaField<byte[]> mf, Object value) {
        super(mf, value);
    }

    @Override
    protected byte[] parseValue(Object v) {
        if (v == null) {
            return new byte[0];
        } else if (v instanceof byte[]) {
            return (byte[]) v;
        } else if (v instanceof InputStream) {
            try {
                return ByteStreams.toByteArray((InputStream)v);
            } catch (IOException e) {
                return new byte[0];
            }
        } else {
            return new byte[0];
        }
    }

}
