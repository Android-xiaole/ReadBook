package com.jj.comics.data.model;

public class RichManModel {
    /**
     * total : 15250
     * username : 张三
     * avatar : null
     */

    private long id;
    private String total;
    private String username;
    private String avatar;
    private int actvalue;

    public int getActvalue() {
        return actvalue;
    }

    public void setActvalue(int actvalue) {
        this.actvalue = actvalue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
