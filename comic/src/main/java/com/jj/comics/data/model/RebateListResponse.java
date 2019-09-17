package com.jj.comics.data.model;

import java.util.List;

public class RebateListResponse extends ResponseModel {

    /**
     * data : {"total_num":2,"data":[{"id":2,"amount":"1.00","balance":"50.00","profit_uid":28531,"profit_user":"1","create_time":"1970-01-01 15:55:31","desc":"返利"},{"id":1,"amount":"1.00","balance":"100.00","profit_uid":28531,"profit_user":"1","create_time":"1970-01-01 08:00:00","desc":"返利"}]}
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
         * total_num : 2
         * data : [{"id":2,"amount":"1.00","balance":"50.00","profit_uid":28531,"profit_user":"1","create_time":"1970-01-01 15:55:31","desc":"返利"},{"id":1,"amount":"1.00","balance":"100.00","profit_uid":28531,"profit_user":"1","create_time":"1970-01-01 08:00:00","desc":"返利"}]
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
             * id : 2
             * amount : 1.00
             * balance : 50.00
             * profit_uid : 28531
             * profit_user : 1
             * create_time : 1970-01-01 15:55:31
             * desc : 返利
             */

            private long id;
            private String amount;
            private String balance;
            private int profit_uid;
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

            public int getProfit_uid() {
                return profit_uid;
            }

            public void setProfit_uid(int profit_uid) {
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
