package com.crsc.nfctest.model;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * 测试记录模型类
 */
public class Record extends DataSupport {

    private int id; //默认主键
    private String tagId; //标签ID
    private String tpye; //标签类型
    private String date; //测试日期
    private Integer isComplete; //标签是否完整，1表示完整，0表示不完整
    private Integer count; //测试次数
    private Integer result; //测试结果

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTpye() {
        return tpye;
    }

    public void setTpye(String tpye) {
        this.tpye = tpye;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Integer isComplete) {
        this.isComplete = isComplete;
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
