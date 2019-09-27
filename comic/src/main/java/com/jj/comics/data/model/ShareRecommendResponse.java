package com.jj.comics.data.model;

import java.util.List;

public class ShareRecommendResponse extends ResponseModel {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 6140
         * cover : http://ossmh.jj1699.cn/novel/诡娇娘墨白秋/cover_1566979086.jpg
         * title : 诡娇娘11
         * intro : 一次去见丈母娘的经历，让我陷入了一个个谜题之中，从此，无限的迷云将我笼罩。
         * author : 墨白秋
         * keywords : 凡人流,恐怖,惊悚,鬼夫,机智,鬼怪,现代
         * tag : ["灵异鬼事"]
         * chapterid : 1957235
         */

        private int id;
        private String cover;
        private String title;
        private String intro;
        private String author;
        private String keywords;
        private int chapterid;
        private List<String> tag;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public int getChapterid() {
            return chapterid;
        }

        public void setChapterid(int chapterid) {
            this.chapterid = chapterid;
        }

        public List<String> getTag() {
            return tag;
        }

        public void setTag(List<String> tag) {
            this.tag = tag;
        }
    }
}
