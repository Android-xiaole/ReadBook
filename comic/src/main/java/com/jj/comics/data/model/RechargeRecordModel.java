package com.jj.comics.data.model;

public class RechargeRecordModel {


    /**
     * id : 4240327
     * title : 签到有礼
     * jinbi : 10
     * created_at : 2019-07-22
     */

    private long id;
    private String title;
    private int jinbi;
    private String created_at;
    private String money;

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getJinbi() {
        return jinbi;
    }

    public void setJinbi(int jinbi) {
        this.jinbi = jinbi;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
