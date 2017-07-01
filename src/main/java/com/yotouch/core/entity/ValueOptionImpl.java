package com.yotouch.core.entity;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tammy on 01/07/2017.
 */
public class ValueOptionImpl implements ValueOption {
    private String displayName;
    private String value;
    private MetaField<?> metaField;
    private String pinYin;
    private boolean checked;
    private Integer weight;

    private static Map<String, Integer> idx = new HashMap<>();

    public ValueOptionImpl(String displayName, MetaField<?> metaField, Integer weight, boolean checked) {
        this.displayName = displayName;
        this.metaField = metaField;
        this.checked = checked;
        this.weight = weight;

        String valuePrefix = getValuePrefix();
        ValueOptionImpl.idx.putIfAbsent(valuePrefix, 0);

        int id = ValueOptionImpl.idx.get(valuePrefix);
        this.value = valuePrefix + id;
        ValueOptionImpl.idx.put(valuePrefix, ++id);

        this.pinYin = Pinyin.toPinyin(this.displayName, "");
    }

    private String getValuePrefix() {
        if (metaField.getMetaEntity() == null) {
            return "systemField-" + metaField.getName() + "-";
        }
        return metaField.getMetaEntity().getName() + "-" + metaField.getName() + "-";
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public Integer getWeight() {
        return weight;
    }

    @Override
    public String getPinYin() {
        return pinYin;
    }

    @Override
    public MetaField<?> getMetaField() {
        return metaField;
    }
}
