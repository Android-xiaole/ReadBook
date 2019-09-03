package com.jj.comics.data.model;

import java.util.List;

public class CollectionResponse extends ResponseModel{

    /**
     * code : 200
     * message : 执行成功
     * data : {"total_num":5,"data":[{"articleid":101,"category_id":0,"id":101,"cover":"http://ossmh.jj1699.cn/images/邂逅/1560835905-封面.jpg","lastvolume":"1","title":"邂逅","fullflag":1,"category_name":["恋爱","都市"],"read_chapter":1,"chapterid":1},{"articleid":232,"category_id":0,"id":232,"cover":"http://ossmh.jj1699.cn/images/colCover/1556183798-郑主任为何这样竖图.jpg","lastvolume":"1","title":"郑主任为何这样","fullflag":1,"category_name":["恋爱","都市"],"read_chapter":1,"chapterid":1},{"articleid":98,"category_id":0,"id":98,"cover":"http://ossmh.jj1699.cn/images/包养契约/1560772660-qyfm.jpg","lastvolume":"1","title":"包养契约","fullflag":1,"category_name":["恋爱","伦理"],"read_chapter":1,"chapterid":1},{"articleid":99,"category_id":0,"id":99,"cover":"http://ossmh.jj1699.cn/images/红色印记/红色印记0.jpg","lastvolume":"1","title":"红色印记","fullflag":1,"category_name":["伦理","悬疑"],"read_chapter":1,"chapterid":1},{"articleid":123,"category_id":0,"id":123,"cover":"http://ossmh.jj1699.cn/images/妇科男医师/妇科男医师 竖图2.jpg","lastvolume":"1","title":"圣手男医师","fullflag":1,"category_name":["恋爱","都市"],"read_chapter":1,"chapterid":1}]}
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
         * data : [{"articleid":101,"category_id":0,"id":101,"cover":"http://ossmh.jj1699.cn/images/邂逅/1560835905-封面.jpg","lastvolume":"1","title":"邂逅","fullflag":1,"category_name":["恋爱","都市"],"read_chapter":1,"chapterid":1},{"articleid":232,"category_id":0,"id":232,"cover":"http://ossmh.jj1699.cn/images/colCover/1556183798-郑主任为何这样竖图.jpg","lastvolume":"1","title":"郑主任为何这样","fullflag":1,"category_name":["恋爱","都市"],"read_chapter":1,"chapterid":1},{"articleid":98,"category_id":0,"id":98,"cover":"http://ossmh.jj1699.cn/images/包养契约/1560772660-qyfm.jpg","lastvolume":"1","title":"包养契约","fullflag":1,"category_name":["恋爱","伦理"],"read_chapter":1,"chapterid":1},{"articleid":99,"category_id":0,"id":99,"cover":"http://ossmh.jj1699.cn/images/红色印记/红色印记0.jpg","lastvolume":"1","title":"红色印记","fullflag":1,"category_name":["伦理","悬疑"],"read_chapter":1,"chapterid":1},{"articleid":123,"category_id":0,"id":123,"cover":"http://ossmh.jj1699.cn/images/妇科男医师/妇科男医师 竖图2.jpg","lastvolume":"1","title":"圣手男医师","fullflag":1,"category_name":["恋爱","都市"],"read_chapter":1,"chapterid":1}]
         */

        private int total_num;
        private List<BookModel> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<BookModel> getData() {
            return data;
        }

        public void setData(List<BookModel> data) {
            this.data = data;
        }
    }
}
