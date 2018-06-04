package com.yotouch.core.entity;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yotouch.core.entity.mf.LongMetaFieldImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.fv.FieldValue;
import com.yotouch.core.entity.mf.BinaryMetaFieldImpl;
import com.yotouch.core.entity.mf.BoolMetaFieldImpl;
import com.yotouch.core.entity.mf.DateTimeMetaFieldImpl;
import com.yotouch.core.entity.mf.DoubleMetaFieldImpl;
import com.yotouch.core.entity.mf.IntMetaFieldImpl;
import com.yotouch.core.entity.mf.MultiReferenceMetaFieldImpl;
import com.yotouch.core.entity.mf.ObjectMetaFieldImpl;
import com.yotouch.core.entity.mf.SingleReferenceMetaFieldImpl;
import com.yotouch.core.entity.mf.StringMetaFieldImpl;

public abstract class MetaFieldImpl<T> implements MetaField<T>, Cloneable {
    
    static final private Logger logger = LoggerFactory.getLogger(MetaFieldImpl.class);
    
    protected MetaEntity me;
    protected String type = "user";
    protected String        uuid;
    protected String        name;
    protected String        displayName;
    protected boolean       required;
    protected boolean       visible;
    protected boolean       deleted;
    protected FieldValue<T> defaultValue;
    protected List<ValueOption> valueOptions = new ArrayList<>();
    
    
    @Override
    public String getType() {
        return this.type;
    }
    
    public MetaFieldImpl setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public void addValueOption(ValueOption valueOption) {
        if (!valueOptions.contains(valueOption)) {
            valueOptions.add(valueOption);
        }
    }

    @Override
    public ValueOption getValueOption(String valueOptionDisplayname) {
        List<ValueOption> afterFilter = this.valueOptions.stream()
                .filter(valueOption -> valueOption.getDisplayName().equals(valueOptionDisplayname))
                .collect(Collectors.toList());
        return afterFilter.isEmpty() ? null : afterFilter.get(0);
    }

    @Override
    public List<ValueOption> getValueOptions() {
        return this.valueOptions;
    }

    @Override
    public void addValueOptions(List<ValueOption> valueOptions) {
        if (valueOptions != null && !valueOptions.isEmpty()) {
           for (ValueOption vo : valueOptions) {
               this.addValueOption(vo);
           }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String asSql() {
        return this.getName();
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }
    
    void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    abstract public String getDataType();
        
    @Override
    public boolean isRequired() {
        return this.required;
    }
    
    void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isDeleted() {
        return this.deleted;
    }

    void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


    @Override
    public T getDefaultValue() {
        return this.getDefaultFieldValue().getValue();
    }

    @Override
    public FieldValue<T> getDefaultFieldValue() {
        if (this.defaultValue != null) {
            try {
                return this.defaultValue.copy();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }

        }
        return this.defaultValue;
    }
    
    protected void setDefaultValue(Object dv) {
        this.defaultValue = this.newFieldValue(dv);
    }
    
    @Override
    public String getUuid() {
        return this.uuid;
    }
    
    private void setUuid(String uuid) {
        this.uuid = uuid;        
    }
    
    public void setMetaEntity(MetaEntityImpl mei) {
        this.me = mei;        
    }
    
    public MetaEntity getMetaEntity() {
        return this.me;
    }

    public static MetaFieldImpl<?> build(EntityManager entityMgr, Map<String, Object> fr) {
        
        String dataType = (String) fr.get("dataType");
        String fieldType = (String) fr.get("fieldType");

        MetaFieldImpl<?> mfi = null;
        
        logger.debug("Build MetaField " + fr.get("name") + " with dataType " + dataType + " with fieldType " + fieldType);

        if (Consts.META_FIELD_TYPE_SINGLE_REFERENCE.equalsIgnoreCase(fieldType)) {
            String targetMetaEntity = (String) fr.get("targetMetaEntity");
            mfi = new SingleReferenceMetaFieldImpl(entityMgr, targetMetaEntity);
        } else if (Consts.META_FIELD_TYPE_MULTI_REFERENCE.equalsIgnoreCase(fieldType)) {
            String targetMetaEntity = (String) fr.get("targetMetaEntity");
            mfi = new MultiReferenceMetaFieldImpl(entityMgr, targetMetaEntity);
        } else {
            if (Consts.META_FIELD_DATA_TYPE_STRING.equalsIgnoreCase(dataType)) {
                mfi = new StringMetaFieldImpl();
            } else if (Consts.META_FIELD_DATA_TYPE_UUID.equalsIgnoreCase(dataType)) {
                mfi = new UuidMetaFieldImpl();
            } else if (Consts.META_FIELD_DATA_TYPE_TEXT.equalsIgnoreCase(dataType)) {
                mfi = new TextMetaFieldImpl();
            } else if (Consts.META_FIELD_DATA_TYPE_DATETIME.equalsIgnoreCase(dataType)) {
                mfi = new DateTimeMetaFieldImpl();
            } else if (Consts.META_FIELD_DATA_TYPE_INT.equalsIgnoreCase(dataType)) {
                mfi = new IntMetaFieldImpl();
            } else if (Consts.META_FIELD_DATA_TYPE_LONG.equalsIgnoreCase(dataType)) {
                mfi = new LongMetaFieldImpl();
            } else if (Consts.META_FIELD_DATA_TYPE_DOUBLE.equalsIgnoreCase(dataType)) {
                mfi = new DoubleMetaFieldImpl();
            } else if (Consts.META_FIELD_DATA_TYPE_BOOLEAN.equalsIgnoreCase(dataType)) {
                mfi = new BoolMetaFieldImpl();
            } else if (Consts.META_FIELD_DATA_TYPE_BINARY.equalsIgnoreCase(dataType)) {
                mfi = new BinaryMetaFieldImpl();
            } else {
                mfi = new ObjectMetaFieldImpl();
            }
        }

        //mfi.setMetaEntity(mei);
        
        mfi.setUuid((String) fr.get("uuid"));
        mfi.setName((String) fr.get("name"));
        mfi.setDisplayName((String) fr.get("displayName"));
        mfi.setDefaultValue(fr.get("defaultValue"));
        mfi.setRequired("1".equals(fr.get("required")));
        mfi.setVisible("1".equals(fr.get("visible")));
        mfi.setDeleted("1".equals(fr.get("deleted")));

        String type = (String)fr.get("type");
        if (type != null && !type.equals("")) {
            mfi.setType(type);
        }

        //logger.info("Try to build MetaField " + fr);
        //mei.addField(mfi);

        logger.debug("MFI " + mfi);

        return mfi;
    }
    
    @Override
    public boolean isReference() {
        return false;
    }
    
    @Override
    public boolean isSingleReference() {
        return false;
    }

    @Override
    public boolean isMultiReference() {
        return false;
    }
    
    @Override
    public MetaEntity getTargetMetaEntity() {
        return null;
    }
    
    @Override
    public String getFieldType() {
        return Consts.META_FIELD_TYPE_DATA_FIELD;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MetaFieldImpl<?> other = (MetaFieldImpl<?>) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MetaFieldImpl [\n\tme=" + (me == null ? "NULL" : me.getName()) + ", \n\tuuid=" + uuid + ", \n\tname=" + name + ", \n\tdisplayName=" + displayName
                + ", \n\tfieldType=" + this.getFieldType() + "\n\tdataType=" + this.getDataType() + ", \n\trequired=" + required  + ", \n\tvisible=" + visible + ", \n\tdeleted=" + deleted + ", \n\tdefaultValue=" + defaultValue + "\n]\n";
    }

    public MetaFieldImpl<?> copy(String uuid) {
        
        try {
            MetaFieldImpl<?> newF = (MetaFieldImpl<?>) this.clone();
            newF.setUuid(uuid);
            return newF;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
