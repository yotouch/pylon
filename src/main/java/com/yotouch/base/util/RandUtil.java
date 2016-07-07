package com.yotouch.base.util;

import org.springframework.stereotype.Component;

@Component
public class RandUtil {


    public String genCode(int length) {

        String s = "";
        for (int i = 0; i < length; i ++) {
            int r = (int)(Math.random()*9);
            s += r;
        }


        return s;

    }



}
