package com.yotouch.core.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class StrUtil {
    
    public String randStr() {
        return UUID.randomUUID().toString().toString().replace("-", "");
    }

    public String genVCode(int size) {
        String s = "";
        Random r = new Random();

        while (size > 0) {
            int i = Math.abs(r.nextInt());
            s += i % 10;
            size -= 1;
        }

        return s;
    }

    // for digital
    public String genVcode() {
        return this.genVCode(4);
    }

    public static <T> String buildQueryInString(List<T> valueList) {
        String[] qa = new String[valueList.size()];
        Arrays.fill(qa, "?");

        return String.join(",", qa);
    }
}
