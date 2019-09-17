package com.jj.comics.data.model;

import java.util.List;

public class CashOutListResponse extends ResponseModel {

    /**
     * data : {"data":[{"id":11,"amount":"10.00","status":"已完成","create_time":"2019-09-12 14:29:51"}],"total_num":1}
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
         * data : [{"id":11,"amount":"10.00","status":"已完成","create_time":"2019-09-12 14:29:51"}]
         * total_num : 1
         */

        private int total_num;
        private List<CashOutModel> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<CashOutModel> getData() {
            return data;
        }

        public void setData(List<CashOutModel> data) {
            this.data = data;
        }

        public static class CashOutModel {
            /**
             * id : 11
             * amount : 10.00
             * status : 已完成
             * create_time : 2019-09-12 14:29:51
             */

            private long id;
            private String amount;
            private String status;
            private String create_time;

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

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }
        }
    }
}
