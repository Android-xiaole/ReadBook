package com.jj.comics.data.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;
import com.jj.comics.adapter.mine.VIPAdapter;

import java.util.List;

public class VIPListResponse extends ResponseModel{

    /**
     * code : 200
     * message : 请求成功
     * data : [{"duration":365,"list":[{"id":17,"price":"365.00","egold":36500,"giveprice":"100.00","giveegold":10000,"description":"年费会员+10000金币","duration":365,"vip_level":2},{"id":31,"price":"68.00","egold":6800,"giveprice":"0.00","giveegold":0,"description":"","duration":365,"vip_level":1}]},{"duration":90,"list":[{"id":30,"price":"180.00","egold":18000,"giveprice":"0.00","giveegold":0,"description":"","duration":90,"vip_level":2},{"id":32,"price":"18.80","egold":1800,"giveprice":"0.00","giveegold":0,"description":"","duration":90,"vip_level":1}]},{"duration":30,"list":[{"id":33,"price":"8.80","egold":800,"giveprice":"0.00","giveegold":0,"description":"","duration":30,"vip_level":1},{"id":36,"price":"88.00","egold":8800,"giveprice":"0.00","giveegold":0,"description":"","duration":30,"vip_level":2}]},{"duration":1,"list":[{"id":35,"price":"0.01","egold":88,"giveprice":"1.00","giveegold":33,"description":"","duration":1,"vip_level":1}]}]
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean extends AbstractExpandableItem<DataBean.ListBean> implements IModel, MultiItemEntity {
        /**
         * duration : 365
         * list : [{"id":17,"price":"365.00","egold":36500,"giveprice":"100.00","giveegold":10000,"description":"年费会员+10000金币","duration":365,"vip_level":2},{"id":31,"price":"68.00","egold":6800,"giveprice":"0.00","giveegold":0,"description":"","duration":365,"vip_level":1}]
         */

        private int duration;
        private List<ListBean> list;

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getItemType() {
            return VIPAdapter.TYPE_LEVEL_0;
        }

        @Override
        public NetError error() {
            return null;
        }

        public static class ListBean implements Comparable<ListBean>, MultiItemEntity{
            /**
             * id : 17
             * price : 365.00
             * egold : 36500
             * giveprice : 100.00
             * giveegold : 10000
             * description : 年费会员+10000金币
             * duration : 365
             * vip_level : 2
             */

            private long id;
            private String price;
            private int egold;
            private String giveprice;
            private int giveegold;
            private String description;
            private int duration;
            private int vip_level;

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

            @Override
            public int getItemType() {
                return 1;
            }

            @Override
            public int compareTo(ListBean o) {
                return 0;
            }
        }
    }
}
