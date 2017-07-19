package com.yotouch.test.core.model;

import com.yotouch.core.model.EntityModel;

public class User extends EntityModel {
    private Integer age;
    private String nickname;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
