package com.yotouch.core.model;

import org.springframework.stereotype.Component;

/**
 * Created by tammy on 09/07/2017.
 */
@Component
public class MetaFieldModel extends EntityModel {
    private String          displayName;
    private String          name;
    private boolean         required;
    private String          defaultValue;
    private String          dataType;
    private String          fieldType;
    private MetaEntityModel targetMetaEntity;
    private Integer         weight;
    private boolean         visible;
    private boolean         disabled;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
