package com.yotouch.core.helper;

import com.yotouch.core.Consts;

import java.util.List;

public class PaginationHelper<T> {
    private int     firstPage = 1;
    private int     lastPage;
    private int     currentPage;
    private int     totalRows;
    private int     itemPerPage = Consts.itemPerPage;
    private List<T> items;

    public PaginationHelper(int currentPage) {
        this.currentPage = currentPage < 1 ? 1 : currentPage;
        this.itemPerPage = Consts.itemPerPage;
    }

    public PaginationHelper(int currentPage, int itemPerPage) {
        this.currentPage = currentPage < 1 ? 1 : currentPage;
        this.itemPerPage = itemPerPage < 1 ? Consts.itemPerPage : itemPerPage;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage < 1 ? 1 : firstPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage < 1 ? 1 : lastPage;
    }

    public int getCurrentPage() {
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > lastPage) {
            currentPage = lastPage;
        }
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage < 1 ? 1 : currentPage;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
        lastPage = (int) Math.ceil(totalRows/(itemPerPage + 0.0));
        if(lastPage < 1){
            lastPage = 1 ;
        }
    }

    public int getItemPerPage() {
        return itemPerPage < 1 ? Consts.itemPerPage : itemPerPage;
    }

    public void setItemPerPage(int itemPerPage) {
        this.itemPerPage = itemPerPage < 1 ? Consts.itemPerPage : itemPerPage;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
