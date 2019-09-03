package com.jj.comics.data.model;

import com.jj.base.net.NetError;

/**
 * 章节内容
 */
public class BookCatalogContentResponse extends ResponseModel{


    @Override
    public NetError error() {
        if (code == 1000||code == 1002){
            if (data != null){//有数据
                return null;
            }else {//没有数据返回异常
                return NetError.noDataError();
            }
        }else{
            return new NetError(message,code);
        }
    }

    /**
     * code=1000 :查询成功
     * code=1001 :强制关注公众号
     * code=1002 :未购买
     * code=1003:内容不存在
     * message :
     * data : {"now":{"chapterorder":26,"saleprice":49,"content":["http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_1.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_2.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_3.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_4.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_5.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_6.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_7.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_8.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_9.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_10.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_11.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_12.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_13.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_14.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_15.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_16.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_17.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_18.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_19.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_20.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_21.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_22.jpg"]}}
     */



    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * chapterorder : 26
         * chapter_id : 7197
         * vip_discount : 0.7
         * saleprice : 49
         * book_id : 135
         * content : ["http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_1.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_2.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_3.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_4.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_5.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_6.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_7.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_8.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_9.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_10.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_11.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_12.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_13.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_14.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_15.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_16.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_17.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_18.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_19.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_20.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_21.jpg","http://ossmh.jj1699.cn/images/乖张学妹/乖张学妹_subs/book_135_chapter_26_22.jpg"]
         */

        private BookCatalogModel now;
        private BookCatalogModel last;
        private BookCatalogModel next;

        public BookCatalogModel getNow() {
            return now;
        }

        public void setNow(BookCatalogModel now) {
            this.now = now;
        }

        public BookCatalogModel getLast() {
            return last;
        }

        public void setLast(BookCatalogModel last) {
            this.last = last;
        }

        public BookCatalogModel getNext() {
            return next;
        }

        public void setNext(BookCatalogModel next) {
            this.next = next;
        }
    }
}
