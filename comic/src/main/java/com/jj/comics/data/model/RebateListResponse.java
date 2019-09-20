package com.jj.comics.data.model;


import java.util.List;

public class RebateListResponse extends ResponseModel {

    /**
     * data : {"total_num":1,"data":[{"id":4869893669664392,"amount":"0.03","balance":"0.00","profit_uid":4869828161392168,"profit_user":"18512132376","create_time":"2019-09-20 14:25:26","desc":"返利"}]}
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
         * total_num : 1
         * data : [{"id":4869893669664392,"amount":"0.03","balance":"0.00","profit_uid":4869828161392168,"profit_user":"18512132376","create_time":"2019-09-20 14:25:26","desc":"返利"}]
         */

        private int total_num;
        private List<RebateModel> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<RebateModel> getData() {
            return data;
        }

        public void setData(List<RebateModel> data) {
            this.data = data;
        }

        public static class RebateModel {
            /**
             * id : 4869893669664392
             * amount : 0.03
             * balance : 0.00
             * profit_uid : 4869828161392168
             * profit_user : 18512132376
             * create_time : 2019-09-20 14:25:26
             * desc : 返利
             */

            private long id;
            private String amount;
            private String balance;
            private long profit_uid;
            private String profit_user;
            private String create_time;
            private String desc;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getBalance() {
                return balance;
            }

            public void setBalance(String balance) {
                this.balance = balance;
            }

            public long getProfit_uid() {
                return profit_uid;
            }

            public void setProfit_uid(long profit_uid) {
                this.profit_uid = profit_uid;
            }

            public String getProfit_user() {
                return profit_user;
            }

            public void setProfit_user(String profit_user) {
                this.profit_user = profit_user;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }
        }
    }
}
