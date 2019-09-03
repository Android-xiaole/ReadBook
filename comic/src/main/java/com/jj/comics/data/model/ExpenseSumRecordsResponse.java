package com.jj.comics.data.model;

import java.util.List;

public class ExpenseSumRecordsResponse extends ResponseModel {


    /**
     * data : {"total_num":2,"data":[{"id":52,"sourceid":1,"articleid":232,"update_time":"2019-07-23","total_money":"272.00","articlename":"郑主任为何这样"},{"id":53,"sourceid":1,"articleid":167,"update_time":"2019-07-23","total_money":"68.00","articlename":"圣诞大冒险"}]}
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
         * data : [{"id":52,"sourceid":1,"articleid":232,"update_time":"2019-07-23","total_money":"272.00","articlename":"郑主任为何这样"},{"id":53,"sourceid":1,"articleid":167,"update_time":"2019-07-23","total_money":"68.00","articlename":"圣诞大冒险"}]
         */

        private int total_num;
        private List<ExpenseSumRecordModel> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<ExpenseSumRecordModel> getData() {
            return data;
        }

        public void setData(List<ExpenseSumRecordModel> data) {
            this.data = data;
        }


    }
}
