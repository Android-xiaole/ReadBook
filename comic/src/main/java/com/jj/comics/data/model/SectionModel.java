package com.jj.comics.data.model;

import java.util.List;

public class SectionModel {
    private int sectionId;
    private String name;
    private String style;
    private String image1;
    private List<BookModel> mContentList;

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BookModel> getContentList() {
        return mContentList;
    }

    public void setContentList(List<BookModel> contentList) {
        mContentList = contentList;
    }
}
