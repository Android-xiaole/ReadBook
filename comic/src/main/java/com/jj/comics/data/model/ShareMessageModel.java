package com.jj.comics.data.model;

public class ShareMessageModel {

    private String shareTitle;//分享标题
    private String bookTitle;//书籍标题
    private String author;//作者
    private String content;//作者
    private String type;//分类
    private String keys;//关键字
    private String shareContent;//分享内容
    private String shareUrl;//分享跳转链接
    private String shareImgUrl;//用户分享缩略图，只支持网络图片
    private int shareImgRes;//只支持本地图片，默认展示的分享图片
    private long boolId;//分享的小说ID


    public long getBoolId() {
        return boolId;
    }

    public void setBoolId(long boolId) {
        this.boolId = boolId;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getShareContent() {
        return shareContent;
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareImgUrl() {
        return shareImgUrl;
    }

    public void setShareImgUrl(String shareImgUrl) {
        this.shareImgUrl = shareImgUrl;
    }

    public int getShareImgRes() {
        return shareImgRes;
    }

    /**
     * 这个资源是本地图片，是默认分享的图片（假如外部没有设置或者各种异常才会加载此图片）
     *
     * @param shareImgRes
     */
    public void setShareImgRes(int shareImgRes) {
        this.shareImgRes = shareImgRes;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeys() {
        return keys;
    }

    public void setKeys(String keys) {
        this.keys = keys;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
