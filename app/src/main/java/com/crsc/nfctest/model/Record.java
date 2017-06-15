package com.crsc.nfctest.model;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by liuji on 2017/6/15.
 */

public class Record extends DataSupport {

    private String tpye;
    private Date date;
    private Integer isAuthenticated;
    private Integer isEmpty;
    private Integer count;
    private Integer result;

    public String getTpye() {
        return tpye;
    }

    public void setTpye(String tpye) {
        this.tpye = tpye;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getIsAuthenticated() {
        return isAuthenticated;
    }

    public void setIsAuthenticated(Integer isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
    }

    public Integer getIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(Integer isEmpty) {
        this.isEmpty = isEmpty;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
