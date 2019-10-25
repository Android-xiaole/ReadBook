package com.jj.comics.data.visittime;

/**
 * Author ：le
 * Date ：2019-10-25 16:57
 * Description ：通用的时间上报
 */
public class TimeReportData {

    private String loginTime;//最近登录时间
    private String logoutTime;//最近退出时间
    private int onlineDuration;//累计在线时长
    private int readDuration;//累计阅读时长
    private long bookId;//小说id
    private String date;//数据产生日期
    private String token;//游客id
    private long uid;//登录用户id


    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(String logoutTime) {
        this.logoutTime = logoutTime;
    }

    public int getOnlineDuration() {
        return onlineDuration;
    }

    public void setOnlineDuration(int onlineDuration) {
        this.onlineDuration = onlineDuration;
    }

    public int getReadDuration() {
        return readDuration;
    }

    public void setReadDuration(int readDuration) {
        this.readDuration = readDuration;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
