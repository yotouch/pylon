package com.yotouch.core.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaEntityImpl implements MetaEntity {

    private Map<String, MetaField<?>> fieldMap;
    
    private String name;
    private String uuid;
    private String tablePrefix;

    /*
    public MetaEntityImpl(String uuid, String name) {
        this(uuid, name, "usr");
    }
    */

    MetaEntityImpl(String uuid, String name, String tablePrefix) {
        this.name = name;
        this.uuid = uuid;
        this.tablePrefix = tablePrefix; 
        this.fieldMap = new HashMap<>();
    }

    public void addField(MetaField<?> field) {
        this.fieldMap.put(field.getName(), field);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Object> MetaField<T> getMetaField(String name) {
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

    
    public String getTableName() {
        return this.tablePrefix + this.getName();
    }
}


