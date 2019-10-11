package com.jj.comics.data.model;

import java.util.List;

public class ConsumeDetailListResponse extends ResponseModel{

    /**
     * data : {"total_num":2,"data":[{"id":5550714,"articleid":15463,"chaptername":"第21章 他很想做爸爸","saleprice":20,"chapterorder":21,"sale_time":"2019-09-10 15:22:08"},{"id":5550713,"articleid":15463,"chaptername":"第20章 他，突然回来了","saleprice":20,"chapterorder":20,"sale_time":"2019-09-10 15:18:57"}]}
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
         * total_num : 2
         * data : [{"id":5550714,"articleid":15463,"chaptername":"第21章 他很想做爸爸","saleprice":20,"chapterorder":21,"sale_time":"2019-09-10 15:22:08"},{"id":5550713,"articleid":15463,"chaptername":"第20章 他，突然回来了","saleprice":20,"chapterorder":20,"sale_time":"2019-09-10 15:18:57"}]
         */

        private int total_num;
        private List<ConsumeDetail> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<ConsumeDetail> getData() {
            return data;
        }

        public void setData(List<ConsumeDetail> data) {
            this.data = data;
        }

        public static class ConsumeDetail {
            /**
             * id : 5550714
             * articleid : 15463
             * chaptername : 第21章 他很想做爸爸
             * saleprice : 20
             * chapterorder : 21
             * sale_time : 2019-09-10 15:22:08
             */

            private long id;
            private long articleid;
            private String chaptername;
            private String saleprice;
            private int chapterorder;
            private String sale_time;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public long getArticleid() {
                return articleid;
            }

            public void setArticleid(long articleid) {
                this.articleid = articleid;
            }

            public String getChaptername() {
                return chaptername;
            }

            public void setChaptername(String chaptername) {
                this.chaptername = chaptername;
            }

            public String getSaleprice() {
                return saleprice;
            }

            public void setSaleprice(String saleprice) {
                this.saleprice = saleprice;
            }

            public int getChapterorder() {
                return chapterorder;
            }

            public void setChapterorder(int chapterorder) {
                this.chapterorder = chapterorder;
            }

            public String getSale_time() {
                return sale_time;
            }

            public void setSale_time(String sale_time) {
                this.sale_time = sale_time;
            }
        }
    }
}
