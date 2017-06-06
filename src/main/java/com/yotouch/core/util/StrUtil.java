package com.yotouch.core.util;

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

    public static String firstCharToLowercase (String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        char c[] = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }

    public static String firstCharToUpercase (String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        char c[] = string.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }

    public static String entityNameToModelName(String entityName) {
        return firstCharToUpercase(entityName);
    }

    public static String modelNameToEntityName(String modelName) {
        return firstCharToLowercase(modelName);
    }
}
