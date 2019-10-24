package com.jj.comics.data.visittime;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Author ：le
 * Date ：2019-10-24 14:13
 * Description ：统计阅读页阅读时长
 */
@Entity
public class ReadTimeData {

    @Id(autoincrement = true)
    private Long id;

    private String date;//日期，在线日期

    private String uid;//用户id（游客登录为友盟生成的id,友盟返回为null时用UUID替代）

    private boolean is_visitor;//标记是否为游客登录

    private int duration;//持续时间，单位秒(该字段累加)

    private long boodId;//小说id

    @Generated(hash = 1584541891)
    public ReadTimeData(Long id, String date, String uid, boolean is_visitor,
            int duration, long boodId) {
        this.id = id;
        this.date = date;
        this.uid = uid;
        this.is_visitor = is_visitor;
        this.duration = duration;
        this.boodId = boodId;
    }

    @Generated(hash = 1239458250)
    public ReadTimeData() {
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean getIs_visitor() {
        return this.is_visitor;
    }

    public void setIs_visitor(boolean is_visitor) {
        this.is_visitor = is_visitor;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getBoodId() {
        return this.boodId;
    }

    public void setBoodId(long boodId) {
        this.boodId = boodId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
