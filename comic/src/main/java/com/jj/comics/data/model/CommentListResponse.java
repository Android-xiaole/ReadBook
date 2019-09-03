package com.jj.comics.data.model;

import java.util.List;

/**
 * 用户评论列表
 */
public class CommentListResponse extends ResponseModel {


    /**
     * data : {"total_num":1,"data":[{"avatar":"","nickname":"15056007765","username":"app19071118322741263","content":"test","goodnum":0,"create_time":"07/22","id":4848706212263424}]}
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
         * data : [{"avatar":"","nickname":"15056007765","username":"app19071118322741263","content":"test","goodnum":0,"create_time":"07/22","id":4848706212263424}]
         */

        private int total_num;
        private List<DataBean> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * avatar :
             * nickname : 15056007765
             * username : app19071118322741263
             * content : test
             * goodnum : 0
             * create_time : 07/22
             * id : 4848706212263424
             */

            private String avatar;
            private String nickname;
            private String username;
            private String content;
            private int goodnum;
            private String create_time;
            private long id;

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getGoodnum() {
                return goodnum;
            }

            public void setGoodnum(int goodnum) {
                this.goodnum = goodnum;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }
        }
    }
}
