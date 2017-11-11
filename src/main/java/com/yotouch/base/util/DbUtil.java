package com.yotouch.base.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DbUtil {
    public static String buildQueryIn(List<String> valueList) {
        String[] qa = new String[valueList.size()];
        Arrays.fill(qa, "?");

        return String.join(",", qa);
    }
}
