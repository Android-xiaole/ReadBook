package com.jj.comics.data.model;

import java.util.List;

public class BoughtResponse extends ResponseModel {


    /**
     * data : {"total_num":3,"data":[{"cover":"http://ossmh.jj1699.cn/novel/盛世荣华之寒门毒妃/1567493420-cover_1567106547_副本.jpg","articleid":15450,"articlename":"盛世荣华之寒门毒妃","sale_time":"2019-09-16 18:21:32","sale_type":"第二十二章 觅得良师"},{"cover":"http://ossmh.jj1699.cn/novel/素女仙缘优婆璎珞/cover_1566196026.jpg","articleid":6364,"articlename":"素女仙缘","sale_time":"2019-09-12 17:08:15","sale_type":"第070章 、猪圈与老妇人"},{"cover":"http://ossmh.jj1699.cn/novel/倾城狂妃太逍遥/1567493074-cover_1567106631_副本.jpg","articleid":15460,"articlename":"倾城狂妃太逍遥","sale_time":"2019-09-12 16:47:53","sale_type":"第十一章  你也赏月啊"}]}
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
         * total_num : 3
         * data : [{"cover":"http://ossmh.jj1699.cn/novel/盛世荣华之寒门毒妃/1567493420-cover_1567106547_副本.jpg","articleid":15450,"articlename":"盛世荣华之寒门毒妃","sale_time":"2019-09-16 18:21:32","sale_type":"第二十二章 觅得良师"},{"cover":"http://ossmh.jj1699.cn/novel/素女仙缘优婆璎珞/cover_1566196026.jpg","articleid":6364,"articlename":"素女仙缘","sale_time":"2019-09-12 17:08:15","sale_type":"第070章 、猪圈与老妇人"},{"cover":"http://ossmh.jj1699.cn/novel/倾城狂妃太逍遥/1567493074-cover_1567106631_副本.jpg","articleid":15460,"articlename":"倾城狂妃太逍遥","sale_time":"2019-09-12 16:47:53","sale_type":"第十一章  你也赏月啊"}]
         */

        private int total_num;
        private List<BoughtModel> data;

        public int getTotal_num() {
            return total_num;
        }

        public void setTotal_num(int total_num) {
            this.total_num = total_num;
        }

        public List<BoughtModel> getData() {
            return data;
        }

        public void setData(List<BoughtModel> data) {
            this.data = data;
        }

        public static class BoughtModel {
            /**
             * cover : http://ossmh.jj1699.cn/novel/盛世荣华之寒门毒妃/1567493420-cover_1567106547_副本.jpg
             * articleid : 15450
             * articlename : 盛世荣华之寒门毒妃
             * sale_time : 2019-09-16 18:21:32
             * sale_type : 第二十二章 觅得良师
             */

            private String cover;
            private int articleid;
            private String articlename;
            private String sale_time;
            private String sale_type;

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public int getArticleid() {
                return articleid;
            }

            public void setArticleid(int articleid) {
                this.articleid = articleid;
            }

            public String getArticlename() {
                return articlename;
            }

            public void setArticlename(String articlename) {
                this.articlename = articlename;
            }

            public String getSale_time() {
                return sale_time;
            }

            public void setSale_time(String sale_time) {
                this.sale_time = sale_time;
            }

            public String getSale_type() {
                return sale_type;
            }

            public void setSale_type(String sale_type) {
                this.sale_type = sale_type;
            }
        }
    }
}
