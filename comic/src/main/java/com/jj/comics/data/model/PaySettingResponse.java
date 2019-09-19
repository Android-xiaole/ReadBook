package com.jj.comics.data.model;

import java.util.List;

/**
 * 充值配置
 */
public class PaySettingResponse extends ResponseModel{


    /**
     * data : {"data":[{"id":334549600668291100,"title":null,"description":"安卓端充值一","price":"0.01","type":1,"goods_count":1,"config":["首充双倍"]},{"id":334553509579264000,"title":null,"description":"安卓金币充值2","price":"0.02","type":1,"goods_count":2,"config":[]}],"describe":"1. 1元=100书币\\r\\n2. 书币充值可叠加，无上限。\\r\\n3. 书币消耗完无法阅读书籍。\\r\\n4. 书币留存小于小说单章的消耗数时无法阅读书籍。\\r\\n5. vip用户阅读书籍不会消耗书币。"}
     */

    private DataBeanX data;

    public DataBeanX getData() {
        return data;
    }

    public void setData(DataBeanX data) {
        this.data = data;
    }

    public static class DataBeanX {
        /**
         * data : [{"id":334549600668291100,"title":null,"description":"安卓端充值一","price":"0.01","type":1,"goods_count":1,"config":["首充双倍"]},{"id":334553509579264000,"title":null,"description":"安卓金币充值2","price":"0.02","type":1,"goods_count":2,"config":[]}]
         * describe : 1. 1元=100书币\r\n2. 书币充值可叠加，无上限。\r\n3. 书币消耗完无法阅读书籍。\r\n4. 书币留存小于小说单章的消耗数时无法阅读书籍。\r\n5. vip用户阅读书籍不会消耗书币。
         */

        private String describe;
        private List<DataBean> data;

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * id : 334549600668291100
             * title : null
             * description : 安卓端充值一
             * price : 0.01
             * type : 1
             * goods_count : 1
             * config : ["首充双倍"]
             */

            private long id;
            private Object title;
            private String description;
            private String price;
            private int type;
            private int goods_count;
            private List<String> config;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public Object getTitle() {
                return title;
            }

            public void setTitle(Object title) {
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
}
