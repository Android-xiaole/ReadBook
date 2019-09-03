package com.jj.comics.data.model;

public class ShareMenuModel {

    private int shareIcon;
    private ShareMenuTypeEnum type;

    public ShareMenuModel(int shareIcon, ShareMenuTypeEnum type) {
        this.shareIcon = shareIcon;
        this.type = type;
    }

    public int getShareIcon() {
        return shareIcon;
    }

    public void setShareIcon(int shareIcon) {
        this.shareIcon = shareIcon;
    }


    public ShareMenuTypeEnum getType() {
        return type;
    }

    public void setType(ShareMenuTypeEnum type) {
        this.type = type;
    }

    public enum ShareMenuTypeEnum{
        WECHAT("微信"),WECHATMOMENT("朋友圈"),
        QQ("QQ"),QQZONE("QQ空间"),
        SINA("微博"),
        COPYLINK("复制链接"),REPORT("举报");

        private String shareTypeValue;
        ShareMenuTypeEnum(String shareTypeValue){
            this.shareTypeValue = shareTypeValue;
        }

        public String getShareTypeValue() {
            return shareTypeValue;
        }
    }
}
