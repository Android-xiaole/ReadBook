package com.jj.comics.widget.bookreadview;

/**
 * Created by newbiechen on 17-7-1.
 */

public class TxtChapter{

    //服务器端的章节id
    String chapterId;
    //标记当前章节是否付费
    boolean isPaid = true;//默认已经付费,后续会通过接口获取内容的时候重置状态，查询是否付费
    //标记当前章节是否需要登录后观看
    boolean needLogin;//默认不需要登录
    //章节所属的小说(网络)
    String bookId;
    //章节的链接(网络)
    String link;

    //章节名(共用)
    String title;

    //章节内容在文章中的起始位置(本地)
    long start;
    //章节内容在文章中的终止位置(本地)
    long end;

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String id) {
        this.bookId = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    @Override
    public String toString() {
        return "TxtChapter{" +
                "chapterId='" + chapterId + '\'' +
                ", isPaid=" + isPaid +
                ", needLogin=" + needLogin +
                ", bookId='" + bookId + '\'' +
                ", link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
