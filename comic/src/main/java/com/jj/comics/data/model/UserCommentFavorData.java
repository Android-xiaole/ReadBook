package com.jj.comics.data.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 本地保存的用户评论点赞信息
 */
@Entity
public class UserCommentFavorData {

    @Id(autoincrement = true)
    private Long id;//主键id
    private long userId;//uid
    private long commentId;//评论id
    private String content;//评论内容
    private long create_time;//点赞评论的时间
    private long update_time;//更新时间（用户可能点赞又后取消，重复此操作）
    private boolean isFavor;//是否点赞
    @Generated(hash = 1239855206)
    public UserCommentFavorData(Long id, long userId, long commentId,
            String content, long create_time, long update_time, boolean isFavor) {
        this.id = id;
        this.userId = userId;
        this.commentId = commentId;
        this.content = content;
        this.create_time = create_time;
        this.update_time = update_time;
        this.isFavor = isFavor;
    }
    @Generated(hash = 329124316)
    public UserCommentFavorData() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getUserId() {
        return this.userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }
    public long getCommentId() {
        return this.commentId;
    }
    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getCreate_time() {
        return this.create_time;
    }
    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }
    public long getUpdate_time() {
        return this.update_time;
    }
    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
    public boolean getIsFavor() {
        return this.isFavor;
    }
    public void setIsFavor(boolean isFavor) {
        this.isFavor = isFavor;
    }

    

}
