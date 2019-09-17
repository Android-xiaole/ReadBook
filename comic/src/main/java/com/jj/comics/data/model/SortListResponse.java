package com.jj.comics.data.model;

import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;

import java.util.List;

public class SortListResponse implements IModel {

    /**
     * code : 200
     * message : 请求成功
     * data : [{"title":"历史军事","id":8,"icon":1},{"title":"仙侠修真","id":9,"icon":1},{"title":"灵异鬼事","id":10,"icon":1},{"title":"玄幻奇幻","id":11,"icon":1},{"title":"都市生活","id":12,"icon":1},{"title":"游戏竞技","id":22,"icon":1},{"title":"武侠江湖","id":29,"icon":1},{"title":"科幻未来","id":38,"icon":1},{"title":"同人","id":39,"icon":1},{"title":"出版物","id":41,"icon":1}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    @Override
    public NetError error() {
        return null;
    }

    public static class DataBean {
        /**
         * title : 历史军事
         * id : 8
         * icon : 1
         */

        private String title;
        private long id;
        private String icon;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
