package com.yotouch.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yotouch.core.Consts;
import com.yotouch.core.model.EntityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import com.yotouch.core.entity.fv.FieldValue;
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
    
    private Map<String, Entity> srMap;
    
    public EntityImpl(MetaEntity me) {
        this.me = me;
        this.valueMap = new HashMap<>();

        me.getMetaFields().stream()
                .filter(mf -> mf.getDefaultFieldValue() != null && !StringUtils.isEmpty(mf.getDefaultFieldValue().getValue())) //由于StringFieldValue默认为"" 所以用isEmpty
                .forEach(mf -> this.valueMap.put(mf.getName(), mf.getDefaultFieldValue()));

        this.srMap = new HashMap<>();
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
    public <T> Entity setValue(String fieldName, Object value) {
        MetaField<?> mf = this.me.getMetaField(fieldName);
        
        if (mf == null) {
            logger.debug("Set none-existing field `"+fieldName+"` for metaEntity `"+this.me.getName()+"`");
            mf = new ObjectMetaFieldImpl(this.me, fieldName, fieldName);
        }
        
        FieldValue<?> fv = this.valueMap.get(mf.getName());

        if (fv == null) {
            fv = mf.newFieldValue(value);
        } else {
            fv.setNewValue(value);
        }
        
        this.valueMap.put(mf.getName(), fv);

        return this;
    }

    @Override
    public <T> T getValue(DbSession dbSession, String field) {
        String[] fieldParts = field.split("\\.");
        if (fieldParts.length == 1) {
            return this.v(field);
        }
        
        
        String refName = fieldParts[0];
        MetaField mf = this.getMetaEntity().getMetaField(refName);
        if (!mf.isSingleReference()) {
            throw new MetaFieldIsNotSingleReference(this.me, mf);
        }
        
        Entity refEntity = this.sr(dbSession, refName);
        if (refEntity == null) {
            return null;
        }
        
        field = field.substring(field.indexOf('.') + 1);
        return refEntity.v(dbSession, field);
        
    }
    
    @Override
    public <T> T v(DbSession dbSession, String field) {
        return this.getValue(dbSession, field);
    }

    @Override
    public <T> T getValue(String fieldName) throws NoSuchMetaFieldException {
        
        MetaField<?> mf = this.me.getMetaField(fieldName);
        if (mf == null && !this.valueMap.containsKey(fieldName)) {
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
            if ("password".equals(name) || mf.getDataType().equals(Consts.META_FIELD_DATA_TYPE_BINARY)) {
                continue;
            }
            m.put(name, this.getValue(name));
        }
        return m;
    }

    @Override
    public Map<String, Object> extraValueMap(DbSession dbSession) {
        Map<String, Object> m = new HashMap<>();
        for (String fn: this.valueMap.keySet()) {
            MetaField mf = this.getMetaEntity().getMetaField(fn);
            if (mf == null) {
                m.put(fn, this.v(fn));
            } else {

                String name = mf.getName();
                
                if ("password".equals(name) || mf.getDataType().equals(Consts.META_FIELD_DATA_TYPE_BINARY)) {
                    continue;
                }

                if (mf.isSingleReference()) {
                    Entity e = this.sr(dbSession, name);
                    if (e == null) {
                        m.put(name, null);
                    } else {
                        m.put(name, e.extraValueMap(dbSession));
                    }
                } else {
                    m.put(name, this.getValue(name));
                }
            }
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

        if (uuids == null) {
            List<Entity> entities = dbSession.queryRawSql(mrf.getMappingMetaEntity().getName(), "s_" + me.getName() + "Uuid = ? ORDER BY weight", new Object[]{this.getUuid()});

            logger.debug(" multi uuids " + entities.stream().map(ee -> ee.getValue("t_" + mrf.getTargetMetaEntity().getName() + "Uuid")));

            List<String> finalUuids = new ArrayList<>();
            entities.stream().forEach(ee -> finalUuids.add(ee.getValue("t_" + mrf.getTargetMetaEntity().getName() + "Uuid")));
            uuids = finalUuids;

            this.setValue(mf.getName(), uuids);
        }

        //logger.info("Get multi reference " + fieldName + " uuids " + uuids);
        List<Entity> entities = new ArrayList<>();
        
        for (String id: uuids) {
            Entity e = dbSession.getEntity(mrf.getTargetMetaEntity().getName(), id);
            entities.add(e);
        }
        
        //logger.info("Get multi reference " + entities);
        
        return entities;
    }
    
    @Override
    public Entity sr(DbSession dbSession, String fieldName) {
        return this.getSingleReference(dbSession, fieldName);
    }

    @Override
    @Deprecated
    public Map<String, Object> asMap() {
        Map<String, Object> m = new HashMap<>();
        for (MetaField<?> mf: this.me.getMetaFields()) {
            if (mf.getFieldType().equals(Consts.META_FIELD_TYPE_DATA_FIELD)) {
                m.put(mf.getName(), v(mf.getName()));
            }
        }
        return m;
    }

    @Override
    public <T extends EntityModel> T looksLike(Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.convertValue(asMap(), clazz);
    }

    @Override
    public <T extends EntityModel> Entity fromModel(T entityModel) {
        return fromMap(modelToMap(entityModel));
    }

    @Override
    public Entity fromMap(Map<String, Object> map){
        if (map == null) return this;

        long changeValueCounts = this.getMetaEntity().getMetaFields().stream()
                .filter(
                        mf -> !"createdAt".equals(mf.getName())
                                && !"updatedAt".equals(mf.getName())
                                && map.containsKey(mf.getName())
                                && map.get(mf.getName()) != null
                                && (mf.isSingleReference() || Consts.META_FIELD_TYPE_DATA_FIELD.equals(mf.getFieldType()))
                )
                .map(mf -> {
                    Object tempV = map.get(mf.getName());
                    if (mf.isSingleReference() && tempV instanceof Map && !StringUtils.isEmpty(((Map) tempV).get("uuid"))) {
                        setValue(mf.getName(), ((Map) tempV).get("uuid"));
                    } else {
                        setValue(mf.getName(), tempV);
                    }
                    return mf;
                }).count();

        return this;
    }

    private <T extends EntityModel> Map<String, Object> modelToMap(T entityModel){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.convertValue(entityModel, new TypeReference<Map<String, Object>>() {});
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
        
        String key = targetMe.getName() + ":" + uuid;
        Entity refEntity = this.srMap.get(key);
        if (refEntity != null) {
            return refEntity;
        }
        
        refEntity = dbSession.getEntity(targetMe, uuid);
        this.srMap.put(key, refEntity);
        return refEntity;
    }

    @Override
    public boolean isNew() {
        String uuid = this.getUuid();
        // NOTE: yinwm -- It's a workaround strategy for outside give uuid, start with minus sign seems a new entity 
        // when save to storage, DBSession should check and remove prefix minus sign, change update to insert
        return StringUtils.isEmpty(this.getUuid()) || uuid.startsWith("-");
    }

    @Override
    public String toString() {
        return "EntityImpl [me=" + me.getName() + ", uuid=" + this.getUuid()+ "]";
    }


}
