package com.jj.comics.data.model;

import java.util.List;

public class NotificationListResponse extends ResponseModel {

    /**
     * data : {"total_num":5,"data":[{"id":329837071746564100,"update_time":"2019-09-06 14:21:36","title":"好书推荐"},{"id":329831140832935940,"update_time":"2019-09-06 13:58:02","title":"公告"},{"id":329461189966135300,"update_time":"2019-09-05 13:32:17","title":"公告"},{"id":329840825527009300,"update_time":"1970-01-01 08:00:00","title":"好书推荐2"},{"id":330903237053304800,"update_time":"1970-01-01 08:00:00","title":"公告"}]}
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
         * total_num : 5
         * data : [{"id":329837071746564100,"update_time":"2019-09-06 14:21:36","title":"好书推荐"},{"id":329831140832935940,"update_time":"2019-09-06 13:58:02","title":"公告"},{"id":329461189966135300,"update_time":"2019-09-05 13:32:17","title":"公告"},{"id":329840825527009300,"update_time":"1970-01-01 08:00:00","title":"好书推荐2"},{"id":330903237053304800,"update_time":"1970-01-01 08:00:00","title":"公告"}]
         */

        private int total_num;
        private List<SimpleNotificationDataBean> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<SimpleNotificationDataBean> getData() {
            return data;
        }

        public void setData(List<SimpleNotificationDataBean> data) {
            this.data = data;
        }

        public static class SimpleNotificationDataBean {
            /**
             * id : 329837071746564100
             * update_time : 2019-09-06 14:21:36
             * title : 好书推荐
             */

            private long id;
            private String update_time;
            private String title;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(String update_time) {
                this.update_time = update_time;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
