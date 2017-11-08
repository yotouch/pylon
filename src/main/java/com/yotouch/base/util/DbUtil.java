package com.yotouch.base.util;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class DbUtil {

    public static QueryIn buildQueryIn(List<String> valueList) {
        String[] qa = new String[valueList.size()];
        Arrays.fill(qa, "?");
        String qaString = String.join(",", qa);
        String valueString = String.join(",", valueList);

        return new QueryIn(qaString, valueString);
    }

    public static class QueryIn {
        private String query;
        private String value;

        public QueryIn() {
        }

        public QueryIn(String query, String value) {
            this.query = query;
            this.value = value;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
