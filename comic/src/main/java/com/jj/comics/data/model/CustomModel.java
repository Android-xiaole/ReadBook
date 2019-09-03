package com.jj.comics.data.model;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 漫画展示图片帮助类
 */
public class CustomModel implements MultiItemEntity {
    //    private long id;
//    private long mainId;
//    private File file;
//    private String title;
    private String url;
    //    private int subSeq;
//    private int position;
//    private int bitmapWidth;
//    private int bitmapHeight;
//    private int index;
//    private int count;
    private int itemType;//用于设置多布局 0-常规布局 1-土豪上榜打赏布局
//    private boolean isLast;//标记是这一话的最后一张图片

    //    public CustomModel(long id, long mainId, File file, String title, int subSeq, String url, int position) {
//        this.id = id;
//        this.mainId = mainId;
//        this.file = file;
//        this.title = title;
//        this.subSeq = subSeq;
//        this.url = url;
//        this.position = position;
//    }
    public CustomModel(String url, int itemType) {
        this.url = url;
        this.itemType = itemType;
    }

//    public boolean isLast() {
//        return isLast;
//    }
//
//    public void setLast(boolean last) {
//        isLast = last;
//    }
//
//    public int getIndex() {
//        return index;
//    }
//
//    public void setIndex(int index) {
//        this.index = index;
//    }
//
//    public int getCount() {
//        return count;
//    }
//
//    public void setCount(int count) {
//        this.count = count;
//    }
//
//    public int getBitmapWidth() {
//        return bitmapWidth;
//    }
//
//    public void setBitmapWidth(int bitmapWidth) {
//        this.bitmapWidth = bitmapWidth;
//    }
//
//    public int getBitmapHeight() {
//        return bitmapHeight;
//    }
//
//    public void setBitmapHeight(int bitmapHeight) {
//        this.bitmapHeight = bitmapHeight;
//    }
//
//    public int getPosition() {
//        return position;
//    }
//
//    public void setPosition(int position) {
//        this.position = position;
//    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

//    public int getSubSeq() {
//        return subSeq;
//    }
//
//    public void setSubSeq(int subSeq) {
//        this.subSeq = subSeq;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public long getMainId() {
//        return mainId;
//    }
//
//    public void setMainId(long mainId) {
//        this.mainId = mainId;
//    }
//
//    public File getFile() {
//        return file;
//    }
//
//    public void setFile(File file) {
//        this.file = file;
//    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CustomModel && itemType != 1 && ((CustomModel) obj).itemType != 1) {
            return TextUtils.equals(((CustomModel) obj).getUrl(), url);
        }
        return super.equals(obj);
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return this.itemType;
    }
}
