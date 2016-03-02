package com.yotouch.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.fv.AbstractFieldValue;
import com.yotouch.core.entity.mf.MultiReferenceMetaFieldImpl;
import com.yotouch.core.entity.mf.ObjectMetaFieldImpl;
import com.yotouch.core.entity.mf.SingleReferenceMetaFieldImpl;
import com.yotouch.core.exception.MetaFieldIsNotSingleReference;
import com.yotouch.core.exception.NoSuchMetaFieldException;
import com.yotouch.core.exception.YotouchException;
import com.yotouch.core.runtime.DbSession;


public class EntityImpl implements Entity {
    
    

    static final private Logger logger = LoggerFactory.getLogger(EntityImpl.class);
    
    private MetaEntity me;
    
    private Map<String, FieldValue<?>> valueMap;
    
    public EntityImpl(MetaEntity me) {
        this.me = me;
        this.valueMap = new HashMap<>();
    }

    @Override
    public MetaEntity getMetaEntity() {
        return this.me;
    }

    @Override
    public String getUuid() {
        return this.getValue("uuid");
    }

    @Override
    public <T> void setValue(String fieldName, Object value) {
        MetaField<?> mf = this.me.getMetaField(fieldName);
        
        if (mf == null) {
            mf = new ObjectMetaFieldImpl(this.me, fieldName, fieldName);
        }
        
        FieldValue<?> fv = (AbstractFieldValue<?>) this.valueMap.get(mf.getName());
        
        if (mf.isMultiReference()) {
            logger.info("Set value for mr " + mf.getName());
        }
        
        if (fv == null) {
            fv = mf.newFieldValue(value);
        } else {
            fv.setNewValue(value);
        }
        
        this.valueMap.put(mf.getName(), fv);
    }

    @Override
    public <T> T getValue(String fieldName) throws NoSuchMetaFieldException {
        
        MetaField<?> mf = this.me.getMetaField(fieldName);
        if (mf == null) {
            throw new NoSuchMetaFieldException(this.me, fieldName);
        }
        
        @SuppressWarnings("unchecked")
        FieldValue<T> fv = (FieldValue<T>) this.valueMap.get(fieldName);
        if (fv != null) {
            return fv.getValue();
        }
        
        return null;
    }
    
    @Override
    public <T> T v(String fieldName) throws NoSuchMetaFieldException {
        return this.getValue(fieldName);
    }

    
    public List<FieldValue<?>> getFieldValueList() {
        return new ArrayList<>(this.valueMap.values());
    }

    @Override
    public int hashCode() {
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher()
                .putString(this.me.getName(), Charsets.UTF_8)
                .putString(getUuid() == null ? "" : getUuid(), Charsets.UTF_8)
                .hash();
        return hc.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        
        EntityImpl other = (EntityImpl) obj;
        if (me == null || other.me == null) {
            return false;
        } else {
            
            if (Objects.equal(me.getName(), other.me.getName())) {
                return Objects.equal(this.getUuid(), other.getUuid());
            } else {
                return false;
            }            
        }
    }

    @Override
    public boolean isFieldChanged(String fieldName) {
        FieldValue<?> fv = this.valueMap.get(fieldName);
        if (fv == null) {
            return false;
        } else {
            return fv.isChanged();
        }
    }

    @Override
    public <T> T getOldValue(String fieldName) {
        
        if (!this.isFieldChanged(fieldName)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        FieldValue<T> fv = (FieldValue<T>) this.valueMap.get(fieldName);
        if (fv == null) {
            return null;
        } else {
            return fv.getOldValue();
        }
    }

    @Override
    public Map<String, Object> valueMap() {
        Map<String, Object> m = new HashMap<>();
        for (MetaField<?> mf: this.me.getMetaFields()) {
            String name = mf.getName();
            
            m.put(name, this.getValue(name));
        }
        
        return m;
    }
    
    @Override
    public List<Entity> mr(DbSession dbSession, String fieldName) {
        return this.getMultiReference(dbSession, fieldName);
    }

    @Override
    public List<Entity> getMultiReference(DbSession dbSession, String fieldName) {
        MetaField<?> mf = this.me.getMetaField(fieldName);
        if (!mf.isMultiReference()) {
            throw new YotouchException(fieldName + " is not mr field");
        }
        
        MultiReferenceMetaFieldImpl mrf = (MultiReferenceMetaFieldImpl) mf;
        
        List<String> uuids = this.getValue(fieldName);
        logger.info("Get multi reference " + fieldName + " uuids " + uuids);
        List<Entity> entities = new ArrayList<>();
        
        for (String id: uuids) {
            Entity e = dbSession.getEntity(mrf.getTargetMetaEntity().getName(), id);
            entities.add(e);
        }
        
        logger.info("Get multi reference " + entities);
        
        return entities;
    }
    
    @Override
    public Entity sr(DbSession dbSession, String fieldName) {
        return this.getSingleReference(dbSession, fieldName);
    }

    @Override
    public Entity getSingleReference(DbSession dbSession, String fieldName) {
        
        MetaField<?> mf = this.me.getMetaField(fieldName);
        if (mf == null) {
            throw new NoSuchMetaFieldException(me, fieldName);
        }
        
        if (!mf.isSingleReference()) {
            throw new MetaFieldIsNotSingleReference(me, mf);
        }
        
        SingleReferenceMetaFieldImpl srf = (SingleReferenceMetaFieldImpl) mf;
        
        MetaEntity targetMe = srf.getTargetMetaEntity();
        String uuid = this.getValue(fieldName);
        if (StringUtils.isEmpty(uuid)) {
            return null;
        }
        
        return dbSession.getEntity(targetMe, uuid);
    }

    @Override
    public boolean isNew() {
        return StringUtils.isEmpty(this.getUuid());
    }

    @Override
    public String toString() {
        return "EntityImpl [me=" + me.getName() + ", uuid=" + this.getUuid()+ "]";
    }


}
