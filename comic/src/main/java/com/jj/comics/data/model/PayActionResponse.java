package com.jj.comics.data.model;

public class PayActionResponse extends ResponseModel{


    /**
     * code : 200
     * message : success
     * data : {"theme":"PAY_SETTING_THEME_01","title":"充值30元赠送50元","is_alert":1,"payinfo":{"id":14,"status":1,"create_time":1542179119,"price":"0.01","egold":3000,"giveprice":"5.00","giveegold":500,"type":1,"sort":3,"description":"","duration":0,"is_default":0,"vip_level":0,"discount":"0.00","everyday_gold":0,"title":null,"theme_type":"PAY_SETTING_THEME_01","remark":null}}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * theme : PAY_SETTING_THEME_01
         * title : 充值30元赠送50元
         * is_alert : 1
         * payinfo : {"id":14,"status":1,"create_time":1542179119,"price":"0.01","egold":3000,"giveprice":"5.00","giveegold":500,"type":1,"sort":3,"description":"","duration":0,"is_default":0,"vip_level":0,"discount":"0.00","everyday_gold":0,"title":null,"theme_type":"PAY_SETTING_THEME_01","remark":null}
         */

        private String theme;
        private String title;
        private int is_alert;
        private int close_time;
        private PayinfoBean payinfo;

        public int getClose_time() {
            return close_time;
        }

        public void setClose_time(int close_time) {
            this.close_time = close_time;
        }

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getIs_alert() {
            return is_alert;
        }

        public void setIs_alert(int is_alert) {
            this.is_alert = is_alert;
        }

        public PayinfoBean getPayinfo() {
            return payinfo;
        }

        public void setPayinfo(PayinfoBean payinfo) {
            this.payinfo = payinfo;
        }

        public static class PayinfoBean {
            /**
             * id : 14
             * status : 1
             * create_time : 1542179119
             * price : 0.01
             * egold : 3000
             * giveprice : 5.00
             * giveegold : 500
             * type : 1
             * sort : 3
             * description :
             * duration : 0
             * is_default : 0
             * vip_level : 0
             * discount : 0.00
             * everyday_gold : 0
             * title : null
             * theme_type : PAY_SETTING_THEME_01
             * remark : null
             */

            private long id;
            private int status;
            private int create_time;
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
            private Object title;
            private String theme_type;
            private Object remark;

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

            public int getCreate_time() {
                return create_time;
            }

            public void setCreate_time(int create_time) {
                this.create_time = create_time;
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

            public Object getTitle() {
                return title;
            }

            public void setTitle(Object title) {
                this.title = title;
            }

            public String getTheme_type() {
                return theme_type;
            }

            public void setTheme_type(String theme_type) {
                this.theme_type = theme_type;
            }

            public Object getRemark() {
                return remark;
            }

            public void setRemark(Object remark) {
                this.remark = remark;
            }
        }
    }
}
