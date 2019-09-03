package com.jj.comics.data.model;

import java.util.List;

public class BannerResponse extends ResponseModel {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * banner_type : 1
         * banner_title : 123
         * banner_url : www.wangyi.com
         * banner_articleid : null
         * banner_img_url : http://ossmh.jj1699.cn/dev_cartoon/banner/1564469197-nan.png
         */

        private int banner_type;
        private String banner_title;
        private String banner_url;
        private long banner_articleid;
        private String banner_img_url;

        public int getBanner_type() {
            return banner_type;
        }

        public void setBanner_type(int banner_type) {
            this.banner_type = banner_type;
        }

        public String getBanner_title() {
            return banner_title;
        }

        public void setBanner_title(String banner_title) {
            this.banner_title = banner_title;
        }

        public String getBanner_url() {
            return banner_url;
        }

        public void setBanner_url(String banner_url) {
            this.banner_url = banner_url;
        }

        public long getBanner_articleid() {
            return banner_articleid;
        }

        public void setBanner_articleid(long banner_articleid) {
            this.banner_articleid = banner_articleid;
        }

        public String getBanner_img_url() {
            return banner_img_url;
        }

        public void setBanner_img_url(String banner_img_url) {
            this.banner_img_url = banner_img_url;
        }
    }
}
