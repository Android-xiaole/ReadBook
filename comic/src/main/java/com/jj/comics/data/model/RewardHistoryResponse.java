package com.jj.comics.data.model;

import java.util.List;

public class RewardHistoryResponse extends ResponseModel {

    /**
     * data : {"list":[{"articleid":547,"name":"倒数七天","cover":"http://ossmh.jj1699.cn/images/倒数七天/1560911392-倒数七天-cover.jpg"}],"actvalue":"366","num":1}
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
         * list : [{"articleid":547,"name":"倒数七天","cover":"http://ossmh.jj1699.cn/images/倒数七天/1560911392-倒数七天-cover.jpg"}]
         * actvalue : 366
         * num : 1
         */

        private String actvalue;
        private int num;
        private List<RewardHistoryModel> list;

        public String getActvalue() {
            return actvalue;
        }

        public void setActvalue(String actvalue) {
            this.actvalue = actvalue;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public List<RewardHistoryModel> getList() {
            return list;
        }

        public void setList(List<RewardHistoryModel> list) {
            this.list = list;
        }

        public static class RewardHistoryModel {
            /**
             * articleid : 547
             * name : 倒数七天
             * cover : http://ossmh.jj1699.cn/images/倒数七天/1560911392-倒数七天-cover.jpg
             */

            private long articleid;
            private String name;
            private String cover;

            public long getArticleid() {
                return articleid;
            }

            public void setArticleid(long articleid) {
                this.articleid = articleid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }
        }
    }
}
