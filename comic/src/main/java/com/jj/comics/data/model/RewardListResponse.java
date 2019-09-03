package com.jj.comics.data.model;

import java.util.List;

public class RewardListResponse extends ResponseModel {


    /**
     * data : {"data":[{"total":"732","username":"15056007765","uid":28547,"avatar":""}],"total_num":1,"total_bonus":732}
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
         * data : [{"total":"732","username":"15056007765","uid":28547,"avatar":""}]
         * total_num : 1
         * total_bonus : 732
         */

        private int total_num;
        private int total_bonus;
        private List<RewardRecordBean> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public int getTotal_bonus() {
            return total_bonus;
        }

        public void setTotal_bonus(int total_bonus) {
            this.total_bonus = total_bonus;
        }

        public List<RewardRecordBean> getData() {
            return data;
        }

        public void setData(List<RewardRecordBean> data) {
            this.data = data;
        }

        public static class RewardRecordBean {
            /**
             * total : 732
             * username : 15056007765
             * uid : 28547
             * avatar :
             */

            private String total;
            private String username;
            private long uid;
            private String avatar;

            public String getTotal() {
                return total;
            }

            public void setTotal(String total) {
                this.total = total;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public long getUid() {
                return uid;
            }

            public void setUid(long uid) {
                this.uid = uid;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }
        }
    }
}
