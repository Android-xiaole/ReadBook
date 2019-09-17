package com.jj.comics.data.model;

import java.util.List;

public class ApprenticeListResponse extends ResponseModel {

    /**
     * data : {"data":[{"nickname":"晓丽?","avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/rnv8IxC5jl8Gb6dmLVa54WngXVQdIQTJBIxYVFsviceibzJQ65VAcqhy99xlKcClZBW7n1OsWQy6PosjUNsicP8gw/132"}],"total_num":2}
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
         * data : [{"nickname":"晓丽?","avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/rnv8IxC5jl8Gb6dmLVa54WngXVQdIQTJBIxYVFsviceibzJQ65VAcqhy99xlKcClZBW7n1OsWQy6PosjUNsicP8gw/132"}]
         * total_num : 2
         */

        private int total_num;
        private List<ApprenticeModel> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<ApprenticeModel> getData() {
            return data;
        }

        public void setData(List<ApprenticeModel> data) {
            this.data = data;
        }


    }
}
