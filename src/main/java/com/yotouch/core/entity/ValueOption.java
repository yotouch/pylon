package com.yotouch.core.entity;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tammy on 01/07/2017.
 */
public class ValueOption {
    private String displayName;
    private String value;
    private MetaField<?> metaField;
    private String fieldName;
    private MetaEntity metaEntity;
    private String entityName;
    private String pinYin;
    private boolean checked;
    private Integer weight;

    private static Map<String, Integer> idx = new HashMap<>();

    public ValueOption(String displayName, MetaField<?> metaField, Integer weight, boolean checked) {
        this.displayName = displayName;
        this.metaField = metaField;
        this.fieldName = metaField.getName();
        this.metaEntity = metaField.getMetaEntity();
        if (this.metaEntity != null) {
            entityName = this.metaEntity.getName();
        }
        this.checked = checked;
        this.weight = weight;

        String valuePrefix = genValuePrefix();
        ValueOption.idx.putIfAbsent(valuePrefix, 0);

        int id = ValueOption.idx.get(valuePrefix);
        this.value = valuePrefix + id;
        ValueOption.idx.put(valuePrefix, ++id);

        this.pinYin = Pinyin.toPinyin(this.displayName, "");
    }

    private String genValuePrefix() {
        if (metaField.getMetaEntity() == null) {
            return "systemField-" + metaField.getName() + "-";
        }
        return metaField.getMetaEntity().getName() + "-" + metaField.getName() + "-";
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return value;
    }

    public boolean isChecked() {
        return checked;
    }

    public Integer getWeight() {
        return weight;
    }

    public String getPinYin() {
        return pinYin;
    }

    public MetaField<?> getMetaField() {
        return metaField;
    }

    public MetaEntity getMetaEntity() {
        return metaEntity;
    }

    public void setMetaEntity(MetaEntity metaEntity) {
        this.metaEntity = metaEntity;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getEntityName() {
        return entityName;
    }
}
