package com.yotouch.base.service;

import java.util.Map;

public interface PaginationService {

    Map<String, Object> getPaginationInfo(int currentPage, int total, int itemPerPage, String entityName, String queryString, Object[] queryCondition, String paginationUrl);

    Map<String, Object> getPageInfo(int currentPage, int totalPage, String paginationUrl, int paginationWidth);

    Map<String, Integer> getInitPageInfo(int currentPage, int total, int itemPerPage);
}
