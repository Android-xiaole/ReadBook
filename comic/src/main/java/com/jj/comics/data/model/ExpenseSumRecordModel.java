package com.jj.comics.data.model;

public class ExpenseSumRecordModel {
    /**
     * id : 52
     * sourceid : 1
     * articleid : 232
     * update_time : 2019-07-23
     * total_money : 272.00
     * articlename : 郑主任为何这样
     */

    private long id;
    private int sourceid;
    private int articleid;
    private String update_time;
    private String total_money;
    private String articlename;
    private String cover;

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSourceid() {
        return sourceid;
    }

    public void setSourceid(int sourceid) {
        this.sourceid = sourceid;
    }

    public int getArticleid() {
        return articleid;
    }

    public void setArticleid(int articleid) {
        this.articleid = articleid;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getTotal_money() {
        return total_money;
    }

    public void setTotal_money(String total_money) {
        this.total_money = total_money;
    }

    public String getArticlename() {
        return articlename;
    }

    public void setArticlename(String articlename) {
        this.articlename = articlename;
    }
}
