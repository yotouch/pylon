package com.yotouch.core.model;

import org.springframework.stereotype.Component;

/**
 * Created by tammy on 09/07/2017.
 */
@Component
public class ValueOptionModel extends EntityModel {
    private String          displayName;
    private String          value;
    private String          pinyin;
    private Boolean         checked;
    private Boolean         deleted;
    private Integer         weight;
    private MetaEntityModel metaEntity;
    private MetaFieldModel  metaField;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public MetaEntityModel getMetaEntity() {
        return metaEntity;
    }

    public void setMetaEntity(MetaEntityModel metaEntity) {
        this.metaEntity = metaEntity;
    }

    public MetaFieldModel getMetaField() {
        return metaField;
    }

    public void setMetaField(MetaFieldModel metaField) {
        this.metaField = metaField;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
