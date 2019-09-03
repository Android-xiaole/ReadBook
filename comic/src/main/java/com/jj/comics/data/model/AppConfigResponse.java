package com.jj.comics.data.model;


public class AppConfigResponse extends ResponseModel {
    private DataBean data;
    /**
     * data : {"login":{"weibo":{"app_id":"82040142","secret":"34ab042f0a1667118ec4383c316c476a"},"wechat":{"app_id":"wx852297c06ef6ba77","secret":"0662fca82ee497ac3482d93feb03c4d0"},"qq":{"app_id":"101571758","secret":"317e846799f51ed7279c5bad6c71826f"}},"pay":{"app_id":"wx852297c06ef6ba77","mch_id":"1526221031","key":"e1875a9cf3e9e6506496acaab66499ca","notify_url":"/api/wx_pay_notify_app/3"},"app_api_url":"http://cartoon-novel.txread.net","channelid":null}
     */

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * login : {"weibo":{"app_id":"82040142","secret":"34ab042f0a1667118ec4383c316c476a"},"wechat":{"app_id":"wx852297c06ef6ba77","secret":"0662fca82ee497ac3482d93feb03c4d0"},"qq":{"app_id":"101571758","secret":"317e846799f51ed7279c5bad6c71826f"}}
         * pay : {"app_id":"wx852297c06ef6ba77","mch_id":"1526221031","key":"e1875a9cf3e9e6506496acaab66499ca","notify_url":"/api/wx_pay_notify_app/3"}
         * app_api_url : http://cartoon-novel.txread.net
         * channelid : null
         */

        private LoginBean login;
        private PayBean pay;
        private String app_api_url;
        private long channelid;
        private String pay_info;

        public LoginBean getLogin() {
            return login;
        }

        public void setLogin(LoginBean login) {
            this.login = login;
        }

        public PayBean getPay() {
            return pay;
        }

        public void setPay(PayBean pay) {
            this.pay = pay;
        }

        public String getApp_api_url() {
            return app_api_url;
        }

        public void setApp_api_url(String app_api_url) {
            this.app_api_url = app_api_url;
        }

        public long getChannelid() {
            return channelid;
        }

        public void setChannelid(long channelid) {
            this.channelid = channelid;
        }

        public String getPay_info() {
            return pay_info;
        }

        public void setPay_info(String pay_info) {
            this.pay_info = pay_info;
        }

        public static class LoginBean {
            /**
             * weibo : {"app_id":"82040142","secret":"34ab042f0a1667118ec4383c316c476a"}
             * wechat : {"app_id":"wx852297c06ef6ba77","secret":"0662fca82ee497ac3482d93feb03c4d0"}
             * qq : {"app_id":"101571758","secret":"317e846799f51ed7279c5bad6c71826f"}
             */

            private WeiboBean weibo;
            private WechatBean wechat;
            private QqBean qq;

            public WeiboBean getWeibo() {
                return weibo;
            }

            public void setWeibo(WeiboBean weibo) {
                this.weibo = weibo;
            }

            public WechatBean getWechat() {
                return wechat;
            }

            public void setWechat(WechatBean wechat) {
                this.wechat = wechat;
            }

            public QqBean getQq() {
                return qq;
            }

            public void setQq(QqBean qq) {
                this.qq = qq;
            }

            public static class WeiboBean {
                /**
                 * app_id : 82040142
                 * secret : 34ab042f0a1667118ec4383c316c476a
                 */

                private String app_id;
                private String secret;

                public String getApp_id() {
                    return app_id;
                }

                public void setApp_id(String app_id) {
                    this.app_id = app_id;
                }

                public String getSecret() {
                    return secret;
                }

                public void setSecret(String secret) {
                    this.secret = secret;
                }
            }

            public static class WechatBean {
                /**
                 * app_id : wx852297c06ef6ba77
                 * secret : 0662fca82ee497ac3482d93feb03c4d0
                 */

                private String app_id;
                private String secret;

                public String getApp_id() {
                    return app_id;
                }

                public void setApp_id(String app_id) {
                    this.app_id = app_id;
                }

                public String getSecret() {
                    return secret;
                }

                public void setSecret(String secret) {
                    this.secret = secret;
                }
            }

            public static class QqBean {
                /**
                 * app_id : 101571758
                 * secret : 317e846799f51ed7279c5bad6c71826f
                 */

                private String app_id;
                private String secret;

                public String getApp_id() {
                    return app_id;
                }

                public void setApp_id(String app_id) {
                    this.app_id = app_id;
                }

                public String getSecret() {
                    return secret;
                }

                public void setSecret(String secret) {
                    this.secret = secret;
                }
            }
        }

        public static class PayBean {
            /**
             * app_id : wx852297c06ef6ba77
             * mch_id : 1526221031
             * key : e1875a9cf3e9e6506496acaab66499ca
             * notify_url : /api/wx_pay_notify_app/3
             */

            private String app_id;
            private String mch_id;
            private String key;
            private String notify_url;

            public String getApp_id() {
                return app_id;
            }

            public void setApp_id(String app_id) {
                this.app_id = app_id;
            }

            public String getMch_id() {
                return mch_id;
            }

            public void setMch_id(String mch_id) {
                this.mch_id = mch_id;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getNotify_url() {
                return notify_url;
            }

            public void setNotify_url(String notify_url) {
                this.notify_url = notify_url;
            }
        }
    }

}
