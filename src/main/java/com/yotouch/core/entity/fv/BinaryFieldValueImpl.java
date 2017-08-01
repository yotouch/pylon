package com.yotouch.core.entity.fv;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

import com.yotouch.core.entity.MetaField;
import org.springframework.util.StringUtils;

public class BinaryFieldValueImpl extends AbstractFieldValue<byte[]> implements FieldValue<byte[]> {

    private static final Logger logger = LoggerFactory.getLogger(BinaryFieldValueImpl.class);

    public BinaryFieldValueImpl(MetaField<byte[]> mf, Object value) {
        super(mf, value);
    }

    @Override
    protected byte[] parseValue(Object v) {
        if (StringUtils.isEmpty(v)) {
            return new byte[0];
        } else if (v instanceof byte[]) {
            return (byte[]) v;
        } else if (v instanceof InputStream) {
            try {
                return ByteStreams.toByteArray((InputStream)v);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                return new byte[0];
            }
        } else {
            return new byte[0];
        }
    }

}
