package com.yotouch.core.model;

import org.springframework.stereotype.Component;

/**
 * Created by tammy on 09/07/2017.
 */
@Component
public class MetaFieldModel extends EntityModel {
    private String          displayName;
    private String          name;
    private Boolean         required;
    private String          defaultValue;
    private String          dataType;
    private String          fieldType;
    private MetaEntityModel targetMetaEntity;
    private Integer         weight;
    private Boolean         visible;
    private Boolean         deleted;
    private MetaEntityModel metaEntity;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public MetaEntityModel getTargetMetaEntity() {
        return targetMetaEntity;
    }

    public void setTargetMetaEntity(MetaEntityModel targetMetaEntity) {
        this.targetMetaEntity = targetMetaEntity;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public MetaEntityModel getMetaEntity() {
        return metaEntity;
    }

    public void setMetaEntity(MetaEntityModel metaEntity) {
        this.metaEntity = metaEntity;
    }
}
