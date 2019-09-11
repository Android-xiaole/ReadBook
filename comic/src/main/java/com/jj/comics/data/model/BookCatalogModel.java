package com.jj.comics.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 一本漫画的章节model
 */
public class BookCatalogModel implements Serializable {


    /**
     * id : 章节id
     * chaptername : 章节名
     * chapterorder:章节数
     * chaptercover : http://ossmh.jj1699.cn/images/chapterCover/1555579432-40.jpg
     * create_time : 2018-11-12 14:06:53
     * isvip : 0:免费;1:收费
     * saleprice : 所需金币
     * is_read : true:已读,false:未读
     * is_buy : true:已购买,false:未购买
     */

    @SerializedName(value = "id", alternate = {"chapter_id"})
    private long id;
    private long book_id;
    private String chaptername;
    private int chapterorder;
    private String chaptercover;
    private String create_time;
    private int isvip;
    private int saleprice;
    private boolean is_read;
    private boolean is_buy;
    private String content;//小说下载地址链接

    //下面两个个字段是获取章节内容接口获取的
    private float vip_discount;//折扣率 0.7

    //下面四个字段是获取章节内容接口通过返回值手动设置的
    private boolean hasLast;//是否有上一话
    private boolean hasNext;//是否有下一话
    private long lastChapterid;//上一话章节id
    private long nextChapterid;//下一话章节id

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getLastChapterid() {
        return lastChapterid;
    }

    public void setLastChapterid(long lastChapterid) {
        this.lastChapterid = lastChapterid;
    }

    public long getNextChapterid() {
        return nextChapterid;
    }

    public void setNextChapterid(long nextChapterid) {
        this.nextChapterid = nextChapterid;
    }

    public float getVip_discount() {
        return vip_discount;
    }

    public void setVip_discount(float vip_discount) {
        this.vip_discount = vip_discount;
    }

    public long getBook_id() {
        return book_id;
    }

    public void setBook_id(long book_id) {
        this.book_id = book_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getChapterorder() {
        return chapterorder;
    }

    public void setChapterorder(int chapterorder) {
        this.chapterorder = chapterorder;
    }

    public String getChaptername() {
        return chaptername;
    }

    public void setChaptername(String chaptername) {
        this.chaptername = chaptername;
    }

    public String getChaptercover() {
        return chaptercover;
    }

    public void setChaptercover(String chaptercover) {
        this.chaptercover = chaptercover;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getIsvip() {
        return isvip;
    }

    public void setIsvip(int isvip) {
        this.isvip = isvip;
    }

    public int getSaleprice() {
        return saleprice;
    }

    public void setSaleprice(int saleprice) {
        this.saleprice = saleprice;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public boolean isIs_buy() {
        return is_buy;
    }

    public void setIs_buy(boolean is_buy) {
        this.is_buy = is_buy;
    }

    public boolean isHasLast() {
        return hasLast;
    }

    public void setHasLast(boolean hasLast) {
        this.hasLast = hasLast;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
}
