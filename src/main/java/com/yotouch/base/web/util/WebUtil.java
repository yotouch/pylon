package com.yotouch.base.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.MetaEntity;
import com.yotouch.core.entity.MetaField;
import com.yotouch.core.runtime.DbSession;

@Component
public class WebUtil {

    static final private Logger logger = LoggerFactory.getLogger(WebUtil.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, Object> asRetJson(Map<String, Object> ret) {

        Map<String, Object> newMap = new HashMap<>();

        for (String key : ret.keySet()) {

            logger.info("Parse to ret json key " + key);

            Object value = ret.get(key);

            if (value instanceof List) {
                newMap.put(key, parseList((List) value));
            } else if (value instanceof Map) {
                newMap.put(key, asRetJson((Map<String, Object>) value));
            } else if (value instanceof Entity) {
                Entity e = (Entity) value;
                newMap.put(key, e.valueMap());
            } else {
                newMap.put(key, value);
            }

        }

        return newMap;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List parseList(List l) {
        List newList = new ArrayList<>();

        for (Object o : l) {
            if (o instanceof List) {
                newList.add(parseList((List) o));
            } else if (o instanceof Map) {
                newList.add(asRetJson((Map<String, Object>) o));
            } else if (o instanceof Entity) {
                Entity e = (Entity) o;
                newList.add(e.valueMap());
            } else {
                newList.add(o);
            }
        }

        return newList;

    }

    

    private void setRequestValue(HttpServletRequest request, Entity e, MetaField<?> mf) {
        String name = e.getMetaEntity().getName() + "_" + mf.getName();
        String value = request.getParameter(name);
        
        logger.info("Set http value for entity " + name + " value " + value + " mf type " + mf.getDataType());
        
        if (mf.isMultiReference()) {
            String uuids = "";
            if (!StringUtils.isEmpty(value)) {
                uuids = value;
            }
            
            List<String> uuidList = Lists.newArrayList(Splitter.on(',').omitEmptyStrings().split(uuids));
            
            logger.info("MR value " + uuidList);
            
            if (uuidList == null || uuidList.size() == 0) {
                e.setValue(mf.getName(), new ArrayList<String>());
            } else {
                e.setValue(mf.getName(), uuidList);
            }
        } else if (mf.isSingleReference()) {
            e.setValue(mf.getName(), request.getParameter(name));
        } else if (Consts.META_FIELD_TYPE_DATA_FIELD.equalsIgnoreCase(mf.getFieldType())) {
            e.setValue(mf.getName(), request.getParameter(name));
        }
    }

    public String getFullUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        
        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    public Entity updateEntityVariables(Entity entity, HttpServletRequest request) {
        
        Map<String, String[]> paramMap = request.getParameterMap();
        
        MetaEntity me = entity.getMetaEntity();
        for (MetaField<?> mf: me.getMetaFields()) {
            String fname = me.getName() + "_" + mf.getName();
            if (!paramMap.containsKey(fname)) {
                continue;
            }
            
            this.setRequestValue(request, entity, mf);            
        }
        
        return entity;
    }

    public String getBaseUrl(HttpServletRequest request) {
        if (request.getServerPort() == 80) {
            return String.format("%s://%s",request.getScheme(),  request.getServerName());
        } else {
            return String.format("%s://%s:%d",request.getScheme(),  request.getServerName(), request.getServerPort());
        }
    }

    public String getBid(HttpServletRequest request){
        String bid = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("bid".equalsIgnoreCase(c.getName())) {
                    bid = c.getValue();
                }
            }
        }

        return bid;
    }

}
