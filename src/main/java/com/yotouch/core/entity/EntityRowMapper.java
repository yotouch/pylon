package com.yotouch.core.entity;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yotouch.core.entity.query.ff.FunctionField;
import com.yotouch.core.entity.query.QueryField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import com.google.common.io.ByteStreams;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.mf.MultiReferenceMetaFieldImpl;
import com.yotouch.core.runtime.DbSession;

public class EntityRowMapper implements RowMapper<Entity> {
    
    static final Logger logger = LoggerFactory.getLogger(EntityRowMapper.class);

    private MetaEntity me;
    
    private DbSession dbSession;

    private List<QueryField> fields;

    public EntityRowMapper(DbSession dbSession, MetaEntity me) {
        this.dbSession = dbSession;
        this.me = me;
    }

    @Override
    public Entity mapRow(ResultSet rs, int rowNum) throws SQLException {

        if (this.fields == null || this.fields.isEmpty()) {
            this.fields = new ArrayList<>();

            for (MetaField<?> mf : me.getMetaFields()) {
                this.fields.add(mf);
            }
        }

        Entity e = new EntityImpl(me);
        //List<MetaField<?>> fields = me.getMetaFields();

        int idx = 0;
        for (QueryField qf : this.fields) {

            idx += 1;

            if (qf instanceof MetaField) {
                checkMetaField(rs, (MetaField<?>) qf, e);
            } else if (qf instanceof FunctionField) {

                if (Consts.META_FIELD_DATA_TYPE_INT.equalsIgnoreCase(qf.getDataType())) {
                    e.setValue(qf.getName(), rs.getInt(idx));
                } else if (Consts.META_FIELD_DATA_TYPE_DOUBLE.equalsIgnoreCase(qf.getDataType())) {
                    e.setValue(qf.getName(), rs.getDouble(idx));
                }

            }

        }


        for (QueryField qf : this.fields) {
            if (!(qf instanceof MetaField)) {
                continue;
            }

            MetaField<?> mf = (MetaField<?>) qf;

            if (!mf.isMultiReference()) {
                continue;
            }

            MultiReferenceMetaFieldImpl mrf = (MultiReferenceMetaFieldImpl) mf;

            List<Entity> entities = dbSession.queryRawSql(mrf.getMappingMetaEntity().getName(), "s_" + me.getName() + "Uuid = ?", new Object[]{e.getUuid()});

            logger.debug(" multi uuids " + entities.stream().map(ee -> ee.getValue("t_" + mrf.getTargetMetaEntity().getName() + "Uuid")));

            List<String> uuids = new ArrayList<>();
            entities.stream().forEach(ee -> uuids.add(ee.getValue("t_" + mrf.getTargetMetaEntity().getName() + "Uuid")));

            logger.debug(" multi uuids " + uuids);

            e.setValue(mf.getName(), uuids);

        }

        return e;


    }

    private void checkMetaField(ResultSet rs, MetaField<?> mf, Entity e) throws SQLException {
        String fname = mf.getName();

        logger.debug("Get value from rs " + mf);

        if (Consts.META_FIELD_TYPE_SINGLE_REFERENCE.equalsIgnoreCase(mf.getFieldType())) {
            String uuid = rs.getString(fname + "Uuid");
            e.setValue(fname, uuid);
        } else {
            if (Consts.META_FIELD_DATA_TYPE_STRING.equalsIgnoreCase(mf.getDataType())
                    || Consts.META_FIELD_DATA_TYPE_UUID.equalsIgnoreCase(mf.getDataType())
                    || Consts.META_FIELD_DATA_TYPE_TEXT.equalsIgnoreCase(mf.getDataType())
                    ) {
                e.setValue(fname, rs.getString(fname));
            } else if (Consts.META_FIELD_DATA_TYPE_DATETIME.equals(mf.getDataType())) {
                Date d = rs.getTimestamp(fname);
                e.setValue(fname, d);
            } else if (Consts.META_FIELD_DATA_TYPE_INT.equals(mf.getDataType())) {
                e.setValue(fname, rs.getInt(fname));
            } else if (Consts.META_FIELD_DATA_TYPE_DOUBLE.equals(mf.getDataType())) {
                e.setValue(fname, rs.getDouble(fname));
            } else if (Consts.META_FIELD_DATA_TYPE_BINARY.equals(mf.getDataType())) {

                Blob blob = rs.getBlob(fname);
                InputStream is = blob.getBinaryStream();
                try {
                    e.setValue(fname, ByteStreams.toByteArray(is));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if (Consts.META_FIELD_DATA_TYPE_BOOLEAN.equalsIgnoreCase(mf.getDataType())) {
                Object o = rs.getObject(fname);
                if (o == null) {
                    e.setValue(fname, null);
                } else {
                    e.setValue(fname, rs.getInt(fname));
                }
            }
        }
    }


    public void setFields(List<QueryField> fields) {
        this.fields = fields;
    }
}
