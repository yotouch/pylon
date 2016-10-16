package com.yotouch.base.service;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.core.runtime.YotouchRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaginationServiceImpl implements PaginationService {
    
    @Autowired
    protected YotouchApplication ytApp;

    public Map<String, Object> getPageInfo(int currentPage, int totalPage, String paginationUrl, int paginationWidth){
        int firstPage = currentPage - paginationWidth/2;
        int lastPage = currentPage + paginationWidth/2;
        
        if (firstPage <= 1){
            firstPage = 1;
            lastPage = paginationWidth > totalPage ? totalPage : paginationWidth;
        }

        if (lastPage >= totalPage){
            lastPage = totalPage;
            firstPage = (totalPage - paginationWidth + 1) < 1 ? 1 : (totalPage - paginationWidth + 1);
        }
        
        Map<String, Object> pageInfo = new HashMap<>();
        pageInfo.put("firstPage", firstPage);
        pageInfo.put("lastPage", lastPage);
        pageInfo.put("paginationUrl", paginationUrl);
        pageInfo.put("currentPage", currentPage);
        pageInfo.put("totalPage", totalPage);
        
        return pageInfo;
    }

    public Map<String, Integer> getInitPageInfo(int currentPage, int total, int itemPerPage) {
        int totalPage = (int) Math.ceil(total/(itemPerPage + 0.0));
        if (currentPage < 1){
            currentPage = 1;
        }

        if (currentPage > totalPage) {
            currentPage = totalPage;
        }

        int offset =  (currentPage - 1) * itemPerPage;

        Map<String, Integer> results = new HashMap<>();
        results.put("totalPage", totalPage);
        results.put("offset", offset);
        results.put("currentPage", currentPage);

        return results;
    }

    public Map<String, Object> getPaginationInfo(int currentPage, int total, int itemPerPage, String entityName, String queryString, Object[] queryCondition, String paginationUrl){
        YotouchRuntime runtime = ytApp.getRuntime();
        DbSession dbSession = runtime.createDbSession();
        
        Map<String, Object> pageInfoResult = new HashMap<>();

        int totalPage = (int) Math.ceil(total/(itemPerPage + 0.0));
        if (currentPage < 1){
            currentPage = 1;
        }
        
        if (currentPage > totalPage) {
            currentPage = totalPage;
        }
        
        int offset =  (currentPage - 1) * itemPerPage;
        
        List<Entity> entities = dbSession.queryRawSql(entityName, queryString + " limit " + offset + "," + itemPerPage, queryCondition);
        
        Map<String, Object> paginationInfo = getPageInfo(currentPage, totalPage, paginationUrl, Consts.paginationWidth);
        
        pageInfoResult.put("paginationInfo", paginationInfo);
        pageInfoResult.put(entityName, entities);
        
        return pageInfoResult;
    }
    

}
