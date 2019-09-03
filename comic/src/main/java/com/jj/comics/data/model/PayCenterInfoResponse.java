package com.jj.comics.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class PayCenterInfoResponse extends ResponseModel{


    private List<PayCenterInfo> data;

    public List<PayCenterInfo> getData() {
        return data;
    }

    public void setData(List<PayCenterInfo> data) {
        this.data = data;
    }

    public static class PayCenterInfo implements MultiItemEntity {
        /**
         * id : 323969860534312960
         * status : 1
         * price : 12.00
         * egold : 1200
         * giveprice : 213.00
         * giveegold : 21300
         * type : 2
         * sort : 1
         * description : 123
         * duration : 123
         * is_default : 0
         * vip_level : 1
         * discount : 123.00
         * everyday_gold : 1233
         * title : 123
         * theme_type : PAY_SETTING_APP
         */

        private long id;
        private int status;
        private String price;
        private int egold;
        private String giveprice;
        private int giveegold;
        private int type;
        private int sort;
        private String description;
        private int duration;
        private int is_default;
        private int vip_level;
        private String discount;
        private int everyday_gold;
        private String title;
        private String theme_type;
        private boolean isDouble = true;

        public boolean isDouble() {
            return isDouble;
        }

        public void setDouble(boolean aDouble) {
            isDouble = aDouble;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getEgold() {
            return egold;
        }

        public void setEgold(int egold) {
            this.egold = egold;
        }

        public String getGiveprice() {
            return giveprice;
        }

        public void setGiveprice(String giveprice) {
            this.giveprice = giveprice;
        }

        public int getGiveegold() {
            return giveegold;
        }

        public void setGiveegold(int giveegold) {
            this.giveegold = giveegold;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getIs_default() {
            return is_default;
        }

        public void setIs_default(int is_default) {
            this.is_default = is_default;
        }

        public int getVip_level() {
            return vip_level;
        }

        public void setVip_level(int vip_level) {
            this.vip_level = vip_level;
        }

        public String getDiscount() {
            return discount;
        }

        public void setDiscount(String discount) {
            this.discount = discount;
        }

        public int getEveryday_gold() {
            return everyday_gold;
        }

        public void setEveryday_gold(int everyday_gold) {
            this.everyday_gold = everyday_gold;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTheme_type() {
            return theme_type;
        }

        public void setTheme_type(String theme_type) {
            this.theme_type = theme_type;
        }

        @Override
        public int getItemType() {
            return type;
        }
    }
}
