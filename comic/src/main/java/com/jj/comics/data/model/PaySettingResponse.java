package com.jj.comics.data.model;

import java.util.List;

/**
 * 充值配置
 */
public class PaySettingResponse extends ResponseModel{


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 333378491205881860
         * title :
         * description : 充会会员送金币
         * price : 365.00
         * type : 2
         * goods_count : 3000
         * config : ["",""]
         */

        private long id;
        private String title;
        private String description;
        private String price;//价格
        private int type;
        private int goods_count;//'充值可得金币数量或会员天数
        private List<String> config;//描述

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getGoods_count() {
            return goods_count;
        }

        public void setGoods_count(int goods_count) {
            this.goods_count = goods_count;
        }

        public List<String> getConfig() {
            return config;
        }

        public void setConfig(List<String> config) {
            this.config = config;
        }
    }
}
