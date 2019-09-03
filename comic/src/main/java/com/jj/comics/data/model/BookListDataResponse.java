package com.jj.comics.data.model;

import com.jj.base.net.NetError;

import java.util.List;

/**
 * 返回书的列表的通用模型（多套了一层data）
 */
public class BookListDataResponse extends ResponseModel {

    @Override
    public NetError error() {
        if (code == 200) {
            if (data != null && data.getData() != null) {//这种情况才算请求成功，就是data有返回值
                return null;
            }
            if (data == null || data.getData() == null) {//返回成功但是没有数据
                return NetError.noDataError();
            }
        }
        return new NetError(message, code);
    }

    /**
     * code : 200
     * message : 请求成功
     * data : {"data":[{"book_id":267,"cover":"http://ossmh.jj1699.cn/images/若情似水/1559011092-fm.png","title":"若情似水","coverl":"http://ossmh.jj1699.cn/images/若情似水/1559011135-tt.png","coverl_index":"/public/images/default.png","cover_index":"/public/images/default.png","hot_const":"36万","intro":"                                                                                                                        和我谈婚论嫁的女友紫儿是个完美的人，她是所有男人梦寐以求的对象，但我卻无法信任她。这时，徬徨的我身边出现了一位同样名为紫儿的女子，她跟我的女友有著相同的名字，就好像二十岁时的紫儿回來了一样\u2026                                                                                                            ","fullflag":0,"lastvolume":"1"},{"book_id":266,"cover":"http://ossmh.jj1699.cn/images/肉色囚笼/肉色囚笼封面.png","title":"黑道女友","coverl":"http://ossmh.jj1699.cn/images/肉色囚笼/肉色囚笼横图.png","coverl_index":"/public/images/default.png","cover_index":"/public/images/default.png","hot_const":"539万","intro":"                                        我被困住了，还在不经意中与老大的女人...我该如何能够逃离出这个巢穴\u2026                                    ","fullflag":0,"lastvolume":"1"},{"book_id":418,"cover":"http://ossmh.jj1699.cn/images/总裁X宅女/1560916020-1556262774-总裁X宅女-cover.jpg","title":"总裁X宅女","coverl":"http://ossmh.jj1699.cn/images/总裁X宅女/1560916141-1556262777-总裁X宅女-poster.jpg","coverl_index":"","cover_index":"","hot_const":"5万","intro":"可爱宅女走出虚拟世界，邂逅总裁？？！！什么嘛，第一次见面竟然这么GAY！ 要不要这么刺激呀，我我我，我还是先溜了...\n","fullflag":1,"lastvolume":"1"}]}
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
         * total_num : 329
         * data : [{"book_id":267,"cover":"http://ossmh.jj1699.cn/images/若情似水/1559011092-fm.png","title":"若情似水","coverl":"http://ossmh.jj1699.cn/images/若情似水/1559011135-tt.png","coverl_index":"/public/images/default.png","cover_index":"/public/images/default.png","hot_const":"36万","intro":"                                                                                                                        和我谈婚论嫁的女友紫儿是个完美的人，她是所有男人梦寐以求的对象，但我卻无法信任她。这时，徬徨的我身边出现了一位同样名为紫儿的女子，她跟我的女友有著相同的名字，就好像二十岁时的紫儿回來了一样\u2026                                                                                                            ","fullflag":0,"lastvolume":"1"},{"book_id":266,"cover":"http://ossmh.jj1699.cn/images/肉色囚笼/肉色囚笼封面.png","title":"黑道女友","coverl":"http://ossmh.jj1699.cn/images/肉色囚笼/肉色囚笼横图.png","coverl_index":"/public/images/default.png","cover_index":"/public/images/default.png","hot_const":"539万","intro":"                                        我被困住了，还在不经意中与老大的女人...我该如何能够逃离出这个巢穴\u2026                                    ","fullflag":0,"lastvolume":"1"},{"book_id":418,"cover":"http://ossmh.jj1699.cn/images/总裁X宅女/1560916020-1556262774-总裁X宅女-cover.jpg","title":"总裁X宅女","coverl":"http://ossmh.jj1699.cn/images/总裁X宅女/1560916141-1556262777-总裁X宅女-poster.jpg","coverl_index":"","cover_index":"","hot_const":"5万","intro":"可爱宅女走出虚拟世界，邂逅总裁？？！！什么嘛，第一次见面竟然这么GAY！ 要不要这么刺激呀，我我我，我还是先溜了...\n","fullflag":1,"lastvolume":"1"}]
         */

        private List<BookModel> data;

        private long total_num;

        public long getTotal_num() {
            return total_num;
        }

        public void setTotal_num(long total_num) {
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
