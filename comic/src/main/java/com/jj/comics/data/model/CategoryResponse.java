package com.jj.comics.data.model;

import com.jj.base.mvp.IModel;
import com.jj.base.net.NetError;

import java.util.List;

public class CategoryResponse implements IModel {

    /**
     * code : 200
     * message : 请求成功
     * data : [{"title":"恋爱","id":25},{"title":"伦理","id":26},{"title":"异能","id":27},{"title":"悬疑","id":29},{"title":"恐怖","id":30},{"title":"福利","id":34},{"title":"都市","id":35},{"title":"热血","id":36},{"title":"悬疑","id":37}]
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
         * title : 恋爱
         * id : 25
         */
        private String title;
        private long id;

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
    }
}
