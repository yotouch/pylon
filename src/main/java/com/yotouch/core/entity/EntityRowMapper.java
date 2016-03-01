package com.yotouch.core.entity;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public EntityRowMapper(DbSession dbSession, MetaEntity me) {
        this.dbSession = dbSession;
        this.me = me;
    }

    @Override
    public Entity mapRow(ResultSet rs, int rowNum) throws SQLException {

        Entity e = new EntityImpl(me);
        List<MetaField<?>> fields = me.getMetaFields();

        for (MetaField<?> mf : fields) {

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
        
        
        for (MetaField<?> mf : fields) {
            if (!mf.isMultiReference()) {
                continue;
            }
            
            MultiReferenceMetaFieldImpl mrf = (MultiReferenceMetaFieldImpl) mf;
            
            List<Entity> entities = dbSession.queryRawSql(mrf.getMappingMetaEntity().getName(), me.getName() + "Uuid = ?", new Object[]{e.getUuid()});
            
            logger.info(" multi uuids " + entities.stream().map(ee -> ee.getValue(mrf.getTargetMetaEntity().getName() + "Uuid")));
            
            List<String> uuids = new ArrayList<>();
            entities.stream().forEach(ee -> uuids.add(ee.getValue(mrf.getTargetMetaEntity().getName() + "Uuid")));
            
            logger.info(" multi uuids " + uuids);
            
            e.setValue(mf.getName(), uuids);
            
        }

        return e;
    }

    

}
