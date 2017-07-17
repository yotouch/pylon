package com.yotouch.test.core.model;

import com.yotouch.core.model.EntityModel;
import org.springframework.stereotype.Component;

/**
 * Created by tammy on 04/06/2017.
 */
@Component
public class Party extends EntityModel {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
