package com.jj.comics.util.eventbus.events;

/**
 * 发送更新阅读历史记录的通知
 */
public class UpdateReadHistoryEvent {

    private long chapterid;//章节id
    private int chapterorder;//表示阅读至第几话

    public UpdateReadHistoryEvent(long chapterid, int chapterorder) {
        this.chapterid = chapterid;
        this.chapterorder = chapterorder;
    }

    public long getChapterid() {
        return chapterid;
    }

    public void setChapterid(long chapterid) {
        this.chapterid = chapterid;
    }

    public int getChapterorder() {
        return chapterorder;
    }

    public void setChapterorder(int chapterorder) {
        this.chapterorder = chapterorder;
    }
}
