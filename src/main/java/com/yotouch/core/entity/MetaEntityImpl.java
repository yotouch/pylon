package com.yotouch.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yotouch.core.exception.MetaFieldIsNotSingleReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class MetaEntityImpl implements MetaEntity {

    private static final Logger logger = LoggerFactory.getLogger(MetaEntityImpl.class);

    private Map<String, MetaField<?>> fieldMap;
    
    private String name;
    private String displayName;
    private String uuid;
    private String tablePrefix;
    
    private String scope;

    private boolean lowerTableNames;

    MetaEntityImpl(String uuid, String name, String displayName, String tablePrefix, String scope, boolean lowerTableName) {
        this.name = name;
        this.uuid = uuid;
        this.displayName = displayName;
        this.tablePrefix = tablePrefix; 
        this.fieldMap = new HashMap<>();
        this.lowerTableNames = lowerTableName;
        this.scope = scope;
    }

    public void addField(MetaField<?> field) {
        this.fieldMap.put(field.getName(), field);
    }

    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Object> MetaField<T> getMetaField(String name) {
        
        String[] nameParts = name.split("\\.");
        if (nameParts.length > 1) {
            String firstName = nameParts[0];
            
            MetaField mf = this.fieldMap.get(firstName);
            if (!mf.isReference()) {
                throw new MetaFieldIsNotSingleReference(this, mf);
            }
            
            MetaEntity me = mf.getTargetMetaEntity();
            name = name.substring(name.indexOf('.') + 1);
            return me.getMetaField(name);
        }
        
        
        return (MetaField<T>) this.fieldMap.get(name);
    }

    @Override
    public List<MetaField<?>> getMetaFields() {
        return new ArrayList<>(fieldMap.values());
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        MetaEntityImpl other = (MetaEntityImpl) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MetaEntityImpl [name=" + name + ", uuid=" + uuid + "]";
    }

    public void addMetaField(MetaField<?> mf) {
        this.fieldMap.put(mf.getName(), mf);
    }

    @Override
    public Entity newEntity() {
        return new EntityImpl(this);
    }

    @Override
    public String getScope() {
        return this.scope;
    }


    public String getTableName() {
        
        String name = this.getName();
        
        if (!StringUtils.isEmpty(this.scope)) {
            name = this.scope + "_" + name;
        }
        
        if (!(this.tablePrefix == null || "".equals(this.tablePrefix) || "-".equals(this.tablePrefix))) {
            name = this.tablePrefix + name;
        }
        
        if (lowerTableNames) {
            return name.toLowerCase();
        }

        logger.debug("Get table name NO lower case " + name.toLowerCase());

        return name;
    }

    @Override
    public int compareTo(MetaEntity o) {
        return this.getName().compareTo(o.getName());
    }
}


