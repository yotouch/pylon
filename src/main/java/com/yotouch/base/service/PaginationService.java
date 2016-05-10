package com.yotouch.base.service;

import java.util.Map;

public interface PaginationService {

    Map<String, Object> getPaginationInfo(int currentPage, int total, int itemPerPage, String entityName, String queryString, Object[] queryCondition, String paginationUrl);
 
}
