package com.yotouch.core.entity;

/**
 * Created by tammy on 01/07/2017.
 */
public interface ValueOption {
    String getDisplayName();
    String getValue();
    boolean isChecked();
    Integer getWeight();
    String getPinYin();
    <T> MetaField<T> getMetaField();
}
