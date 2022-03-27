package ks.util;

import java.util.ArrayList;
import java.util.List;

public class L1Pagination {
    private int currentPageNo = 1;
    private int recordCountPerPage = 5;
    private int pageSize = 1000;
    private int totalRecordCount;

    private List<?> searchList = new ArrayList<>();

    public L1Pagination() {
    }

    public List<?> getSearchList() {
        return searchList;
    }

    public void setSearchList(List<?> searchList) {
        this.searchList = searchList;
    }

    public int getRecordCountPerPage() {
        return this.recordCountPerPage;
    }

    public void setRecordCountPerPage(int recordCountPerPage) {
        this.recordCountPerPage = recordCountPerPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPageNo() {
        return this.currentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public int getTotalRecordCount() {
        return this.totalRecordCount;
    }

    public void setTotalRecordCount(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }

    public int getTotalPageCount() {
        return (this.getTotalRecordCount() - 1) / this.getRecordCountPerPage() + 1;
    }

    public void prev() {
        setCurrentPageNo(getCurrentPageNo() - 1);

        if (getCurrentPageNo() < 1) {
            setCurrentPageNo(getTotalPageCount());
        }
    }

    public void next() {
        if (getCurrentPageNo() >= getTotalPageCount()) {
            setCurrentPageNo(1);
        } else {
            setCurrentPageNo(getCurrentPageNo() + 1);
        }
    }

    public String getPagingString() {
        return getCurrentPageNo() + "/" + getTotalPageCount();
    }
}
