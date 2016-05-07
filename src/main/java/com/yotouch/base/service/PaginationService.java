/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yotouch.base.service;

import java.util.Map;

/**
 *
 * @author king
 */
public interface PaginationService {

    /**
     *
     * @param currentPage
     * @param total
     * @param itemPerPage
     * @param entityName
     * @param queryString
     * @param queryCondition
     * @param paginationUrl
     * @return
     */
    Map<String, Object> getPaginationInfo(int currentPage, int total, int itemPerPage, String entityName, String queryString, Object[] queryCondition, String paginationUrl);
 
}
