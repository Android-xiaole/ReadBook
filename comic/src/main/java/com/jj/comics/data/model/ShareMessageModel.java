package com.jj.comics.data.model;

public class ShareMessageModel {

    private String shareTitle;//分享标题
    private String shareContent;//分享内容
    private String shareUrl;//分享跳转链接
    private String shareImgUrl;//用户分享缩略图，只支持网络图片
    private int shareImgRes;//只支持本地图片，默认展示的分享图片
    private String umengPrarms;//友盟上传参数

    public ShareMessageModel() {
    }

    public ShareMessageModel(String shareTitle, String shareContent, String shareUrl, String shareImgUrl, int shareImgRes, String umengPrarms) {
        this.shareTitle = shareTitle;
        this.shareContent = shareContent;
        this.shareUrl = shareUrl;
        this.shareImgUrl = shareImgUrl;
        this.shareImgRes = shareImgRes;
        this.umengPrarms = umengPrarms;
    }

    public void setUmengPrarms(String umengPrarms) {
        this.umengPrarms = umengPrarms;
    }

    public String getUmengPrarms() {
        return umengPrarms;
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
}
