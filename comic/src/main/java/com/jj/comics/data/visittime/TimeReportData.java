package com.jj.comics.data.visittime;

/**
 * Author ：le
 * Date ：2019-10-25 16:57
 * Description ：通用的时间上报
 */
public class TimeReportData {

    private long id;//本地数据库主键,用于删除时使用
    private boolean is_visitor;//标记当前用户是否为游客
    private String loginTime;//最近登录时间
    private String logoutTime;//最近退出时间
    private int onlineDuration;//累计在线时长
    private int readDuration;//累计阅读时长
    private long bookId;//小说id
    private long chapterId;//章节id
    private String date;//数据产生日期
    private String token;//游客id
    private long uid;//登录用户id

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isIs_visitor() {
        return is_visitor;
    }

    public void setIs_visitor(boolean is_visitor) {
        this.is_visitor = is_visitor;
    }

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

    public long getChapterId() {
        return chapterId;
    }

    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
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
