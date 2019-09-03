package com.jj.comics.data.model;

import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;

import java.io.Serializable;
import java.util.List;

public class ShareParamModel implements IModel, Serializable {
    private int code;
    private String msg;
    private List<ShareParam> shareWays;

    @Override
    public NetError error() {
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ShareParam> getShareWays() {
        return shareWays;
    }

    public void setShareWays(List<ShareParam> shareWays) {
        this.shareWays = shareWays;
    }

    public class ShareParam {
        private String code;
        private String name;
        private String appId;
        private String appKey;
        private String appSecret;
        private String defaultImgUrl;
        private String defaultShareLinkHost;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public String getDefaultImgUrl() {
            return defaultImgUrl;
        }

        public void setDefaultImgUrl(String defaultImgUrl) {
            this.defaultImgUrl = defaultImgUrl;
        }

        public String getDefaultShareLinkHost() {
            return defaultShareLinkHost;
        }

        public void setDefaultShareLinkHost(String defaultShareLinkHost) {
            this.defaultShareLinkHost = defaultShareLinkHost;
        }
    }
}
