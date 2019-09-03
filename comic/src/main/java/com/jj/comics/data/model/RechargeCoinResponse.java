package com.jj.comics.data.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class RechargeCoinResponse extends ResponseModel{

    /**
     * code : 200
     * message : 请求成功
     * data : [{"price":"18.00","egold":1800,"giveprice":"2.00","giveegold":200,"description":"","duration":0,"vip_level":0},{"price":"30.00","egold":3000,"giveprice":"5.00","giveegold":500,"description":"","duration":0,"vip_level":0},{"price":"50.00","egold":5000,"giveprice":"15.00","giveegold":1500,"description":"","duration":0,"vip_level":0},{"price":"100.00","egold":10000,"giveprice":"40.00","giveegold":4000,"description":"","duration":0,"vip_level":0}]
     */
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements MultiItemEntity {
        /**
         * price : 18.00
         * egold : 1800
         * giveprice : 2.00
         * giveegold : 200
         * description :
         * duration : 0
         * vip_level : 0
         * isDouble 是否首充
         */
        private long id;
        private String price;
        private int egold;
        private String giveprice;
        private int giveegold;
        private String description;
        private String title;
        private int duration;
        private int vip_level;
        private boolean isDouble;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
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

        public int getVip_level() {
            return vip_level;
        }

        public void setVip_level(int vip_level) {
            this.vip_level = vip_level;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isDouble() {
            return isDouble;
        }

        public void setDouble(boolean aDouble) {
            isDouble = aDouble;
        }

        @Override
        public int getItemType() {
            if (vip_level == 2){
                return 1;
            }
            return 2;
        }
    }
}
