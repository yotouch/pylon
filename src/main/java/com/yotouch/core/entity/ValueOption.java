package com.yotouch.core.entity;

import com.github.promeg.pinyinhelper.Pinyin;
import com.yotouch.core.exception.MetaFieldHasNoMetaEntityException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tammy on 01/07/2017.
 */
public class ValueOption {
    private String       displayName;
    private String       value;
    private MetaField<?> metaField;
    private MetaEntity   metaEntity;
    private String       pinYin;
    private boolean      checked;
    private Integer      weight;
    private static Map<String, ValueOption> loadedValues = new HashMap<>();

    private ValueOption(MetaField<?> metaField, String displayName, Integer weight, boolean checked) {

        this.displayName = displayName;
        this.metaField = metaField;
        this.metaEntity = metaField.getMetaEntity();
        this.checked = checked;
        this.weight = weight;
        this.value = genValue(metaField, displayName);
        this.pinYin = Pinyin.toPinyin(this.displayName, "");
    }

    public static ValueOption build(MetaField<?> metaField, String displayName, Integer weight, boolean checked) throws MetaFieldHasNoMetaEntityException {
        if (metaField.getMetaEntity() == null) {
            throw new MetaFieldHasNoMetaEntityException(metaField);
        }

        String value = genValue(metaField, displayName);
        if (loadedValues.containsKey(value)) {
            return loadedValues.get(value);
        }

        ValueOption valueOption = new ValueOption(metaField, displayName, weight, checked);
        loadedValues.put(value, valueOption);
        return valueOption;
    }

    private static String genValue(MetaField<?> metaField, String displayName) {
        return metaField.getMetaEntity().getName() + "-" + metaField.getName() + "-" + displayName;
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
}
