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
         * id : 7011
         * cover : http://ossmh.jj1699.cn/novel/万界登陆之全知全能天要下雨/cover_1566295657.jpg
         * title : 万界登陆之全知全能
         * intro : 一觉醒来，穿越了？这个世界的人竟然拥有登录万界的能力？但是文化断层，海贼、火影、死神之类的统统不知道？
         　　“纳尼？《火影忍者》世界中最强的势力是水之国雾忍村？”
         　　“最强忍术叫做三身术？”
         　　“已知最强水遁忍术是大瀑布之术？世界的最后大BOSS叫做山椒鱼半藏，是雨忍村的首领，被称作‘忍界半神’？”
         　　望着一个个所谓的价值连城的情报，火影迷雷洛风中凌乱……
         * author : 天要下雨
         * chapterid : 3129025
         */

        private int id;
        private String cover;
        private String title;
        private String intro;
        private String author;
        private int chapterid;

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

        public int getChapterid() {
            return chapterid;
        }

        public void setChapterid(int chapterid) {
            this.chapterid = chapterid;
        }
    }
}
